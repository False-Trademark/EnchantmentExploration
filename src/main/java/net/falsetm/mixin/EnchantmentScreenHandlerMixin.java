package net.falsetm.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.falsetm.events.EnchantmentScreenHandlerApplyCostCallback;
import net.falsetm.events.GenerateEnchantCallback;
import net.falsetm.events.EnchantmentContentChangedCallback;
import net.falsetm.mixin_ducks.EnchantmentHandlerDuck;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.screen.EnchantmentScreenHandler;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.List;

@Mixin(EnchantmentScreenHandler.class)
public class EnchantmentScreenHandlerMixin implements EnchantmentHandlerDuck {

    @Unique
    private List<EnchantmentLevelEntry> possibleEnchants;

    @Override
    public List<EnchantmentLevelEntry> falsetm$GetPossibleEnchants() {
        return possibleEnchants;
    }

    @Override
    public void falsetm$SetPossibleEnchants(List<EnchantmentLevelEntry> possibleEnchants) {
        this.possibleEnchants = possibleEnchants;
    }

    @Inject(method = "method_17411", at= @At("HEAD"), cancellable = true)
    public void falsetm$EnchantmentContentChanged(ItemStack itemStack, World world, BlockPos pos, CallbackInfo ci){
        ActionResult result = EnchantmentContentChangedCallback.EVENT.invoker().contentChanged((EnchantmentScreenHandler) ((Object)this), itemStack, world, pos);
        if(result == ActionResult.FAIL){
            ci.cancel();
        }
    }

    @WrapOperation(method = "method_17410", at= @At(value = "INVOKE", target = "Lnet/minecraft/screen/EnchantmentScreenHandler;generateEnchantments(Lnet/minecraft/registry/DynamicRegistryManager;Lnet/minecraft/item/ItemStack;II)Ljava/util/List;"))
    public List<EnchantmentLevelEntry> falsetm$ButtonClickedGenerateEnchantment(EnchantmentScreenHandler instance, DynamicRegistryManager registryManager, ItemStack stack, int slot, int level, Operation<List<EnchantmentLevelEntry>> original){
        List <EnchantmentLevelEntry> result = GenerateEnchantCallback.EVENT.invoker().generateEnchantment(instance, registryManager, stack, slot, level);
        if(result != null){
            return result;
        }
        return original.call(instance, registryManager, stack, slot, level);
    }

    @ModifyArgs(method = "method_17410", at= @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;applyEnchantmentCosts(Lnet/minecraft/item/ItemStack;I)V"))
    public void falsetm$modifyApplyEnchantmentCost(Args args){
        @Nullable Integer output = EnchantmentScreenHandlerApplyCostCallback.EVENT.invoker().applyCost((EnchantmentScreenHandler) ((Object)this), args.get(0), args.get(1));
        if(output != null){
            args.set(1, output);
        }
    }
}
