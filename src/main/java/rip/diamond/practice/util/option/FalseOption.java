package rip.diamond.practice.util.option;

import lombok.Getter;
import org.bukkit.entity.Player;
import rip.diamond.practice.config.Language;

@Getter
public class FalseOption extends Option {

    public FalseOption(boolean default_) {
        super(default_, Language.DISABLED.toString());
    }

    @Override
    public void run(Player player) {

    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public String getValue() {
        return "false";
    }
}
