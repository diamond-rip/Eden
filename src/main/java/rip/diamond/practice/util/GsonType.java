package rip.diamond.practice.util;

import com.google.gson.reflect.TypeToken;
import rip.diamond.practice.kits.KitExtraItem;
import rip.diamond.practice.kits.KitGameRules;
import rip.diamond.practice.kits.KitMatchType;

import java.lang.reflect.Type;
import java.util.List;
import java.util.UUID;

public class GsonType {

    public static final Type STRING_LIST = new TypeToken<List<String>>() {}.getType();
    public static final Type UUID_LIST = new TypeToken<List<UUID>>() {}.getType();
    public static final Type KIT_GAME_RULES = new TypeToken<KitGameRules>() {}.getType();
    public static final Type KIT_MATCH_TYPES = new TypeToken<List<KitMatchType>>() {}.getType();
    public static final Type KIT_EXTRA_ITEM = new TypeToken<List<KitExtraItem>>() {}.getType();
}
