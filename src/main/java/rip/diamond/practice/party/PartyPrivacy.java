package rip.diamond.practice.party;

import lombok.AllArgsConstructor;
import lombok.Getter;
import rip.diamond.practice.config.Language;

@AllArgsConstructor
@Getter
public enum PartyPrivacy {

	OPEN(Language.PARTY_PRIVACY_OPEN.toString()),
	CLOSED(Language.PARTY_PRIVACY_CLOSED.toString());

	private final String readable;

}
