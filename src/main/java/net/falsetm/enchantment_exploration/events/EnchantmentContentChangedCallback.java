package net.falsetm.enchantment_exploration.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.EnchantmentScreenHandler;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface EnchantmentContentChangedCallback {
    Event<EnchantmentContentChangedCallback> EVENT = EventFactory.createArrayBacked(EnchantmentContentChangedCallback.class,
            (listeners) -> (receiver, stack, world, pos) -> {
                for (EnchantmentContentChangedCallback listener : listeners) {
                    ActionResult result = listener.contentChanged(receiver, stack, world, pos);

                    if (result != ActionResult.PASS) {
                        return result;
                    }
                }

                return ActionResult.PASS;
            });
    ActionResult contentChanged(EnchantmentScreenHandler receiver, ItemStack stack, World world, BlockPos pos);
}
