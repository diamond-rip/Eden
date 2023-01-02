package rip.diamond.practice.arenas;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import rip.diamond.practice.Eden;
import rip.diamond.practice.kits.Kit;
import rip.diamond.practice.util.Common;
import rip.diamond.practice.util.ItemBuilder;
import rip.diamond.practice.util.serialization.BukkitSerialization;
import rip.diamond.practice.util.serialization.LocationSerialization;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Arena {

    @Getter private static final List<Arena> arenas = new ArrayList<>();

    private final String name;
    private ItemStack icon = new ItemBuilder(Material.GRASS).build();
    private List<ArenaDetail> arenaDetails = new ArrayList<>();

    private int yLimit = 0;
    private int buildMax = -1;
    private List<String> allowedKits = new ArrayList<>();
    private boolean enabled = false;
    private boolean edited = false;

    public static Arena getArena(String name) {
        return arenas.stream()
                .filter(arena -> arena.getName().equals(name))
                .findFirst().orElse(null);
    }

    public static Arena getEnabledArena(Kit kit) {
        return arenas.stream()
                .filter(Arena::isEnabled)
                .filter(a -> a.getAllowedKits().contains(kit.getName()))
                .findFirst().orElse(null);
    }

    public static ArenaDetail getAvailableArenaDetail(String name, Kit kit) {
        Arena arena = arenas.stream()
                .filter(a -> a.getName().equals(name))
                .filter(Arena::isEnabled)
                .filter(a -> a.getAllowedKits().contains(kit.getName()))
                .findFirst().orElse(null);
        if (arena == null) {
            return null;
        }
        return getArenaDetail(arena);
    }

    public static ArenaDetail getAvailableArenaDetail(Kit kit) {
        Arena arena = arenas.stream()
                .filter(Arena::isEnabled)
                .filter(a -> a.getAllowedKits().contains(kit.getName()))
                .findFirst().orElse(null);
        if (arena == null) {
            return null;
        }
        return getArenaDetail(arena);
    }

    public static ArenaDetail getArenaDetail(Arena arena) {
        return arena.getArenaDetails().stream()
                .filter(arenaDetail -> !arenaDetail.isUsing())
                .findFirst().orElse(null);
    }

    public Arena(String name) {
        this.name = name;
    }

    public ItemStack getIcon() {
        return icon.clone();
    }

    public boolean isFinishedSetup() {
        return arenaDetails.stream().allMatch(ArenaDetail::isFinishedSetup);
    }

    public boolean isEnabled() {
        return enabled && isFinishedSetup() && !edited;
    }

    public boolean hasClone() {
        return arenaDetails.size() > 1;
    }

    public Location getA() {
        return arenaDetails.get(0).getA();
    }

    public Location getB() {
        return arenaDetails.get(0).getB();
    }

    public Location getMin() {
        return arenaDetails.get(0).getMin();
    }

    public Location getMax() {
        return arenaDetails.get(0).getMax();
    }

    public void setA(Location location) {
        arenaDetails.get(0).setA(location);
    }

    public void setB(Location location) {
        arenaDetails.get(0).setB(location);
    }

    public void setMin(Location location) {
        arenaDetails.get(0).setMin(location);
    }

    public void setMax(Location location) {
        arenaDetails.get(0).setMax(location);
    }

    public void autoSave() {
        if (Eden.INSTANCE.getConfigFile().getBoolean("arena-kit-auto-save")) {
            save();
        }
    }

    public static void init() {
        FileConfiguration fileConfig = Eden.INSTANCE.getArenaFile().getConfiguration();
        ConfigurationSection arenaSection = fileConfig.getConfigurationSection("arenas");
        if (arenaSection == null) {
            return;
        }

        arenaSection.getKeys(false).forEach(name -> {
            ItemStack icon = BukkitSerialization.itemStackFromBase64(arenaSection.getString(name + ".icon"));
            int yLimit = arenaSection.getInt(name + ".y-limit");
            int buildMax = arenaSection.getInt(name + ".build-max");
            List<String> allowedKits = arenaSection.getStringList(name + ".kits");
            boolean enabled = arenaSection.getBoolean(name + ".enabled", false);

            Arena arena = new Arena(name);
            arena.setIcon(icon);
            arena.setYLimit(yLimit);
            arena.setBuildMax(buildMax);
            arena.setAllowedKits(allowedKits);
            arena.setEnabled(enabled);

            //場地加載
            ConfigurationSection details = arenaSection.getConfigurationSection(name + ".details");
            if (details != null) {
                details.getKeys(false).forEach(id -> {
                    Location locCloneA = LocationSerialization.deserializeLocation(details.getString(id + ".a"));
                    Location locCloneB = LocationSerialization.deserializeLocation(details.getString(id + ".b"));
                    Location locCloneMin = LocationSerialization.deserializeLocation(details.getString(id + ".min"));
                    Location locCloneMax = LocationSerialization.deserializeLocation(details.getString(id + ".max"));

                    ArenaDetail arenaDetail = new ArenaDetail(arena, locCloneA, locCloneB, locCloneMin, locCloneMax);
                    // TODO: 1/1/2023 Copy arena to cache
                    arena.getArenaDetails().add(arenaDetail);
                });
            }

            arenas.add(arena);
            Common.log("Loaded " + arena.getArenaDetails().size() + " " + arena.getName() + " arenas");
        });
    }

    public void save() {
        FileConfiguration fileConfig = Eden.INSTANCE.getArenaFile().getConfiguration();
        String arenaRoot = "arenas." + name;
        fileConfig.set(arenaRoot, null); //Remove everything related to that arena first, then add the details one by one
        fileConfig.set(arenaRoot + ".icon", BukkitSerialization.itemStackToBase64(icon));
        fileConfig.set(arenaRoot + ".y-limit", yLimit);
        fileConfig.set(arenaRoot + ".build-max", buildMax);
        fileConfig.set(arenaRoot + ".kits", allowedKits);
        fileConfig.set(arenaRoot + ".enabled", enabled);

        if (!arenaDetails.isEmpty()) {
            for (int i = 0; i < arenaDetails.size(); i++) {
                ArenaDetail arenaDetail = arenaDetails.get(i);
                String arenaDetailsRoot = arenaRoot + ".details." + i;
                fileConfig.set(arenaDetailsRoot + ".a", LocationSerialization.serializeLocation(arenaDetail.getA()));
                fileConfig.set(arenaDetailsRoot + ".b", LocationSerialization.serializeLocation(arenaDetail.getB()));
                fileConfig.set(arenaDetailsRoot + ".min", LocationSerialization.serializeLocation(arenaDetail.getMin()));
                fileConfig.set(arenaDetailsRoot + ".max", LocationSerialization.serializeLocation(arenaDetail.getMax()));
            }
        }

        Eden.INSTANCE.getArenaFile().save();
    }

}
