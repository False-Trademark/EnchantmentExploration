package net.falsetm.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.AnvilScreenHandler;
import org.jetbrains.annotations.Nullable;

public interface AnvilScreenUpdateResultGetSecondInputCallback {
    Event<AnvilScreenUpdateResultGetSecondInputCallback> EVENT = EventFactory.createArrayBacked(AnvilScreenUpdateResultGetSecondInputCallback.class,
            (listeners) -> (receiver, inventory) -> {
                for (AnvilScreenUpdateResultGetSecondInputCallback listener : listeners) {
                    @Nullable ItemStack result = listener.getStack(receiver, inventory);

                    if (result != null) {
                        return result;
                    }
                }

                return null;
            });
    @Nullable ItemStack getStack(AnvilScreenHandler receiver, Inventory inventory);
}
