package rip.diamond.practice.config;

import lombok.RequiredArgsConstructor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import rip.diamond.practice.Eden;
import rip.diamond.practice.util.Common;
import rip.diamond.practice.util.Util;

@RequiredArgsConstructor
public enum EdenSound {

    RECEIVE_DUEL_REQUEST("receive-duel-request", Sound.CHICKEN_EGG_POP, 1f, 1f),
    GOLDEN_HEAD_EAT("golden-head-eat", Sound.EAT, 1f, 1f),
    SELF_BREAK_BED("self-break-bed", Sound.ENDERDRAGON_GROWL, 1f, 1f),
    OPPONENT_BREAK_BED("opponent-break-bed", Sound.WITHER_DEATH, 1f, 1f),
    NEW_ROUND_COUNTDOWN("new-round-countdown", Sound.CLICK, 1f, 1f),
    MATCH_START("match-start", Sound.FIREWORK_BLAST, 1f, 1f),
    ;

    private final String path;
    private final Sound sound;
    private final float volume;
    private final float pitch;

    private Sound getSound() {
        String str = Eden.INSTANCE.getSoundFile().getString(path);
        if (Util.isNull(str)) {
            return sound;
        }
        return Sound.valueOf(str.split(";")[0]);
    }

    public float getVolume() {
        String str = Eden.INSTANCE.getSoundFile().getString(path);
        if (Util.isNull(str)) {
            return volume;
        }
        return Float.parseFloat(str.split(";")[1]);
    }

    public float getPitch() {
        String str = Eden.INSTANCE.getSoundFile().getString(path);
        if (Util.isNull(str)) {
            return pitch;
        }
        return Float.parseFloat(str.split(";")[2]);
    }

    public void play(Player player) {
        Common.playSound(player, getSound(), getVolume(), getPitch());
    }

}
