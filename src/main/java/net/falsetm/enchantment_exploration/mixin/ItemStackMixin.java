package net.falsetm.enchantment_exploration.mixin;

import net.falsetm.enchantment_exploration.events.ItemStackCanRepairWithCallback;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class ItemStackMixin {
    @Inject(method = "canRepairWith", at = @At("HEAD"), cancellable = true)
    void falsetm$canRepairWith(ItemStack ingredient, CallbackInfoReturnable<Boolean> cir){
        @Nullable Boolean result = ItemStackCanRepairWithCallback.EVENT.invoker().canRepairWith((ItemStack)((Object)this), ingredient);
        if(result != null){
            cir.setReturnValue(result);
        }
    }
}
