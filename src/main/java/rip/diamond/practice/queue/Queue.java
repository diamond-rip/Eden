package rip.diamond.practice.queue;

import lombok.Getter;
import org.bukkit.entity.Player;
import rip.diamond.practice.Eden;
import rip.diamond.practice.config.Config;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.kits.Kit;
import rip.diamond.practice.party.Party;
import rip.diamond.practice.profile.PlayerProfile;
import rip.diamond.practice.profile.PlayerState;
import rip.diamond.practice.profile.data.ProfileKitData;
import rip.diamond.practice.queue.task.QueueTask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class Queue {

    @Getter private static final Map<UUID, QueueProfile> players = new HashMap<>();

    public static void init() {
        new QueueTask();
    }

    public static List<QueueProfile> getUnmatchedPlayers() {
        return players.values().stream().filter(qProfile -> !qProfile.isFound()).collect(Collectors.toList());
    }

    public static void joinQueue(Player player, Kit kit, QueueType queueType) {
        if (player == null || !player.isOnline()) {
            return;
        }
        if (players.get(player.getUniqueId()) != null) {
            Language.QUEUE_ERROR_FOUND_QUEUE_PROFILE.sendMessage(player);
            return;
        }
        if (Party.getByPlayer(player) != null) {
            Language.PARTY_IN_A_PARTY.sendMessage(player);
            return;
        }

        PlayerProfile profile = PlayerProfile.get(player);
        if (profile.getPlayerState() != PlayerState.IN_LOBBY) {
            Language.QUEUE_WRONG_STATE.sendMessage(player);
            return;
        }

        if (profile.getKitData().get(kit.getName()) == null) {
            Language.QUEUE_ERROR_KIT_DATA_NOT_FOUND.sendMessage(player);
            return;
        }

        if (queueType == QueueType.RANKED) {
            int required = Config.QUEUE_RANKED_REQUIRED_WINS.toInteger();
            int wins = profile.getKitData().values().stream().mapToInt(ProfileKitData::getUnrankedWon).sum();
            if (wins < required) {
                Language.QUEUE_ERROR_NOT_ENOUGH_WINS.sendMessage(player, required, wins);
                return;
            }
        }

        QueueProfile qProfile = new QueueProfile(player.getUniqueId(), kit, profile.getKitData().get(kit.getName()).getElo(), queueType);
        players.put(player.getUniqueId(), qProfile);
        profile.setPlayerState(PlayerState.IN_QUEUE);
        profile.setupItems();

        Language.QUEUE_SUCCESS_JOIN.sendMessage(player, kit.getDisplayName());
    }

    public static void leaveQueue(Player player) {
        PlayerProfile profile = PlayerProfile.get(player);
        QueueProfile qProfile = players.get(player.getUniqueId());

        if (profile.getPlayerState() != PlayerState.IN_QUEUE) {
            Language.QUEUE_CANNOT_QUIT_QUEUE.sendMessage(player);
            return;
        }
        if (qProfile == null) {
            Language.QUEUE_ERROR_NOT_FOUND_QUEUE_PROFILE.sendMessage(player);
            return;
        }

        players.remove(player.getUniqueId());

        Eden.INSTANCE.getLobbyManager().reset(player);

        Language.QUEUE_SUCCESS_QUIT.sendMessage(player, qProfile.getKit().getDisplayName());
    }

}
