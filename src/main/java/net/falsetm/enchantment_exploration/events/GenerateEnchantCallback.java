package net.falsetm.enchantment_exploration.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.screen.EnchantmentScreenHandler;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface GenerateEnchantCallback {
    Event<GenerateEnchantCallback> EVENT = EventFactory.createArrayBacked(GenerateEnchantCallback.class,
            (listeners) -> (receiver, registryManager, stack, slot, level) -> {
                for (GenerateEnchantCallback listener : listeners) {
                    @Nullable
                    List<EnchantmentLevelEntry> result = listener.generateEnchantment(receiver, registryManager, stack, slot, level);
                    if (result != null) {
                        return result;
                    }
                }

                return null;
            });

    @Nullable
    List<EnchantmentLevelEntry> generateEnchantment(EnchantmentScreenHandler receiver, DynamicRegistryManager registryManager, ItemStack stack, int slot, int level);
}
