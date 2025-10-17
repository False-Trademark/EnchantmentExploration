package net.falsetm.enchantment_exploration;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.loader.api.FabricLoader;
import net.falsetm.enchantment_exploration.config.ClothConfigScreenFactory;

public class ModMenuIntegration implements ModMenuApi
{
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory()
    {
        if (FabricLoader.getInstance().isModLoaded("cloth-config2"))
        {
            return ClothConfigScreenFactory::makeConfig;
        }
        return (parent) -> null;
    }
}
