package rip.diamond.practice.util.option;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import rip.diamond.practice.util.exception.PracticeUnexpectedException;

@Getter
@RequiredArgsConstructor
public abstract class Option {

    private final boolean default_;
    private final String name;

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Option && ((Option) obj).getName().equals(name);
    }

    public abstract void run(Player player);

    public boolean isEnabled() {
        throw new PracticeUnexpectedException("isEnabled is not supported by Option class");
    }

    public abstract String getValue();

    @Override
    public String toString() {
        return getValue();
    }
}
