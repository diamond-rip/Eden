package rip.diamond.practice.kits.command;

import org.bukkit.entity.Player;
import rip.diamond.practice.Eden;
import rip.diamond.practice.kits.Kit;
import rip.diamond.practice.util.CC;
import rip.diamond.practice.util.Common;
import rip.diamond.practice.util.GsonType;
import rip.diamond.practice.util.command.Command;
import rip.diamond.practice.util.command.CommandArgs;
import rip.diamond.practice.util.command.argument.CommandArguments;
import rip.diamond.practice.util.serialization.BukkitSerialization;
import rip.diamond.practice.util.serialization.EffectSerialization;

import java.util.ArrayList;

public class TransferCommand extends Command {
    @CommandArgs(name = "kittransfer", permission = "eden.command.kittransfer")
    public void execute(CommandArguments command) {
        Player player = command.getPlayer();

        Eden.INSTANCE.getMongoManager().getKits().find().into(new ArrayList<>()).forEach(document -> {
            Kit kit = new Kit(document.getString("_id"));

            kit.setEnabled(document.getBoolean("enabled"));
            kit.setRanked(document.getBoolean("ranked"));
            kit.setDisplayName(document.getString("display-name"));
            kit.setPriority(document.getInteger("priority"));
            kit.setDamageTicks(document.getInteger("damage-ticks"));
            kit.setDisplayIcon(BukkitSerialization.itemStackFromBase64(document.getString("display-icon")));
            kit.setDescription(Eden.GSON.fromJson(document.getString("description"), GsonType.STRING_LIST));
            kit.setEffects(EffectSerialization.deserializeEffects(document.getString("potion-effects")));
            kit.getKitLoadout().setArmor(BukkitSerialization.itemStackArrayFromBase64(document.getString("armor-loadout")));
            kit.getKitLoadout().setContents(BukkitSerialization.itemStackArrayFromBase64(document.getString("contents-loadout")));
            kit.setGameRules(Eden.GSON.fromJson(document.getString("game-rules"), GsonType.KIT_GAME_RULES));
            kit.setKitMatchTypes(Eden.GSON.fromJson(document.getString("kit-match-types"), GsonType.KIT_MATCH_TYPES));
            kit.setKitExtraItems(Eden.GSON.fromJson(document.getString("kit-extra-item"), GsonType.KIT_EXTRA_ITEM));

            Kit.getKits().add(kit);

            Common.sendMessage(player, CC.YELLOW + "[Eden] Imported kit - " + kit.getName());
        });

        Kit.getKits().forEach(Kit::save);

        Common.sendMessage(player, CC.GREEN + "[Eden] Successfully saved all kit data into kit.yml!");
    }
}
