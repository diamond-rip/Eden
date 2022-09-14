package rip.diamond.practice.util.tablist.skin;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SkinUtil {

    private final static Map<UUID, String[]> cache = new HashMap<>();

    /**
     * Get the skin data by a player's unique identifier
     *
     * @param uuid the unique identifier to get the skin data by
     * @return the skin data
     */
    public static String[] getSkinData(UUID uuid) throws IOException {
        if (cache.containsKey(uuid)) {
            return cache.get(uuid);
        }

        final URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.toString().replace("-", "") + "?unsigned=false");
        final JsonObject json = new JsonParser().parse(new InputStreamReader(url.openStream())).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject();

        return cache.put(uuid, new String[]{
                json.get("value").getAsString(),
                json.get("signature").getAsString()
        });
    }

    /**
     * Get the skin data by a player's unique identifier
     *
     * @param uuid the unique identifier to get the skin data by
     * @return the skin data
     */
    public static String[] getSkinDataThrown(UUID uuid) {
        try {
            return getSkinData(uuid);
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        return null;
    }
}