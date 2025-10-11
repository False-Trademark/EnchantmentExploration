package net.falsetm.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.EnchantmentScreenHandler;
import org.jetbrains.annotations.Nullable;

public interface EnchantmentScreenHandlerApplyCostCallback {
    Event<EnchantmentScreenHandlerApplyCostCallback> EVENT = EventFactory.createArrayBacked(EnchantmentScreenHandlerApplyCostCallback.class,
            (listeners) -> (receiver, stack, inputLevels) -> {
                for (EnchantmentScreenHandlerApplyCostCallback listener : listeners) {
                    @Nullable Integer result = listener.applyCost(receiver, stack, inputLevels);

                    if (result != null) {
                        return result;
                    }
                }

                return null;
            });
    @Nullable Integer applyCost(EnchantmentScreenHandler receiver, ItemStack stack, int inputLevels);
}

