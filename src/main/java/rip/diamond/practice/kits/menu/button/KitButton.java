package rip.diamond.practice.kits.menu.button;

import lombok.RequiredArgsConstructor;
import rip.diamond.practice.kits.Kit;
import rip.diamond.practice.util.menu.Button;

@RequiredArgsConstructor
public abstract class KitButton extends Button {

    public final Kit kit;

}
