package rip.diamond.practice.leaderboard.impl;

import com.mongodb.client.model.Sorts;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.inventory.ItemStack;
import rip.diamond.practice.Eden;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.kits.Kit;
import rip.diamond.practice.leaderboard.Leaderboard;
import rip.diamond.practice.leaderboard.LeaderboardPlayerCache;
import rip.diamond.practice.leaderboard.LeaderboardType;
import rip.diamond.practice.util.ItemBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class KitLeaderboard extends Leaderboard {

    private final Kit kit;

    public KitLeaderboard(LeaderboardType type, Kit kit) {
        super(type);
        this.kit = kit;
    }

    @Override
    public void update() {
        String path = getType().getPath(kit);
        List<Document> documents = Eden.INSTANCE.getMongoManager().getProfiles().find().sort(Sorts.descending(path)).limit(10).into(new ArrayList<>());
        getLeaderboard().clear();
        for (int i = 0; i < documents.size(); i++) {
            Document document = documents.get(i);
            String username = document.getString("username");
            UUID uuid = UUID.fromString(document.getString("uuid"));

            int data = -1;
            try {
                String[] paths = path.split("\\.");
                for (int j = 0; j < paths.length; j++) {
                    if (j + 1 == paths.length) {
                        data = document.getInteger(paths[j]);
                    } else {
                        document = document.get(paths[j], Document.class);
                    }
                }
            } catch (Exception ignored) {
                //The exception usually occurs when the server have a new added kit and the player doesn't have the kit data. Ignore it will be fine
            }


            getLeaderboard().put(i + 1, new LeaderboardPlayerCache(username, uuid, data));
        }
    }

    public ItemStack getDisplayIcon() {
        List<String> lore = new ArrayList<>();

        getLeaderboard().forEach((key, value) -> lore.add(Language.LEADERBOARD_TOP10_DISPLAY_LORE.toString(key, value.getPlayerName(), value.getData())));

        return new ItemBuilder(kit.getDisplayIcon())
                .name(Language.LEADERBOARD_TOP10_DISPLAY_NAME.toString(kit.getDisplayName()))
                .lore(lore)
                .build().clone();
    }

}
