/*
 * MIT License
 *
 * Copyright (c) 2020 - 2020 Imanity
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package rip.diamond.practice.util.tablist.util;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Setter
@RequiredArgsConstructor
public class Skin {

    private static final LoadingCache<String, Skin> SKIN_CACHE = Caffeine.newBuilder()
            .expireAfterAccess(60L, TimeUnit.SECONDS)
            .initialCapacity(60)
            .build(new CacheLoader<String, Skin>() {
                @Override
                public @Nullable Skin load(@NonNull String s) {
                    Player player = Bukkit.getPlayer(s);
                    if (player != null) {
                        EntityPlayer playerNMS = ((CraftPlayer) player).getHandle();
                        GameProfile profile = playerNMS.getProfile();

                        if (!profile.getProperties().get("textures").isEmpty()) {
                            Property property = profile.getProperties().get("textures").iterator().next();
                            String texture = property.getValue();
                            String signature = property.getSignature();

                            return new Skin(texture, signature);
                        }
                        return null;
                    }
                    return null;
                }
            });

    public static Skin GRAY = new Skin(
            "eyJ0aW1lc3RhbXAiOjE0MTEyNjg3OTI3NjUsInByb2ZpbGVJZCI6IjNmYmVjN2RkMGE1ZjQwYmY5ZDExODg1YTU0NTA3MTEyIiwicHJvZmlsZU5hbWUiOiJsYXN0X3VzZXJuYW1lIiwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzg0N2I1Mjc5OTg0NjUxNTRhZDZjMjM4YTFlM2MyZGQzZTMyOTY1MzUyZTNhNjRmMzZlMTZhOTQwNWFiOCJ9fX0=",
            "u8sG8tlbmiekrfAdQjy4nXIcCfNdnUZzXSx9BE1X5K27NiUvE1dDNIeBBSPdZzQG1kHGijuokuHPdNi/KXHZkQM7OJ4aCu5JiUoOY28uz3wZhW4D+KG3dH4ei5ww2KwvjcqVL7LFKfr/ONU5Hvi7MIIty1eKpoGDYpWj3WjnbN4ye5Zo88I2ZEkP1wBw2eDDN4P3YEDYTumQndcbXFPuRRTntoGdZq3N5EBKfDZxlw4L3pgkcSLU5rWkd5UH4ZUOHAP/VaJ04mpFLsFXzzdU4xNZ5fthCwxwVBNLtHRWO26k/qcVBzvEXtKGFJmxfLGCzXScET/OjUBak/JEkkRG2m+kpmBMgFRNtjyZgQ1w08U6HHnLTiAiio3JswPlW5v56pGWRHQT5XWSkfnrXDalxtSmPnB5LmacpIImKgL8V9wLnWvBzI7SHjlyQbbgd+kUOkLlu7+717ySDEJwsFJekfuR6N/rpcYgNZYrxDwe4w57uDPlwNL6cJPfNUHV7WEbIU1pMgxsxaXe8WSvV87qLsR7H06xocl2C0JFfe2jZR4Zh3k9xzEnfCeFKBgGb4lrOWBu1eDWYgtKV67M2Y+B3W5pjuAjwAxn0waODtEn/3jKPbc/sxbPvljUCw65X+ok0UUN1eOwXV5l2EGzn05t3Yhwq19/GxARg63ISGE8CKw="
    );

    public final String skinValue;
    public final String skinSignature;

    @Override
    public String toString() {
        return skinSignature + skinValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Skin skin = (Skin) o;

        if (!Objects.equals(skinValue, skin.skinValue)) return false;
        return Objects.equals(skinSignature, skin.skinSignature);
    }

    @Override
    public int hashCode() {
        int result = skinValue != null ? skinValue.hashCode() : 0;
        result = 31 * result + (skinSignature != null ? skinSignature.hashCode() : 0);
        return result;
    }

    public static Skin fromPlayer(Player player) {
        Skin skin = SKIN_CACHE.get(player.getName());

        if (skin == null) {
            EntityPlayer playerNMS = ((CraftPlayer) player).getHandle();
            GameProfile profile = playerNMS.getProfile();

            if (!profile.getProperties().get("textures").isEmpty()) {
                Property property = profile.getProperties().get("textures").iterator().next();
                skin = new Skin(property.getValue(), property.getSignature());
                SKIN_CACHE.put(player.getName(), skin);
                return skin;
            }
        }

        return skin == null ? Skin.GRAY : skin;
    }
}
