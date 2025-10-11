package net.falsetm.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.block.BlockState;
import net.minecraft.registry.tag.TagKey;
import org.jetbrains.annotations.Nullable;

public interface isAccessPowerProviderCallback {
    Event<isAccessPowerProviderCallback> EVENT = EventFactory.createArrayBacked(isAccessPowerProviderCallback.class,
            (listeners) -> (state, key) -> {
                for (isAccessPowerProviderCallback listener : listeners) {
                    @Nullable Boolean result = listener.isPowerProvider(state, key);

                    if (result != null) {
                        return result;
                    }
                }

                return null;
            });
    @Nullable Boolean isPowerProvider(BlockState state, TagKey key);
}
