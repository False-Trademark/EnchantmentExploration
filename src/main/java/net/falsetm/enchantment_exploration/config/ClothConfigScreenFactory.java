package net.falsetm.enchantment_exploration.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.falsetm.enchantment_exploration.EnchantmentExploration;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ClothConfigScreenFactory {
    public static Screen makeConfig(Screen parent) {
        EnchantmentExplorationConfig defaultConfig = new EnchantmentExplorationConfig();

        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.translatable("title.enchantment-exploration.config"))
                .setSavingRunnable(EnchantmentExploration.getConfig()::saveAsync);
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        ConfigCategory main = builder.getOrCreateCategory(Text.translatable("category.enchantment-exploration.main"));
        ConfigCategory anvil = builder.getOrCreateCategory(Text.translatable("category.enchantment-exploration.anvil"));
        ConfigCategory lootTables = builder.getOrCreateCategory(Text.translatable("category.enchantment-exploration.loot-table"));

        main.addEntry(entryBuilder.startBooleanToggle(Text.translatable("option.enchantment-exploration.main.enabled"), EnchantmentExploration.getConfig().isEnabled())
                .setDefaultValue(defaultConfig.isEnabled())
                .setTooltip(Text.translatable("tooltip.enchantment-exploration.main.enabled"))
                .setSaveConsumer(newValue -> EnchantmentExploration.getConfig().setEnabled(newValue))
                .build());
        main.addEntry(entryBuilder.startBooleanToggle(Text.translatable("option.enchantment-exploration.show-bump"), EnchantmentExploration.getConfig().shouldShowBumpUp())
                .setDefaultValue(defaultConfig.shouldShowBumpUp())
                .setTooltip(Text.translatable("tooltip.enchantment-exploration.show-bump"))
                .setSaveConsumer(newValue -> EnchantmentExploration.getConfig().setShowBumpUp(newValue))
                .build());
        main.addEntry(entryBuilder.startFloatField(Text.translatable("option.enchantment-exploration.chance-up"), EnchantmentExploration.getConfig().getBumpUpChance())
                .setDefaultValue(defaultConfig.getBumpUpChance())
                .setTooltip(Text.translatable("tooltip.enchantment-exploration.chance-up"))
                .setSaveConsumer(newValue -> EnchantmentExploration.getConfig().setBumpUpChance(newValue))
                .build());
        main.addEntry(entryBuilder.startFloatField(Text.translatable("option.enchantment-exploration.chance-enchantments"), EnchantmentExploration.getConfig().getAdditionalEnchantmentChance())
                .setDefaultValue(defaultConfig.getAdditionalEnchantmentChance())
                .setTooltip(Text.translatable("tooltip.enchantment-exploration.chance-enchantments"))
                .setSaveConsumer(newValue -> EnchantmentExploration.getConfig().setAdditionalEnchantmentChance(newValue))
                .build());
        main.addEntry(entryBuilder.startFloatField(Text.translatable("option.enchantment-exploration.chance-down"), EnchantmentExploration.getConfig().getBumpDownChance())
                .setDefaultValue(defaultConfig.getBumpDownChance())
                .setTooltip(Text.translatable("tooltip.enchantment-exploration.chance-down"))
                .setSaveConsumer(newValue -> EnchantmentExploration.getConfig().setBumpDownChance(newValue))
                .build());
        main.addEntry(entryBuilder.startIntField(Text.translatable("option.enchantment-exploration.cost0"), EnchantmentExploration.getConfig().getCost0())
                .setDefaultValue(defaultConfig.getCost0())
                .setTooltip(Text.translatable("tooltip.enchantment-exploration.cost0"))
                .setSaveConsumer(newValue -> EnchantmentExploration.getConfig().setCost0(newValue))
                .build());
        main.addEntry(entryBuilder.startIntField(Text.translatable("option.enchantment-exploration.cost1"), EnchantmentExploration.getConfig().getCost1())
                .setDefaultValue(defaultConfig.getCost1())
                .setTooltip(Text.translatable("tooltip.enchantment-exploration.cost1"))
                .setSaveConsumer(newValue -> EnchantmentExploration.getConfig().setCost1(newValue))
                .build());
        main.addEntry(entryBuilder.startIntField(Text.translatable("option.enchantment-exploration.cost2"), EnchantmentExploration.getConfig().getCost2())
                .setDefaultValue(defaultConfig.getCost2())
                .setTooltip(Text.translatable("tooltip.enchantment-exploration.cost2"))
                .setSaveConsumer(newValue -> EnchantmentExploration.getConfig().setCost2(newValue))
                .build());

        anvil.addEntry(entryBuilder.startBooleanToggle(Text.translatable("option.enchantment-exploration.anvil.tool.enabled"), EnchantmentExploration.getConfig().shouldRemoveToolAnvilCombination())
                .setDefaultValue(defaultConfig.shouldRemoveToolAnvilCombination())
                .setTooltip(Text.translatable("tooltip.enchantment-exploration.anvil.tool.enabled"))
                .setSaveConsumer(newValue -> EnchantmentExploration.getConfig().setRemoveToolAnvilCombination(newValue))
                .build());
        anvil.addEntry(entryBuilder.startBooleanToggle(Text.translatable("option.enchantment-exploration.anvil.book.enabled"), EnchantmentExploration.getConfig().shouldRemoveBookAnvilCombination())
                .setDefaultValue(defaultConfig.shouldRemoveBookAnvilCombination())
                .setTooltip(Text.translatable("tooltip.enchantment-exploration.anvil.book.enabled"))
                .setSaveConsumer(newValue -> EnchantmentExploration.getConfig().setRemoveBookAnvilCombination(newValue))
                .build());
        anvil.addEntry(entryBuilder.startBooleanToggle(Text.translatable("option.enchantment-exploration.anvil.combine.enabled"), EnchantmentExploration.getConfig().shouldAnvilCombineBookUpgrade())
                .setDefaultValue(defaultConfig.shouldAnvilCombineBookUpgrade())
                .setTooltip(Text.translatable("tooltip.enchantment-exploration.anvil.combine.enabled"))
                .setSaveConsumer(newValue -> EnchantmentExploration.getConfig().setAnvilCombineBookUpgrade(newValue))
                .build());
        anvil.addEntry(entryBuilder.startStrList(Text.translatable("option.enchantment-exploration.anvil.upgrade-materials"), EnchantmentExploration.getConfig().getAnvilBookUpgradeItems())
                .setDefaultValue(defaultConfig.getAnvilBookUpgradeItems())
                .setTooltip(Text.translatable("tooltip.enchantment-exploration.anvil.upgrade-materials"))
                .setSaveConsumer(newValue -> EnchantmentExploration.getConfig().setAnvilBookUpgradeItems(newValue))
                .build());
        anvil.addEntry(entryBuilder.startBooleanToggle(Text.translatable("option.enchantment-exploration.anvil.vanilla-materials.disabled"), EnchantmentExploration.getConfig().doDefaultRepairMaterialsWork())
                .setDefaultValue(defaultConfig.doDefaultRepairMaterialsWork())
                .setTooltip(Text.translatable("tooltip.enchantment-exploration.anvil.vanilla-materials.disabled"))
                .setSaveConsumer(newValue -> EnchantmentExploration.getConfig().setDefaultRepairMaterialsWork(newValue))
                .build());
        anvil.addEntry(entryBuilder.startStrList(Text.translatable("option.enchantment-exploration.anvil.custom-materials"), EnchantmentExploration.getConfig().getCustomRepairMaterialsAsStringList())
                .setDefaultValue(defaultConfig.getCustomRepairMaterialsAsStringList())
                .setTooltip(Text.translatable("tooltip.enchantment-exploration.anvil.custom-materials.1")
                        .append(Text.translatable("tooltip.enchantment-exploration.anvil.custom-materials.2").formatted(Formatting.YELLOW)))
                .setSaveConsumer(newValue -> EnchantmentExploration.getConfig().setCustomRepairMaterialsFromStringList(newValue))
                .build());

        lootTables.addEntry(entryBuilder.startStrList(Text.translatable("option.enchantment-exploration.loot-table.ignore-skip"), EnchantmentExploration.getConfig().getIgnoreTables())
                .setDefaultValue(defaultConfig.getIgnoreTables())
                .setTooltip(Text.translatable("tooltip.enchantment-exploration.loot-table.ignore-skip"))
                .setSaveConsumer(newValue -> EnchantmentExploration.getConfig().setIgnoreTables(newValue))
                .build());
        lootTables.addEntry(entryBuilder.startStrList(Text.translatable("option.enchantment-exploration.loot-table.skip"), EnchantmentExploration.getConfig().getSkipEnchantmentsList())
                .setDefaultValue(defaultConfig.getSkipEnchantmentsList())
                .setTooltip(Text.translatable("tooltip.enchantment-exploration.loot-table.skip"))
                .setSaveConsumer(newValue -> EnchantmentExploration.getConfig().setSkipEnchantmentsFromList(newValue))
                .build());
        lootTables.addEntry(entryBuilder.startStrList(Text.translatable("option.enchantment-exploration.loot-table.book-pulls"), EnchantmentExploration.getConfig().getLootTableBookPullsList())
                .setDefaultValue(defaultConfig.getLootTableBookPullsList())
                .setTooltip(Text.translatable("tooltip.enchantment-exploration.loot-table.skip.book-pulls.1")
                        .append(Text.translatable("tooltip.enchantment-exploration.anvil.skip.book-pulls.2").formatted(Formatting.YELLOW)))
                .setSaveConsumer(newValue -> EnchantmentExploration.getConfig().setLootTableBookPullsList(newValue))
                .build());

        return builder.build();
    }
}
