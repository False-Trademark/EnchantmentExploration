package net.falsetm.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.util.ActionResult;

public interface AnvilScreenTakeOutputNoRepairClearSecondCallback {
    Event<AnvilScreenTakeOutputNoRepairClearSecondCallback> EVENT = EventFactory.createArrayBacked(AnvilScreenTakeOutputNoRepairClearSecondCallback.class,
            (listeners) -> (receiver, inventory) -> {
                for (AnvilScreenTakeOutputNoRepairClearSecondCallback listener : listeners) {
                    ActionResult result = listener.clearSlot(receiver, inventory);
                    
                    if (result != ActionResult.PASS) {
                        return result;
                    }
                }

                return ActionResult.PASS;
            });
    ActionResult clearSlot(AnvilScreenHandler receiver, Inventory inventory);
}
