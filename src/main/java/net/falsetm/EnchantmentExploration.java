package net.falsetm;

import net.fabricmc.api.ModInitializer;

import net.falsetm.config.EnchantmentExplorationConfig;
import net.falsetm.events.*;
import net.falsetm.mixin.EnchantmentScreenHandlerAccessor;
import net.falsetm.mixin_ducks.EnchantmentHandlerDuck;
import net.falsetm.mixin_ducks.LootTableDuck;
import net.falsetm.util.EnchantmentHelper;
import net.minecraft.block.Blocks;
import net.minecraft.block.EnchantingTableBlock;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChiseledBookshelfBlockEntity;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.IndexedIterable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class EnchantmentExploration implements ModInitializer {
	public static final String MOD_ID = "enchantment-exploration";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static final Identifier mixerID = Identifier.of(MOD_ID,"mixer");
	private static final Identifier all_enchantsID = Identifier.of(MOD_ID,"all_enchants");

	private static final String enchantmentAllowedString = "enchantment-exploration-allowed";

	private static EnchantmentExplorationConfig config;

	public static EnchantmentExplorationConfig getConfig()
	{
		return config;
	}

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		loadConfig();

		//makes the enchantment power provider chiseled bookshelves, on toggle
		IsAccessPowerProviderCallback.EVENT.register((state, key) -> {
			if(config.isEnabled()){
                return state.getBlock() == Blocks.CHISELED_BOOKSHELF;
            }
			return null;
		});

		EnchantmentContentChangedCallback.EVENT.register((receiver, stack, world, pos) -> {
			if(config.isEnabled() && world instanceof ServerWorld serverWorld){
				Map<RegistryEntry<Enchantment>, Integer> enchantmentLevelMap = new HashMap<>();
				for (BlockPos offset : EnchantingTableBlock.POWER_PROVIDER_OFFSETS) {
					//if we can use the block
					if (EnchantingTableBlock.canAccessPowerProvider(world, pos, offset)) {
						BlockPos shelfPos = new BlockPos(pos).add(offset);
						if(world.getBlockState(shelfPos).getBlock() == Blocks.CHISELED_BOOKSHELF){
							ChiseledBookshelfBlockEntity bookshelfBlockEntity = serverWorld.getBlockEntity(shelfPos, BlockEntityType.CHISELED_BOOKSHELF).orElse(null);
							if(bookshelfBlockEntity != null){
								for(int i = 0; i < ChiseledBookshelfBlockEntity.MAX_BOOKS; i++){
									ItemStack bookItem = bookshelfBlockEntity.getStack(i);
									if(bookItem.isOf(Items.ENCHANTED_BOOK)){
										ItemEnchantmentsComponent enchantmentComponent = net.minecraft.enchantment.EnchantmentHelper.getEnchantments(bookItem);
										if(enchantmentComponent != null){
											for(RegistryEntry<Enchantment> enchantment : enchantmentComponent.getEnchantments()){
												Integer oldLevel = enchantmentLevelMap.getOrDefault(enchantment, null);
												int newLevel = enchantmentComponent.getLevel(enchantment);
												if(oldLevel == null || oldLevel < newLevel){
													enchantmentLevelMap.put(enchantment, newLevel);
												}
											}
										}
									}
								}
							}
						}
					}
				}

				List<EnchantmentLevelEntry> possibleEnchants = new ArrayList<>();
				for(var entry : enchantmentLevelMap.entrySet()){
					possibleEnchants.add(new EnchantmentLevelEntry(entry.getKey(), entry.getValue()));
				}
				possibleEnchants = EnchantmentHelper.legalEnchantments(stack, possibleEnchants);

				((EnchantmentHandlerDuck)receiver).falsetm$SetPossibleEnchants(possibleEnchants);

				if(!possibleEnchants.isEmpty()){
					Registry<Enchantment> registryManager = world.getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT);
					IndexedIterable<RegistryEntry<Enchantment>> indexedIterable = registryManager.getIndexedEntries();

					Random random = ((EnchantmentScreenHandlerAccessor)receiver).getRandom();
					random.setSeed(receiver.getSeed());

					EnchantmentLevelEntry selected = possibleEnchants.get(random.nextInt(possibleEnchants.size()));

					//bump up now if we should show it
					float roll = random.nextFloat();
					int selectedEnchantsLevel = selected.level;
					if(config.shouldShowBumpUp() && roll <= config.getBumpUpChance() && selectedEnchantsLevel < selected.enchantment.value().getMaxLevel()){
						selected = new EnchantmentLevelEntry(selected.enchantment, selected.level+1);
					}

					//set the enchantment. Edit this to make it use text for display
					receiver.enchantmentPower[0] = config.getCost0();
					receiver.enchantmentId[0] = indexedIterable.getRawId(selected.enchantment);
					receiver.enchantmentLevel[0] = selected.level;

					int mixerRealID = -1;
					Optional<RegistryEntry.Reference<Enchantment>> mixer = registryManager.getEntry(mixerID);
					if(mixer.isPresent()){
						mixerRealID = indexedIterable.getRawId(mixer.get());
					}
					int allRealID = -1;
					Optional<RegistryEntry.Reference<Enchantment>> all = registryManager.getEntry(all_enchantsID);
					if(all.isPresent()){
						allRealID = indexedIterable.getRawId(all.get());
					}
					receiver.enchantmentPower[1] = config.getCost1();
					receiver.enchantmentId[1] = mixerRealID;
					receiver.enchantmentLevel[1] = 1;
					receiver.enchantmentPower[2] = config.getCost2();
					receiver.enchantmentId[2] = allRealID;
					receiver.enchantmentLevel[2] = 1;

					receiver.sendContentUpdates();
				}
				else{
					//set to nothing if no possible enchantments
					for (int i = 0; i < 3; i++) {
						receiver.enchantmentPower[i] = 0;
						receiver.enchantmentId[i] = -1;
						receiver.enchantmentLevel[i] = -1;
					}
				}

				return ActionResult.FAIL;
			}
			return ActionResult.PASS;
		});

		GenerateEnchantCallback.EVENT.register((receiver, registryManager, stack, slot, level) -> {
			if(config.isEnabled()){
				List<EnchantmentLevelEntry> possibleEnchants = ((EnchantmentHandlerDuck)receiver).falsetm$GetPossibleEnchants();
				List<EnchantmentLevelEntry> returnEnchants = new ArrayList<>();

				Random random = ((EnchantmentScreenHandlerAccessor)receiver).getRandom();
				random.setSeed(receiver.getSeed() + slot);

				if(possibleEnchants != null && !possibleEnchants.isEmpty()){
					//top slot (SINGLE MAX LEVEL ENCHANT)
					if(slot == 0){
						Optional<Registry<Enchantment>> optional = registryManager.getOptional(RegistryKeys.ENCHANTMENT);
						if(optional.isPresent()){
							Optional<RegistryEntry.Reference<Enchantment>> enchantment = optional.get().getEntry(receiver.enchantmentId[0]);
							if(enchantment.isPresent()){

								Enchantment selectedEnchantment = enchantment.get().value();
								float roll = random.nextFloat();
								int selectedEnchantsLevel = receiver.enchantmentLevel[0];
								if(!config.shouldShowBumpUp() && roll <= config.getBumpUpChance() && selectedEnchantsLevel < selectedEnchantment.getMaxLevel()){
									selectedEnchantsLevel++;
								}
								EnchantmentLevelEntry outputEnchantmentEntry = new EnchantmentLevelEntry(enchantment.get(), selectedEnchantsLevel);
								returnEnchants.add(outputEnchantmentEntry);
							}
						}
					}
					//middle slot (MIXED UP)
					else if(slot == 1){
						int originalSize = possibleEnchants.size();
						for(int i = 0; i < originalSize; i++) {
							int randomIndex = random.nextInt(possibleEnchants.size());
							EnchantmentLevelEntry entry = possibleEnchants.get(randomIndex);
							possibleEnchants.remove(randomIndex);

							boolean compatibleWithAll = net.minecraft.enchantment.EnchantmentHelper.isCompatible(returnEnchants.stream().map(e->e.enchantment).collect(Collectors.toList()), entry.enchantment);

							if(compatibleWithAll){
								float roll = random.nextFloat();
								if(returnEnchants.isEmpty() || roll <= config.getAdditionalEnchantmentChance()){
									int curLevel = entry.level;

									if(curLevel > 1){
										roll = random.nextFloat();
										if(roll <= config.getBumpDownChance()){
											curLevel--;
										}
									}
									returnEnchants.add(new EnchantmentLevelEntry(entry.enchantment, curLevel));
								}
							}
						}
					}
					//bottom slot (ALL ENCHANTS LOWER LEVEL)
					else{
						int originalSize = possibleEnchants.size();
						for(int i = 0; i < originalSize; i++){
							int randomIndex = random.nextInt(possibleEnchants.size());
							EnchantmentLevelEntry entry = possibleEnchants.get(randomIndex);
							possibleEnchants.remove(randomIndex);

							boolean compatibleWithAll = net.minecraft.enchantment.EnchantmentHelper.isCompatible(returnEnchants.stream().map(e->e.enchantment).collect(Collectors.toList()), entry.enchantment);
							if(compatibleWithAll){
								int curLevel = entry.level;
								if(curLevel > 1){
									returnEnchants.add(new EnchantmentLevelEntry(entry.enchantment, curLevel-1));
								}
								else{
									returnEnchants.add(entry);
								}
							}
						}
					}
				}
				return returnEnchants;
			}
			return null;
		});

		EnchantmentScreenHandlerApplyCostCallback.EVENT.register((receiver, itemStack, inputLevels) -> {
			if(config.isEnabled()){
				int selected = inputLevels-1;
				if(selected < receiver.enchantmentPower.length){
					return receiver.enchantmentPower[selected];
				}
			}
			return inputLevels;
		});

		AnvilScreenHandlerUpdateResultCallback.EVENT.register((receiver -> {
			if(config.isEnabled()){
				ItemStack input1 = receiver.getSlot(AnvilScreenHandler.INPUT_1_ID).getStack();
				ItemStack input2 = receiver.getSlot(AnvilScreenHandler.INPUT_2_ID).getStack();
				boolean cancelBook = false;
				if(input2.isOf(Items.ENCHANTED_BOOK)){
					cancelBook = true;
					if((input1.isOf(Items.ENCHANTED_BOOK) && config.shouldAnvilCombineBookUpgrade())){
						cancelBook = false;
						ItemEnchantmentsComponent enchants1 = net.minecraft.enchantment.EnchantmentHelper.getEnchantments(input1);
						ItemEnchantmentsComponent enchants2 = net.minecraft.enchantment.EnchantmentHelper.getEnchantments(input2);
						for(var enchantment : enchants1.getEnchantments()){
							if(!enchants2.getEnchantments().contains(enchantment) || enchants1.getLevel(enchantment) != enchants2.getLevel(enchantment)){
								cancelBook = true;
								break;
							}
						}
					}
				}
				if((input2.isDamageable() && config.shouldRemoveToolAnvilCombination()) || (config.shouldRemoveBookAnvilCombination() && cancelBook)){
					receiver.setStackInSlot(AnvilScreenHandler.OUTPUT_ID, receiver.nextRevision(), ItemStack.EMPTY);
					//receiver.levelCost.set(0);
					return ActionResult.FAIL;
				}
			}
			return ActionResult.PASS;
		}));

		AnvilScreenUpdateResultGetSecondInputCallback.EVENT.register((receiver, inventory) -> {
			if(config.isEnabled()){
				ItemStack book = inventory.getStack(0);
				if(book.isOf(Items.ENCHANTED_BOOK)){
					ItemStack input2 = inventory.getStack(1);
					String itemID = Registries.ITEM.getId(input2.getItem()).toString();
					if(config.getAnvilBookUpgradeItems().contains(itemID)){
						ItemEnchantmentsComponent enchantmentsComponent = net.minecraft.enchantment.EnchantmentHelper.getEnchantments(book);
						if(!enchantmentsComponent.getEnchantments().isEmpty()){
							ItemStack returnStack = Items.ENCHANTED_BOOK.getDefaultStack();
							net.minecraft.enchantment.EnchantmentHelper.set(returnStack, enchantmentsComponent);
							return returnStack;
						}
					}
				}
			}
			return null;
		});

		//If you are a developer and are having an issue with compatibility because of this. leave an issue. I'll do something better, but ATM I don't want to deal with this.
		//patching removing the second stack on repair
		AnvilScreenTakeOutputNoRepairClearSecondCallback.EVENT.register((receiver, inventory) -> {
			if(config.isEnabled()){
				ItemStack input2 = inventory.getStack(1);
				if(!input2.isEmpty() && input2.getCount() > 1){
					input2.decrement(1);
					inventory.setStack(1, input2);
				}
				else{
					inventory.setStack(1, ItemStack.EMPTY);
				}
				return ActionResult.FAIL;
			}
			return ActionResult.PASS;
		});

		ItemStackCanRepairWithCallback.EVENT.register((receiver, repairStack) -> {
			if(config.isEnabled()){
				var repair = config.getRepairItem(Registries.ITEM.getId(repairStack.getItem()).toString());
				if(repair != null && repair.containsItem(Registries.ITEM.getId(receiver.getItem()).toString())){
					return true;
				}
				if(!config.doDefaultRepairMaterialsWork()){
					return false;
				}
			}
			return null;
		});

		//I should redo this function to instead of passing the operation, to return a consumer which will get added to the stack, but seeing as how I'm not using this function twice ATM and that's extra work I'm leaving as is for now
		LootTableApplyFunctionsCallback.EVENT.register((receiver, itemApplier, lootConsumer, context, original) -> {
			if(config.isEnabled()){
				return original.call(itemApplier, generateLootAfterFunctions(receiver, lootConsumer, context), context);
			}
			return null;
		});

		LootTableFinishGenerateUnprocessedCallback.EVENT.register((receiver, context, lootConsumer) -> {
			if(config.isEnabled()){
				for (var entry : config.getLootTableBookPulls().entrySet()) {

					ReloadableRegistries.Lookup lookup = context.getWorld().getServer().getReloadableRegistries();

					Identifier table1ID = Identifier.of(entry.getKey());
					var attempted = lookup.getLootTable(RegistryKey.of(RegistryKeys.LOOT_TABLE, table1ID));

					if (receiver.equals(attempted)) {
						Identifier table2ID = Identifier.of(entry.getValue());
						var secondaryTable = lookup.getLootTable(RegistryKey.of(RegistryKeys.LOOT_TABLE, table2ID));
						if(secondaryTable != null){
							((LootTableDuck) secondaryTable).falsetm$skipMixin();
							secondaryTable.generateUnprocessedLoot(context, lootConsumer);
						}
						break;
					}
				}
			}
			return ActionResult.PASS;
		});
	}

	public static Consumer<ItemStack> generateLootAfterFunctions(LootTable receiver, Consumer<ItemStack> lootConsumer, LootContext context) {
		return (itemStack) -> {
			if(itemStack.isOf(Items.ENCHANTED_BOOK)) {
				boolean ignore = false;
				for (String entry : config.getIgnoreTables()) {

					Identifier id = Identifier.of(entry);
					var attempted = context.getWorld().getServer().getReloadableRegistries().getLootTable(RegistryKey.of(RegistryKeys.LOOT_TABLE, id));

					if (receiver.equals(attempted)) {
						ignore = true;
						break;
					}
				}
				if (!ignore) {
					ItemStack itemEnchanted = itemStack.copy();
					ItemEnchantmentsComponent enchantmentComponent = net.minecraft.enchantment.EnchantmentHelper.getEnchantments(itemEnchanted);

					net.minecraft.enchantment.EnchantmentHelper.apply(itemStack, components -> components.remove(removeEnchant -> true));
					for (RegistryEntry<Enchantment> enchantment : enchantmentComponent.getEnchantments()) {
						if (!config.getSkipEnchantments().contains(enchantment.getIdAsString())) {
							itemStack.addEnchantment(enchantment, enchantmentComponent.getLevel(enchantment));
						}
					}
					if (net.minecraft.enchantment.EnchantmentHelper.getEnchantments(itemStack).getEnchantments().isEmpty()) {
						itemStack = ItemStack.EMPTY;
					}
				}
			}
			lootConsumer.accept(itemStack);
		};
	}

	private static void loadConfig() {
		Path configFile = EnchantmentExplorationConfig.CONFIG_FILE;
		if (Files.exists(configFile)) {
			try(BufferedReader reader = Files.newBufferedReader(configFile)) {
				config = EnchantmentExplorationConfig.fromJson(reader);
			} catch (Exception e) {
				LOGGER.error("Error loading Enchantment Exploration config file. Default values will be used for this session.", e);
				config = new EnchantmentExplorationConfig();
			}
		} else {
			config = new EnchantmentExplorationConfig();
		}

		// Immedietly save config to file to update any fields that may have changed.
		config.saveAsync();
	}
}