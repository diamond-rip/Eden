package rip.diamond.practice.kiteditor;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import rip.diamond.practice.Eden;
import rip.diamond.practice.Language;
import rip.diamond.practice.kits.Kit;
import rip.diamond.practice.profile.PlayerProfile;
import rip.diamond.practice.profile.PlayerState;
import rip.diamond.practice.util.Common;
import rip.diamond.practice.util.Util;
import rip.diamond.practice.util.serialization.LocationSerialization;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class KitEditorManager {

    private final Eden plugin;
    private final Map<UUID, KitEditProfile> editing = new HashMap<>();
    private Location editorLocation = null;

    public KitEditorManager(Eden plugin) {
        this.plugin = plugin;
        try {
            this.editorLocation = LocationSerialization.deserializeLocation(plugin.getLocationFile().getString("editor-location"));
        } catch (Exception e) {
            Common.log("Unable to deserialize editor-location from location file.");
        }
    }

    public void setEditorLocation(Player player) {
        editorLocation = player.getLocation();
        plugin.getLocationFile().getConfiguration().set("editor-location", LocationSerialization.serializeLocation(editorLocation));
        plugin.getLocationFile().save();

        Language.KIT_EDITOR_EDITOR_LOCATION_CHANGED.sendMessage(player);
    }

    public boolean isEditing(Player player) {
        PlayerProfile profile = PlayerProfile.get(player);
        //Check if PracticePlayer is null, this usually be null when player quit the server instantly when they join
        if (profile == null) {
            return false;
        }
        return profile.getPlayerState() == PlayerState.IN_EDIT && editing.containsKey(player.getUniqueId());
    }

    public KitEditProfile getEditingProfile(Player player) {
        return editing.get(player.getUniqueId());
    }

    public void addKitEditor(Player player, Kit kit) {
        if (editorLocation == null) {
            Language.KIT_EDITOR_CANNOT_FIND_EDITOR_LOCATION.sendMessage(player);
            return;
        }
        PlayerProfile profile = PlayerProfile.get(player);
        profile.setPlayerState(PlayerState.IN_EDIT);

        KitEditProfile kProfile = new KitEditProfile(player.getUniqueId(), kit);
        editing.put(player.getUniqueId(), kProfile);

        Util.teleport(player, editorLocation);
        player.getInventory().clear();
        player.getInventory().setContents(kit.getKitLoadout().getContents());

        Language.KIT_EDITOR_EDITING.sendListOfMessage(player, kit.getDisplayName());
    }

    public void leaveKitEditor(Player player, boolean sendToSpawnAndReset) {
        editing.remove(player.getUniqueId());
        if (sendToSpawnAndReset) {
            plugin.getLobbyManager().sendToSpawnAndReset(player);
        }
    }

}
