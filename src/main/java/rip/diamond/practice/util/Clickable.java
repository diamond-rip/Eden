package rip.diamond.practice.util;

import lombok.*;
import net.md_5.bungee.api.chat.*;
import org.bukkit.entity.*;

import java.util.*;

@NoArgsConstructor
@Getter
public class Clickable {

    private final List<TextComponent> components = new ArrayList<>();
    private String hoverText;
    private String text;

    public Clickable(String msg) {
        TextComponent message = new TextComponent(CC.translate(msg));

        this.components.add(message);
        this.text = msg;
    }

    public Clickable(String msg, String hoverMsg, String clickString) {
        this.add(msg, hoverMsg, clickString);
        this.text = msg;
        this.hoverText = hoverMsg;
    }

    public TextComponent add(String msg, String hoverMsg, String clickString) {
        TextComponent message = new TextComponent(CC.translate(msg));

        if (hoverMsg != null) {
            message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(CC.translate(hoverMsg)).create()));
        }

        if (clickString != null) {
            message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, clickString));
        }

        this.components.add(message);
        this.text = msg;
        this.hoverText = hoverMsg;

        return message;
    }

    public void add(String message) {
        this.components.add(new TextComponent(message));
    }

    public void sendToPlayer(Player player) {
        player.spigot().sendMessage(this.asComponents());
    }

    public void sendToPlayer(Player player, String hoverPermission) {
        if (!player.hasPermission(hoverPermission)) {
            player.sendMessage(this.text);
        } else {
            player.spigot().sendMessage(this.asComponents());
        }
    }

    public TextComponent[] asComponents() {
        return this.components.toArray(new TextComponent[0]);
    }
}