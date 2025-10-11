package net.falsetm.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.falsetm.events.IsAccessPowerProviderCallback;
import net.minecraft.block.BlockState;
import net.minecraft.block.EnchantingTableBlock;
import net.minecraft.registry.tag.TagKey;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
@Mixin(EnchantingTableBlock.class)
public class EnchantingTableBlockMixin {
    @WrapOperation(method = "canAccessPowerProvider", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isIn(Lnet/minecraft/registry/tag/TagKey;)Z", ordinal = 0))
    private static boolean falsetm$isPowerProvider(BlockState instance, TagKey tagKey, Operation<Boolean> original){
        @Nullable Boolean result = IsAccessPowerProviderCallback.EVENT.invoker().isPowerProvider(instance, tagKey);
        if(result != null){
            return result;
        }
        return original.call(instance, tagKey);
    }
}
