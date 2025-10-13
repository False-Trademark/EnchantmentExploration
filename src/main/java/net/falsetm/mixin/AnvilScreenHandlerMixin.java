package net.falsetm.mixin;

import net.falsetm.events.AnvilScreenHandlerUpdateResultCallback;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.util.ActionResult;
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
}
