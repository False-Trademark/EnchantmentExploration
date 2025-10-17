package net.falsetm.enchantment_exploration.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.falsetm.enchantment_exploration.events.AnvilScreenHandlerUpdateResultCallback;
import net.falsetm.enchantment_exploration.events.AnvilScreenTakeOutputNoRepairClearSecondCallback;
import net.falsetm.enchantment_exploration.events.AnvilScreenUpdateResultGetSecondInputCallback;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.util.ActionResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilScreenHandler.class)
public class AnvilScreenHandlerMixin {
    @Inject(method = "updateResult", at = @At("HEAD"), cancellable = true)
    public void falsetm$UpdateResult(CallbackInfo ci){
        ActionResult result = AnvilScreenHandlerUpdateResultCallback.EVENT.invoker().updateResult((AnvilScreenHandler)(Object)this);
        if(result == ActionResult.FAIL){
            ci.cancel();
        }
    }

    //skipping the 1 because we only are doing this for the getting of the second slot, as per WrapOperation headers
    @WrapOperation(method = "updateResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/inventory/Inventory;getStack(I)Lnet/minecraft/item/ItemStack;", ordinal = 1))
    ItemStack falsetm$updateResultGetSecond(Inventory instance, int i, Operation<ItemStack> original){
        @Nullable ItemStack result = AnvilScreenUpdateResultGetSecondInputCallback.EVENT.invoker().getStack((AnvilScreenHandler)((Object)this), instance);
        if(result != null){
            return result;
        }
        return original.call(instance, i);
    }

    //why am I even making this an event, such overkill. Actually so stupid it clears instead of reduce
    @WrapOperation(method = "onTakeOutput", at = @At(value = "INVOKE", target = "Lnet/minecraft/inventory/Inventory;setStack(ILnet/minecraft/item/ItemStack;)V", ordinal = 2))
    void falsetm$takeOutputNoRepairSetSecond(Inventory instance, int i, ItemStack itemStack, Operation<Void> original){
        @Nullable ActionResult result = AnvilScreenTakeOutputNoRepairClearSecondCallback.EVENT.invoker().clearSlot((AnvilScreenHandler)((Object)this), instance);

        if(result != ActionResult.FAIL){
            original.call(instance, i, itemStack);
        }
    }

}
