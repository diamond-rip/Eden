package rip.diamond.practice.match;

import lombok.AllArgsConstructor;
import lombok.Getter;
import rip.diamond.practice.Language;

@Getter
@AllArgsConstructor
public enum MatchType {
    SOLO(Language.MATCH_MATCH_TYPE_SOLO.toString()),
    FFA(Language.MATCH_MATCH_TYPE_FFA.toString()),
    SPLIT(Language.MATCH_MATCH_TYPE_SPLIT.toString());

    private final String readable;
}
