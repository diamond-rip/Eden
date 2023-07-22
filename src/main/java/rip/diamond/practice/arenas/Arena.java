package rip.diamond.practice.arenas;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import rip.diamond.practice.Eden;
import rip.diamond.practice.config.Config;
import rip.diamond.practice.kits.Kit;
import rip.diamond.practice.util.Common;
import rip.diamond.practice.util.ItemBuilder;
import rip.diamond.practice.util.exception.PracticeUnexpectedException;
import rip.diamond.practice.util.serialization.BukkitSerialization;
import rip.diamond.practice.util.serialization.LocationSerialization;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
public class Arena {

    @Getter private static final List<Arena> arenas = new ArrayList<>();

    private final String name;
    private String displayName;
    private ItemStack icon = new ItemBuilder(Material.GRASS).build();
    private List<ArenaDetail> arenaDetails = new ArrayList<>();

    private int yLimit = 0;
    private int buildMax = -1;
    private int portalProtectionRadius = 3;
    private List<String> allowedKits = new ArrayList<>();
    private boolean enabled = false;
    private boolean edited = false;

    public static Arena getArena(String name) {
        return arenas.stream()
                .filter(arena -> arena.getName().equalsIgnoreCase(name))
                .findAny().orElse(null);
    }

    public static Arena getEnabledArena(String name, Kit kit) {
        Collections.shuffle(arenas);
        return arenas.stream()
                .filter(arena ->
                        arena.isEnabled() &&
                        !arena.isLocked() &&
                        !arena.getArenaDetails().isEmpty() &&
                        arena.getAllowedKits().contains(kit.getName()) &&
                        arena.getName().equalsIgnoreCase(name)
                )
                .findAny().orElse(null);
    }

    public static Arena getEnabledArena(Kit kit) {
        Collections.shuffle(arenas);
        return arenas.stream()
                .filter(Arena::isEnabled)
                .filter(arena -> arena.getAllowedKits().contains(kit.getName()))
                .findAny().orElse(null);
    }

    public static ArenaDetail getAvailableArenaDetail(Kit kit) {
        Collections.shuffle(arenas);
        Arena arena = arenas.stream()
                .filter(Arena::isEnabled)
                .filter(a -> a.getAllowedKits().contains(kit.getName()))
                .findAny().orElse(null);
        if (arena == null) {
            return null;
        }
        return getArenaDetail(arena);
    }

    public static ArenaDetail getArenaDetail(Arena arena) {
        return arena.getArenaDetails().stream()
                .filter(arenaDetail -> !arenaDetail.isUsing())
                .filter(arenaDetail -> {
                    if (Config.EXPERIMENT_DISABLE_ORIGINAL_ARENA.toBoolean()) {
                        return arena.getArenaDetails().get(0) != arenaDetail;
                    }
                    return true;
                })
                .findAny().orElse(null);
    }

    public Arena(String name) {
        this.name = name;
        this.displayName = name;
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

    //If an arena is locked, which means the arena can only be accessible by special cases, like event
    public boolean isLocked() {
        return Config.EVENT_SUMO_EVENT_ARENAS.toStringList().contains(name);
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

    public Location getSpectator() {
        return arenaDetails.get(0).getSpectator();
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

    public void setSpectator(Location location) {
        arenaDetails.get(0).setSpectator(location);
    }

    public void setMin(Location location) {
        arenaDetails.get(0).setMin(location);
    }

    public void setMax(Location location) {
        arenaDetails.get(0).setMax(location);
    }

    public void autoSave() {
        if (Config.ARENA_KIT_AUTO_SAVE.toBoolean()) {
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
            String displayName = arenaSection.getString(name + ".display-name", name); // Add default value if display-name not found for backwards compatibility
            ItemStack icon = BukkitSerialization.itemStackFromBase64(arenaSection.getString(name + ".icon"));
            int yLimit = arenaSection.getInt(name + ".y-limit");
            int buildMax = arenaSection.getInt(name + ".build-max");
            int portalProtectionRadius = arenaSection.getInt(name + ".portal-protection-radius");
            List<String> allowedKits = arenaSection.getStringList(name + ".kits");
            boolean enabled = arenaSection.getBoolean(name + ".enabled", false);

            Arena arena = new Arena(name);
            arena.setDisplayName(displayName);
            arena.setIcon(icon);
            arena.setYLimit(yLimit);
            arena.setBuildMax(buildMax);
            arena.setPortalProtectionRadius(portalProtectionRadius);
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
                    Location locCloneSpectator = LocationSerialization.deserializeLocation(details.getString(id + ".spectator"));

                    ArenaDetail arenaDetail = new ArenaDetail(arena, locCloneA, locCloneB, locCloneSpectator == null ? locCloneA : locCloneSpectator, locCloneMin, locCloneMax);
                    arenaDetail.copyChunk();
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
        fileConfig.set(arenaRoot + ".display-name", displayName);
        fileConfig.set(arenaRoot + ".icon", BukkitSerialization.itemStackToBase64(icon));
        fileConfig.set(arenaRoot + ".y-limit", yLimit);
        fileConfig.set(arenaRoot + ".build-max", buildMax);
        fileConfig.set(arenaRoot + ".portal-protection-radius", portalProtectionRadius);
        fileConfig.set(arenaRoot + ".kits", allowedKits);
        fileConfig.set(arenaRoot + ".enabled", enabled);

        if (!arenaDetails.isEmpty()) {
            for (int i = 0; i < arenaDetails.size(); i++) {
                ArenaDetail arenaDetail = arenaDetails.get(i);
                String arenaDetailsRoot = arenaRoot + ".details." + i;
                fileConfig.set(arenaDetailsRoot + ".a", LocationSerialization.serializeLocation(arenaDetail.getA()));
                fileConfig.set(arenaDetailsRoot + ".b", LocationSerialization.serializeLocation(arenaDetail.getB()));
                fileConfig.set(arenaDetailsRoot + ".spectator", LocationSerialization.serializeLocation(arenaDetail.getSpectator()));
                fileConfig.set(arenaDetailsRoot + ".min", LocationSerialization.serializeLocation(arenaDetail.getMin()));
                fileConfig.set(arenaDetailsRoot + ".max", LocationSerialization.serializeLocation(arenaDetail.getMax()));
            }
        }

        Eden.INSTANCE.getArenaFile().save();
    }

}
