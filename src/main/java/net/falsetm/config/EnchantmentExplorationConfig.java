package net.falsetm.config;

import java.io.BufferedWriter;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
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
    public List<String> ignoreSkipLootTables = new ArrayList<>();
    public Set<String> skipEnchantmentsInLootTable = new HashSet<>(Set.of("minecraft:blast_protection","minecraft:feather_falling","minecraft:fire_protection","minecraft:projectile_protection","minecraft:protection","minecraft:thorns","minecraft:aqua_affinity","minecraft:depth_strider","minecraft:frost_walker","minecraft:respiration","minecraft:soul_speed","minecraft:swift_sneak","minecraft:bane_of_arthropods","minecraft:breach","minecraft:density","minecraft:fire_aspect","minecraft:knockback","minecraft:looting","minecraft:sharpness","minecraft:smite","minecraft:sweeping_edge","minecraft:wind_burst","minecraft:flame","minecraft:power","minecraft:punch","minecraft:quick_charge","minecraft:multishot","minecraft:piercing","minecraft:infinity","minecraft:channeling","minecraft:impaling","minecraft:loyalty","minecraft:riptide","minecraft:efficiency","minecraft:fortune","minecraft:silk_touch","minecraft:mending","minecraft:unbreaking","minecraft:luck_of_the_sea","minecraft:lure","minecraft:binding_curse","minecraft:vanishing_curse"));
    public Map<String, String> lootTableBookPulls = new HashMap<>(Map.ofEntries(
            Map.entry("minecraft:chests/abandoned_mineshaft","enchantment-exploration:book/generic_mineshaft"),
            Map.entry("minecraft:chests/simple_dungeon","enchantment-exploration:book/generic_dungeon"),
            Map.entry("minecraft:chests/pillager_outpost", "enchantment-exploration:book/crossbow"),
            Map.entry("minecraft:chests/woodland_mansion", "enchantment-exploration:book/crossbow"),
            Map.entry("minecraft:chests/ancient_city", "enchantment-exploration:book/ancient_city"),
            Map.entry("minecraft:chests/ancient_city_ice_box", "enchantment-exploration:book/frost"),
            Map.entry("minecraft:chests/igloo_chest", "enchantment-exploration:book/frost"),
            Map.entry("minecraft:chests/shipwreck_map", "enchantment-exploration:book/ocean0"),
            Map.entry("minecraft:chests/underwater_ruin_big", "enchantment-exploration:book/ocean1"),
            Map.entry("minecraft:chests/underwater_ruin_small", "enchantment-exploration:book/ocean1"),
            Map.entry("minecraft:chests/buried_treasure", "enchantment-exploration:book/ocean2"),
            Map.entry("minecraft:chests/jungle_temple", "enchantment-exploration:book/jungle"),
            Map.entry("minecraft:chests/desert_pyramid", "enchantment-exploration:book/pyramid"),
            Map.entry("minecraft:chests/stronghold_library", "enchantment-exploration:book/mending"),
            Map.entry("minecraft:chests/nether_bridge", "enchantment-exploration:book/fortress"),
            Map.entry("minecraft:chests/bastion_treasure", "enchantment-exploration:book/protection"),
            Map.entry("minecraft:chests/bastion_other", "enchantment-exploration:book/bastion"))
    );

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

    public List<String> getIgnoreTables(){
        return new ArrayList<>(ignoreSkipLootTables);
    }

    public void setIgnoreTables(List<String> list){
        ignoreSkipLootTables = new ArrayList<>(list);
    }

    public Set<String> getSkipEnchantments(){
        return skipEnchantmentsInLootTable;
    }

    public List<String> getSkipEnchantmentsList(){
        return new ArrayList<>(skipEnchantmentsInLootTable);
    }

    public void setSkipEnchantmentsFromList(List<String> list){
        skipEnchantmentsInLootTable = new HashSet<>(list);
    }

    public Map<String, String>getLootTableBookPulls(){
        return lootTableBookPulls;
    }

    public List<String> getLootTableBookPullsList(){
        List<String> outList = new ArrayList<>();
        for(var entry : lootTableBookPulls.entrySet()){
            String combined = entry.getKey() + ";" + entry.getValue();
            outList.add(combined);
        }
        return outList;
    }

    public void setLootTableBookPullsList(List<String> stringList){
        Map<String, String> newMap = new HashMap<>();
        for(String string : stringList){
            String[] splitString = string.split(";", 2);
            if(splitString.length != 2){
                continue;
            }
            newMap.put(splitString[0], splitString[1]);
        }
        lootTableBookPulls = newMap;
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

