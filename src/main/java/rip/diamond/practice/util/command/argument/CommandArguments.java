package rip.diamond.practice.util.command.argument;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
@Getter
public class CommandArguments {

    private final CommandSender sender;
    private final String label;
    private final String[] args;

    public int length() {
        return this.args.length;
    }

    public Player getPlayer() {
        if (!(sender instanceof Player)) return null;
        return (Player) sender;
    }
}
