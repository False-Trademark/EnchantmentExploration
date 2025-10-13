package net.falsetm.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface ItemStackCanRepairWithCallback {
    Event<ItemStackCanRepairWithCallback> EVENT = EventFactory.createArrayBacked(ItemStackCanRepairWithCallback.class,
            (listeners) -> (receiver, repairStack) -> {
                for (ItemStackCanRepairWithCallback listener : listeners) {
                    @Nullable Boolean result = listener.canRepairWith(receiver, repairStack);

                    if (result != null) {
                        return result;
                    }
                }

                return null;
            });
    @Nullable Boolean canRepairWith(ItemStack receiver, ItemStack repairStack);
}
