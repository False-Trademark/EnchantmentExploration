package net.falsetm;

import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class EnchantmentHelper {
    public static List<EnchantmentLevelEntry> legalEnchantments(ItemStack stack, List<EnchantmentLevelEntry> inputEnchantments){
        List<EnchantmentLevelEntry> enchantments = new ArrayList<>();
        for(EnchantmentLevelEntry enchantment : inputEnchantments){
            if(enchantment.enchantment.value().isAcceptableItem(stack)){
                enchantments.add(enchantment);
            }
        }
        return enchantments;
    }
}
