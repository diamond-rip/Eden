package rip.diamond.practice.hook.plugin.placeholderapi;

import lombok.RequiredArgsConstructor;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import rip.diamond.practice.Eden;

@RequiredArgsConstructor
public class EdenPlaceholderExpansion extends PlaceholderExpansion {

    private final Eden plugin;

    @Override
    public String getIdentifier() {
        return "eden";
    }

    @Override
    public String getAuthor() {
        return "GoodestEnglish";
    }

    @Override
    public String getVersion() {
        return Eden.INSTANCE.getDescription().getVersion();
    }

    // Not sure what I should put into papi.

}
