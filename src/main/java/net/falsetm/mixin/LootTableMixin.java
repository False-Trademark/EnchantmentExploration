package net.falsetm.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.falsetm.events.LootTableApplyFunctionsCallback;
import net.falsetm.events.LootTableFinishGenerateUnprocessedCallback;
import net.falsetm.mixin_ducks.LootTableDuck;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.util.ActionResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BiFunction;
import java.util.function.Consumer;

@Mixin(LootTable.class)
public class LootTableMixin implements LootTableDuck {
    @Unique
    private boolean skipFunctionMixin = false;

    @Override
    public void falsetm$skipMixin() {
        skipFunctionMixin = true;
    }

    @WrapOperation(method = "generateUnprocessedLoot(Lnet/minecraft/loot/context/LootContext;Ljava/util/function/Consumer;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/loot/function/LootFunction;apply(Ljava/util/function/BiFunction;Ljava/util/function/Consumer;Lnet/minecraft/loot/context/LootContext;)Ljava/util/function/Consumer;"))
    public Consumer<ItemStack> falsetm$applyFunctions(BiFunction<ItemStack, LootContext, ItemStack> itemApplier, Consumer<ItemStack> lootConsumer, LootContext context, Operation<Consumer<ItemStack>> original){
        if(!skipFunctionMixin){
            @Nullable Consumer<ItemStack> result = LootTableApplyFunctionsCallback.EVENT.invoker().onApply((LootTable)((Object)this), itemApplier, lootConsumer, context, original);

            if(result != null){
                return result;
            }
        }
        skipFunctionMixin = false;
        return lootConsumer;
    }

    @Inject(method = "generateUnprocessedLoot(Lnet/minecraft/loot/context/LootContext;Ljava/util/function/Consumer;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/loot/context/LootContext;markInactive(Lnet/minecraft/loot/context/LootContext$Entry;)V"), cancellable = true)
    public void falsetm$endUnprocessed(LootContext context, Consumer<ItemStack> lootConsumer, CallbackInfo ci){
        ActionResult result = LootTableFinishGenerateUnprocessedCallback.EVENT.invoker().beforeInactive((LootTable)((Object)this), context, lootConsumer);
        if(result == ActionResult.FAIL){
            ci.cancel();
        }
    }
}
