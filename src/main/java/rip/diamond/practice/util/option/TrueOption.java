package rip.diamond.practice.util.option;

import lombok.Getter;
import org.bukkit.entity.Player;
import rip.diamond.practice.config.Language;

@Getter
public class TrueOption extends Option {

    public TrueOption(boolean default_) {
        super(default_, Language.ENABLED.toString());
    }

    @Override
    public void run(Player player) {

    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getValue() {
        return "true";
    }
}
