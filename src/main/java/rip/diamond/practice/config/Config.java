package rip.diamond.practice.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import rip.diamond.practice.Eden;
import rip.diamond.practice.util.Util;

// TODO: 30/3/2023 Slowly migrate all config options into this file, with default options (Add to language enum class soon)
// TODO: 30/3/2023 Auto import missing field to config.yml

@AllArgsConstructor
public enum Config {

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
