package rip.diamond.practice.kiteditor;

import lombok.AllArgsConstructor;
import lombok.Getter;
import rip.diamond.practice.kits.Kit;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class KitEditProfile {

    private UUID playerUuid;
    private Kit kit;

}
