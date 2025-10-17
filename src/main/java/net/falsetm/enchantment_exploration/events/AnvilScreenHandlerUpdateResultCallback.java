package net.falsetm.enchantment_exploration.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.util.ActionResult;

public interface AnvilScreenHandlerUpdateResultCallback {
    Event<AnvilScreenHandlerUpdateResultCallback> EVENT = EventFactory.createArrayBacked(AnvilScreenHandlerUpdateResultCallback.class,
            (listeners) -> (receiver) -> {
                for (AnvilScreenHandlerUpdateResultCallback listener : listeners) {
                    ActionResult result = listener.updateResult(receiver);

                    if (result != ActionResult.PASS) {
                        return result;
                    }
                }

                return ActionResult.PASS;
            });
    ActionResult updateResult(AnvilScreenHandler receiver);
}
