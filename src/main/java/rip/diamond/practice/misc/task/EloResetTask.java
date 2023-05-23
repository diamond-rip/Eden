package rip.diamond.practice.misc.task;

import org.bson.Document;
import rip.diamond.practice.config.Config;
import rip.diamond.practice.profile.PlayerProfile;
import rip.diamond.practice.profile.PlayerState;
import rip.diamond.practice.util.CC;
import rip.diamond.practice.util.Common;
import rip.diamond.practice.util.TaskTicker;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class EloResetTask extends TaskTicker {

    private final List<Document> documents;
    private final Map<UUID, String> successUsers = new HashMap<>();
    private final Map<UUID, String> failUsers = new HashMap<>();
    private int i = 0;
    private boolean canProcessNext = true;

    public EloResetTask(List<Document> documents) {
        super(0, 20, true);
        this.documents = documents;
    }

    @Override
    public void onRun() {
        if (!canProcessNext) {
            return;
        }
        if (i >= documents.size()) {
            Common.broadcastMessage(
                    "",
                    CC.YELLOW + "Finished elo reset task.",
                    CC.YELLOW + "A total of " + CC.GREEN + successUsers.size() + " users " + CC.YELLOW + "has been reset",
                    CC.YELLOW + "A total of " + CC.RED + failUsers.size() + " users " + CC.YELLOW + "failed to reset"
            );
            if (failUsers.size() != 0) {
                Common.broadcastMessage(CC.YELLOW + "Failed users contain: ");
                Common.broadcastMessage(failUsers.entrySet().stream().map(entry -> " - " + CC.YELLOW + entry.getKey().toString() + CC.GRAY + " (" + entry.getValue() + ")").collect(Collectors.toList()));
            }
            Common.broadcastMessage("");
            cancel();
            return;
        }
        Document document = documents.get(i);
        resetElo(document);
        i++;
    }

    @Override
    public TickType getTickType() {
        return TickType.NONE;
    }

    @Override
    public int getStartTick() {
        return 0;
    }

    private void resetElo(Document document) {
        canProcessNext = false;

        UUID uuid = UUID.fromString(document.getString("uuid"));
        String username = document.getString("username");
        PlayerProfile profile = PlayerProfile.createPlayerProfile(uuid, username);

        Common.broadcastMessage(CC.YELLOW + "Processing to clear " + username + "'s elo... (" + i + "/" + documents.size() + ")");

        profile.setTemporary(true);
        profile.load(document, (success) -> {
            if (success) {
                profile.setPlayerState(PlayerState.IN_LOBBY); //Have to set the player state in here, otherwise PlayerProfile.save will not actually save because data won't be saved when player state is LOADING.
                profile.getKitData().forEach((s, kitData) -> {
                    kitData.setElo(Config.PROFILE_DEFAULT_ELO.toInteger());
                    kitData.setPeakElo(Config.PROFILE_DEFAULT_ELO.toInteger());
                });
                profile.save(false, (bool)-> {
                    canProcessNext = true;
                    PlayerProfile.getProfiles().remove(uuid);
                });
                Common.broadcastMessage(CC.GREEN + "Successfully wiped '" + username + "' elo.");
                successUsers.put(uuid, username);
            } else {
                Common.broadcastMessage(CC.RED + "Unable to load user '" + username + "' profile.");
                failUsers.put(uuid, username);
            }
        });
    }
}
