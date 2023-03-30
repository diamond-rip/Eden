package rip.diamond.practice.match.team;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.DyeColor;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.util.CC;

@Getter
@AllArgsConstructor
public enum TeamColor {

    RED(CC.RED, 16711680, DyeColor.RED, Language.MATCH_TEAM_COLOR_RED_NAME.toString(), Language.MATCH_TEAM_COLOR_RED_LOGO.toString(), DyeColor.RED.ordinal()),
    BLUE(CC.BLUE, 255, DyeColor.BLUE, Language.MATCH_TEAM_COLOR_BLUE_NAME.toString(), Language.MATCH_TEAM_COLOR_BLUE_LOGO.toString(), DyeColor.BLUE.ordinal()),
    GREEN(CC.GREEN, 32768, DyeColor.GREEN, Language.MATCH_TEAM_COLOR_GREEN_NAME.toString(), Language.MATCH_TEAM_COLOR_GREEN_LOGO.toString(), DyeColor.GREEN.ordinal()),
    YELLOW(CC.YELLOW, 16776960, DyeColor.YELLOW, Language.MATCH_TEAM_COLOR_YELLOW_NAME.toString(), Language.MATCH_TEAM_COLOR_YELLOW_LOGO.toString(), DyeColor.YELLOW.ordinal()),
    AQUA(CC.AQUA, 65535, DyeColor.CYAN, Language.MATCH_TEAM_COLOR_AQUA_NAME.toString(), Language.MATCH_TEAM_COLOR_AQUA_LOGO.toString(), DyeColor.CYAN.ordinal()),
    WHITE(CC.WHITE, 16777215, DyeColor.WHITE, Language.MATCH_TEAM_COLOR_WHITE_NAME.toString(), Language.MATCH_TEAM_COLOR_WHITE_LOGO.toString(), DyeColor.WHITE.ordinal()),
    PINK(CC.PINK, 8388736, DyeColor.PINK, Language.MATCH_TEAM_COLOR_PINK_NAME.toString(), Language.MATCH_TEAM_COLOR_PINK_LOGO.toString(), DyeColor.PINK.ordinal()),
    GRAY(CC.DARK_GRAY, 8421504, DyeColor.GRAY, Language.MATCH_TEAM_COLOR_GRAY_NAME.toString(), Language.MATCH_TEAM_COLOR_GRAY_LOGO.toString(), DyeColor.GRAY.ordinal());

    private final String color;
    private final int rgb;
    private final DyeColor dyeColor;
    private final String teamName;
    private final String teamLogo;
    private final int durability;
}
