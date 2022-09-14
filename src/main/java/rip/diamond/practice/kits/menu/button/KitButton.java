package rip.diamond.practice.kits.menu.button;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import rip.diamond.practice.kits.Kit;
import rip.diamond.practice.util.menu.Button;

@Getter
@RequiredArgsConstructor
public abstract class KitButton extends Button {

    private final Kit kit;

}
