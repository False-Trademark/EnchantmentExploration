package net.falsetm.config;

import java.io.BufferedWriter;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.falsetm.EnchantmentExploration;

/**
 * Taken from webspeak
 */
public class EnchantmentExplorationConfig {
    public static final Path CONFIG_FILE = FabricLoader.getInstance().getConfigDir().resolve("enchantment-exploration.json");
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public boolean enabled = true;
    public int cost0 = 10;
    public int cost1 = 10;
    public int cost2 = 10;
    public boolean toolAnvilCombination = false;

    public void setEnabled(boolean enabled){
        this.enabled = enabled;
    }

    public boolean isEnabled(){
        return enabled;
    }

    public void setCost0(int newCost){
        cost0 = newCost;
    }

    public int getCost0(){
        return cost0;
    }

    public void setCost1(int newCost){
        cost1 = newCost;
    }

    public int getCost1(){
        return cost1;
    }

    public void setCost2(int newCost){
        cost2 = newCost;
    }

    public int getCost2(){
        return cost2;
    }

    public void setAnvilCombination(boolean toolAnvilCombination){
        this.toolAnvilCombination = toolAnvilCombination;
    }

    public boolean shouldAnvilCombination(){
        return toolAnvilCombination;
    }

    public String toJson() {
        return GSON.toJson(this);
    }

    public static EnchantmentExplorationConfig fromJson(String json) {
        return GSON.fromJson(json, EnchantmentExplorationConfig.class);
    }

    public static EnchantmentExplorationConfig fromJson(Reader reader) {
        return GSON.fromJson(reader, EnchantmentExplorationConfig.class);
    }

    /**
     * Asynchronously save the EnchantmentExploration config to file.
     * @return A future that completes when the config is saved.
     */
    public CompletableFuture<Void> saveAsync() {
        return CompletableFuture.runAsync(() -> {
            try(BufferedWriter writer = Files.newBufferedWriter(CONFIG_FILE)) {
                writer.write(toJson());
            } catch (Exception e) {
                EnchantmentExploration.LOGGER.error("Error saving Enchantment Exploration config.", e);
                throw new CompletionException(e);
            }
        });
    }
}

