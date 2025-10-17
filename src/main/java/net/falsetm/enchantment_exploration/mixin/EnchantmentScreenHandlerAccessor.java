package net.falsetm.enchantment_exploration.mixin;

import net.minecraft.screen.EnchantmentScreenHandler;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EnchantmentScreenHandler.class)
public interface EnchantmentScreenHandlerAccessor{
    @Accessor("random")
    Random getRandom();
}
