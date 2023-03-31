package rip.diamond.practice.config;

import com.google.common.collect.ImmutableList;
import lombok.AllArgsConstructor;
import lombok.Getter;
import rip.diamond.practice.Eden;
import rip.diamond.practice.util.Util;

import java.util.List;

// TODO: 30/3/2023 Slowly migrate all config options into this file, with default options (Add to language enum class soon)
// TODO: 30/3/2023 Auto import missing field to config.yml

@AllArgsConstructor
public enum Config {

    DEBUG("debug", false),
    ARENA_KIT_AUTO_SAVE("arena-kit-auto-save", false),
    //MongoDB
    MONGO_ENABLED("mongo.enabled", false),
    MONGO_URI_MODE("mongo.uri-mode", false),
    MONGO_NORMAL_HOST("mongo.normal.host", "127.0.0.1"),
    MONGO_NORMAL_PORT("mongo.normal.port", 27017),
    MONGO_NORMAL_AUTH_ENABLED("mongo.normal.auth.enabled", false),
    MONGO_NORMAL_AUTH_USERNAME("mongo.normal.auth.username", ""),
    MONGO_NORMAL_AUTH_PASSWORD("mongo.normal.auth.password", ""),
    MONGO_URI_DATABASE("mongo.uri.database", "Practice"),
    MONGO_URI_CONNECTION_STRING("mongo.uri.connection-string", "mongodb://127.0.0.1:27017/Eden"),
    //Tablist Edit
    FANCY_TABLIST_ENABLED("fancy-tablist.enabled", true),
    FANCY_TABLIST_FORMAT("fancy-tablist.format", "&a{player-name}"),
    FANCY_TABLIST_UPDATE_TICKS("fancy-tablist.update-ticks", 20),
    //NameTag
    NAMETAG_ENABLED("nametag.enabled", true),
    NAMETAG_PREFIX_LOBBY("nametag.prefix.lobby", "&9"),
    NAMETAG_PREFIX_SPECTATOR("nametag.prefix.spectator", "&7"),
    NAMETAG_PREFIX_TEAMMATE("nametag.prefix.teammate", "&a"),
    NAMETAG_PREFIX_OPPONENT("nametag.prefix.opponent", "&c"),
    NAMETAG_PREFIX_OTHER("nametag.prefix.other", "&e"),
    //Party
    PARTY_DEFAULT_MAX_SIZE("party.default-max-size", 30),
    //Lobby
    LOBBY_DISPLAY_PLAYERS("lobby.display-players", true),
    //Match
    MATCH_ALLOW_REQUEUE("match.allow-requeue", true),
    MATCH_REMOVE_SHOW_BLOCK_WHEN_SNOWBALL_HIT("match.remove-snow-block-when-snowball-hit", false),
    MATCH_INSTANT_GAPPLE_EFFECTS("match.instant.gapple.effects", false),
    MATCH_TITLE_SCORE("match.title.score", true),
    MATCH_TITLE_END("match.title.end", true),
    MATCH_END_DURATION("match.end-duration", 100),
    MATCH_FIREBALL_ENABLED("match.fireball.enabled", true),
    MATCH_FIREBALL_DIVIDE_DAMAGE("match.fireball.divide-damage", 5.0),
    MATCH_FIREBALL_SPEED("match.fireball.speed", 2.0),
    MATCH_FIREBALL_YIELD("match.fireball.yield", 2.0),
    MATCH_FIREBALL_ALLOWED_BREAKING_BLOCKS("match.fireball.allowed-breaking-blocks", ImmutableList.of("WOOD", "BED_BLOCK")),
    MATCH_FIREBALL_KNOCKBACK_ENABLED("match.fireball.knockback.enabled", true),
    MATCH_FIREBALL_KNOCKBACK_VERTICAL("match.fireball.knockback.vertical", 1.1),
    MATCH_FIREBALL_KNOCKBACK_HORIZONTAL("match.fireball.knockback.horizontal", 1.2),
    MATCH_TNT_ENABLED("match.tnt.enabled", true),
    MATCH_TNT_DIVIDE_DAMAGE("match.tnt.divide-damage", 5.0),
    MATCH_TNT_SPEED("match.tnt.speed", 2.0),
    MATCH_TNT_YIELD("match.tnt.yield", 2.0),
    MATCH_TNT_FUSE_TICKS("match.tnt.fuse-ticks", 50),
    MATCH_TNT_ALLOWED_BREAKING_BLOCKS("match.tnt.allowed-breaking-blocks", ImmutableList.of("WOOD", "BED_BLOCK")),
    MATCH_TNT_KNOCKBACK_ENABLED("match.tnt.knockback.enabled", true),
    MATCH_TNT_KNOCKBACK_VERTICAL("match.tnt.knockback.vertical", 1.1),
    MATCH_TNT_KNOCKBACK_HORIZONTAL("match.tnt.knockback.horizontal", 1.2),
    //Event
    EVENT_SUMO_EVENT_ARENAS("event.sumo-event.arenas", ImmutableList.of("sumoevent")),
    EVENT_SUMO_EVENT_KIT("event.sumo-event.kit", "sumo"),
    //Chat Format
    CHAT_FORMAT_ENABLED("chat-format.enabled", true),
    CHAT_FORMAT_FORMAT("chat-format.format", "&a%1$s&f: %2$s"),
    //Profile
    PROFILE_SAVE_ON_SERVER_STOP("profile.save-on-server-stop", true),
    PROFILE_DEFAULT_SETTINGS_TIME_CHANGER("profile.default-settings.time-changer", "normal"),
    PROFILE_DEFAULT_SETTINGS_ARENA_SELECTION("profile.default-settings.arena-selection", false),
    PROFILE_DEFAULT_SETTINGS_MATCH_SCOREBOARD("profile.default-settings.match-scoreboard", true),
    PROFILE_DEFAULT_SETTINGS_ALLOW_DUEL_REQUEST("profile.default-settings.allow-duel-request", true),
    PROFILE_DEFAULT_SETTINGS_ALLOW_PARTY_INVITE("profile.default-settings.allow-party-invite", true),
    PROFILE_DEFAULT_SETTINGS_SPECTATOR_VISIBILITY("profile.default-settings.spectator-visibility", true),
    PROFILE_DEFAULT_SETTINGS_SPECTATOR_JOIN_LEAVE_MESSAGE("profile.default-settings.spectator-join-leave-message", true),
    PROFILE_DEFAULT_SETTINGS_EVENT_ANNOUNCEMENT("profile.default-settings.event-announcement", true),
    PROFILE_DEFAULT_SETTINGS_PING_RANGE("profile.default-settings.ping-range", "infinite"),
    //Imanity Spigot Options
    IMANITY_TELEPORT_ASYNC("imanity.teleport-async", true),
    //Optimization
    OPTIMIZATION_CITIZENS_HOOK("optimization.citizens-hook", false),
    ;

    @Getter private final String path;
    @Getter private final Object defaultValue;

    public String toString() {
        String str = Eden.INSTANCE.getConfigFile().getString(path);
        if (Util.isNull(str)) {
            return defaultValue.toString();
        }
        return str;
    }


    public List<String> toStringList() {
        List<String> str = Eden.INSTANCE.getConfigFile().getStringList(path);
        if (str.get(0).equals("null")) {
            return (List<String>) defaultValue;
        }
        return str;
    }

    public boolean toBoolean() {
        return Boolean.parseBoolean(toString());
    }

    public int toInteger() {
        return Integer.parseInt(toString());
    }

    public double toDouble() {
        return Double.parseDouble(toString());
    }

}
