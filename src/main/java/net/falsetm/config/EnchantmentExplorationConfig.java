package net.falsetm.config;

import java.io.BufferedWriter;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.falsetm.EnchantmentExploration;
import org.jetbrains.annotations.Nullable;

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
    public boolean removeToolAnvilCombination = true;
    public boolean removeBookAnvilCombination = true;
    public boolean defaultRepairMaterialsWork = true;
    public List<customRepair> customRepairMaterials = new ArrayList<>(List.of(new customRepair("minecraft:string", new String[]{"minecraft:bow", "minecraft:crossbow"}), new customRepair("minecraft:prismarine_shard", new String[]{"minecraft:trident"})));

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

    public void setRemoveToolAnvilCombination(boolean removeToolAnvilCombination){
        this.removeToolAnvilCombination = removeToolAnvilCombination;
    }

    public boolean shouldRemoveToolAnvilCombination(){
        return removeToolAnvilCombination;
    }

    public void setRemoveBookAnvilCombination(boolean removeBookAnvilCombination){
        this.removeBookAnvilCombination = removeBookAnvilCombination;
    }

    public boolean shouldRemoveBookAnvilCombination(){
        return removeBookAnvilCombination;
    }

    public void setDefaultRepairMaterialsWork(boolean defaultRepairMaterialsWork){
        this.defaultRepairMaterialsWork = defaultRepairMaterialsWork;
    }

    public boolean doDefaultRepairMaterialsWork(){
        return defaultRepairMaterialsWork;
    }

    public List<customRepair> getCustomRepairMaterials(){
        return customRepairMaterials;
    }

    public int getCustomRepairMaterialsSize(){
        return customRepairMaterials.size();
    }

    public List<String> getCustomRepairMaterialsAsStringList(){
        List<String> output = new ArrayList<>(customRepairMaterials.size());
        for(customRepair repair : customRepairMaterials){
            output.add(repair.getSimpleString());
        }
        return output;
    }

    public void setCustomRepairMaterialsFromStringList(List<String> stringList){
        customRepairMaterials.clear();
        for(String string : stringList){
            customRepairMaterials.add(customRepair.fromString(string));
        }
    }

    public void setCustomRepairMaterialsSize(int newSize){
        for(int i = customRepairMaterials.size(); i < newSize; i++){
            customRepairMaterials.add(null);
        }
        for(int i = customRepairMaterials.size(); i > newSize; i--){
            customRepairMaterials.removeLast();
        }
    }

    @Nullable
    public customRepair getRepairItem(String item){
        for(customRepair repair : customRepairMaterials){
            if(repair.repairMaterial.equals(item)){
                return repair;
            }
        }
        return null;
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

    //A dumber way to do a map so that we can hack cloth config to support it more easily
    public record customRepair(String repairMaterial, String[] repairs){
        public static customRepair fromString(String string){
            String[] splitString = string.split(";", 2);
            if(splitString.length != 2){
                return null;
            }
            String[] splitRepairables = splitString[1].split("/", 0);
            return new customRepair(splitString[0], splitRepairables);
        }

        public boolean containsItem(String item){
            for(String testItem : repairs){
                if(testItem.equals(item)){
                    return true;
                }
            }
            return false;
        }

        public String getSimpleString(){
            StringBuilder output = new StringBuilder(repairMaterial);
            output.append(";");
            if(repairs.length > 0){
                output.append(repairs[0]);
                for(int i = 1; i < repairs.length; i++){
                    output.append("/");
                    output.append(repairs[i]);
                }
            }

            return output.toString();
        }
    }
}

