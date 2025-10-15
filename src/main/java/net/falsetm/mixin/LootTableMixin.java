package net.falsetm.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.falsetm.events.LootTableApplyFunctionsCallback;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.BiFunction;
import java.util.function.Consumer;

@Mixin(LootTable.class)
public class LootTableMixin {
    @WrapOperation(method = "generateUnprocessedLoot(Lnet/minecraft/loot/context/LootContext;Ljava/util/function/Consumer;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/loot/function/LootFunction;apply(Ljava/util/function/BiFunction;Ljava/util/function/Consumer;Lnet/minecraft/loot/context/LootContext;)Ljava/util/function/Consumer;"))
    public Consumer<ItemStack> falsetm$applyFunctions(BiFunction<ItemStack, LootContext, ItemStack> itemApplier, Consumer<ItemStack> lootConsumer, LootContext context, Operation<Consumer<ItemStack>> original){
        @Nullable Consumer<ItemStack> result = LootTableApplyFunctionsCallback.EVENT.invoker().onApply((LootTable)((Object)this), itemApplier, lootConsumer, context, original);

        if(result != null){
            return result;
        }

        return lootConsumer;
    }
}
