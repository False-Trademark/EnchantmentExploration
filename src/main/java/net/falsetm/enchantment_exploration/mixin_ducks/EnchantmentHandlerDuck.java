package net.falsetm.enchantment_exploration.mixin_ducks;

import net.minecraft.enchantment.EnchantmentLevelEntry;

import java.util.List;

public interface EnchantmentHandlerDuck {
    List<EnchantmentLevelEntry> falsetm$GetPossibleEnchants();
    void falsetm$SetPossibleEnchants(List<EnchantmentLevelEntry> possibleEnchants);
}
