package net.falsetm.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.falsetm.EnchantmentExploration;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ClothConfigScreenFactory {
    public static Screen makeConfig(Screen parent) {
        EnchantmentExplorationConfig defaultConfig = new EnchantmentExplorationConfig();

        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.translatable("title.enchantment-exploration.config"))
                .setSavingRunnable(EnchantmentExploration.getConfig()::saveAsync);
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        ConfigCategory main = builder.getOrCreateCategory(Text.translatable("category.enchantment-exploration.main"));

        main.addEntry(entryBuilder.startBooleanToggle(Text.translatable("option.enchantment-exploration.main.enabled"), EnchantmentExploration.getConfig().isEnabled())
                .setDefaultValue(defaultConfig.isEnabled())
                .setTooltip(Text.translatable("tooltip.enchantment-exploration.main.enabled"))
                .setSaveConsumer(newValue -> EnchantmentExploration.getConfig().setEnabled(newValue))
                .build());
        main.addEntry(entryBuilder.startIntField(Text.translatable("option.enchantment-exploration.cost0.enabled"), EnchantmentExploration.getConfig().getCost0())
                .setDefaultValue(defaultConfig.getCost0())
                .setTooltip(Text.translatable("tooltip.enchantment-exploration.cost0.enabled"))
                .setSaveConsumer(newValue -> EnchantmentExploration.getConfig().setCost0(newValue))
                .build());
        main.addEntry(entryBuilder.startIntField(Text.translatable("option.enchantment-exploration.cost1.enabled"), EnchantmentExploration.getConfig().getCost1())
                .setDefaultValue(defaultConfig.getCost1())
                .setTooltip(Text.translatable("tooltip.enchantment-exploration.cost1.enabled"))
                .setSaveConsumer(newValue -> EnchantmentExploration.getConfig().setCost1(newValue))
                .build());
        main.addEntry(entryBuilder.startIntField(Text.translatable("option.enchantment-exploration.cost2.enabled"), EnchantmentExploration.getConfig().getCost2())
                .setDefaultValue(defaultConfig.getCost2())
                .setTooltip(Text.translatable("tooltip.enchantment-exploration.cost2.enabled"))
                .setSaveConsumer(newValue -> EnchantmentExploration.getConfig().setCost2(newValue))
                .build());

        return builder.build();
    }
}
