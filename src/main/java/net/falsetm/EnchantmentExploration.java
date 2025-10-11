package net.falsetm;

import net.fabricmc.api.ModInitializer;

import net.falsetm.config.EnchantmentExplorationConfig;
import net.falsetm.events.GenerateEnchantCallback;
import net.falsetm.events.isAccessPowerProviderCallback;
import net.falsetm.events.EnchantmentContentChangedCallback;
import net.falsetm.mixin.EnchantmentScreenHandlerAccessor;
import net.falsetm.mixin_ducks.EnchantmentHandlerDuck;
import net.minecraft.block.Blocks;
import net.minecraft.block.EnchantingTableBlock;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChiseledBookshelfBlockEntity;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
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
import java.util.stream.Collectors;

public class EnchantmentExploration implements ModInitializer {
	public static final String MOD_ID = "enchantment-exploration";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static final Identifier mixerID = Identifier.of("enchantment-exploration","mixer");
	private static final Identifier all_enchantsID = Identifier.of("enchantment-exploration","all_enchants");

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
		isAccessPowerProviderCallback.EVENT.register((state, key) -> {
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
									if(bookItem.getItem() == Items.ENCHANTED_BOOK){
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
				possibleEnchants = net.falsetm.EnchantmentHelper.legalEnchantments(stack, possibleEnchants);

				((EnchantmentHandlerDuck)receiver).falsetm$SetPossibleEnchants(possibleEnchants);

				if(!possibleEnchants.isEmpty()){
					Registry<Enchantment> registryManager = world.getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT);
					IndexedIterable<RegistryEntry<Enchantment>> indexedIterable = registryManager.getIndexedEntries();

					Random random = ((EnchantmentScreenHandlerAccessor)receiver).getRandom();
					random.setSeed(receiver.getSeed());

					EnchantmentLevelEntry selected = possibleEnchants.get(random.nextInt(possibleEnchants.size()));

					//set the enchantment. Edit this to make it use text for display
					receiver.enchantmentPower[0] = 10;
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
					receiver.enchantmentPower[1] = 10;
					receiver.enchantmentId[1] = mixerRealID;
					receiver.enchantmentLevel[1] = 1;
					receiver.enchantmentPower[2] = 10;
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
								int roll = random.nextInt(5);
								int selectedEnchantsLevel = receiver.enchantmentLevel[0];
								if(roll == 0 && selectedEnchantsLevel < selectedEnchantment.getMaxLevel()){
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
								int roll = random.nextInt(2);
								if(returnEnchants.isEmpty() || roll == 0){
									int curLevel = entry.level;

									if(curLevel > 1){
										roll = random.nextInt(5);
										if(roll < 2){
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