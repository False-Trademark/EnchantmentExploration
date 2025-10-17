package net.falsetm.enchantment_exploration.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.util.ActionResult;

import java.util.function.Consumer;

public interface LootTableFinishGenerateUnprocessedCallback {
    Event<LootTableFinishGenerateUnprocessedCallback> EVENT = EventFactory.createArrayBacked(LootTableFinishGenerateUnprocessedCallback.class,
            (listeners) -> (receiver, context, lootConsumer) -> {
                for (LootTableFinishGenerateUnprocessedCallback listener : listeners) {
                    ActionResult result = listener.beforeInactive(receiver, context, lootConsumer);

                    if (result != ActionResult.PASS) {
                        return result;
                    }
                }

                return ActionResult.PASS;
            });
    ActionResult beforeInactive(LootTable receiver, LootContext context, Consumer<ItemStack> lootConsumer);
}
