package rip.diamond.practice.profile;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PlayerState {
    LOADING(false),
    IN_LOBBY(false),
    IN_EDIT(true),
    IN_QUEUE(false),
    IN_MATCH(true),
    IN_SPECTATING(false);

    private final boolean ableToMoveItemInInventory;
}
