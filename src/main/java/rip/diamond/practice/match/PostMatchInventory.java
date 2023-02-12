package rip.diamond.practice.match;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import rip.diamond.practice.match.team.TeamPlayer;
import rip.diamond.practice.util.HeadUtil;
import rip.diamond.practice.util.HealingMethod;

import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

@Getter
public class PostMatchInventory {

    private final String owner;
    private final UUID ownerUUID;
    private final String ownerHeadValue;
    private String switchTo;
    private UUID switchToUUID;
    private final int health;
    private final int maxHealth;
    private final int hunger;
    private final ItemStack[] armor;
    private final ItemStack[] contents;
    private final Collection<PotionEffect> effects;
    private final int hits;
    private final int blockedHits;
    private final int longestCombo;
    private final HealingMethod healingMethod;
    private final int potionsThrown;
    private final int potionsMissed;

    private final long created = System.currentTimeMillis();

    public PostMatchInventory(TeamPlayer teamPlayer) {
        Player player = teamPlayer.getPlayer();

        this.owner = teamPlayer.getUsername();
        this.ownerUUID = teamPlayer.getUuid();
        this.ownerHeadValue = HeadUtil.getValue(player);
        this.health = player.getHealth() == 0 ? 0 : (int) Math.round(player.getHealth());
        this.maxHealth = (int) Math.round(player.getMaxHealth());
        this.hunger = player.getFoodLevel();
        this.armor = Arrays.stream(player.getInventory().getArmorContents()).map(itemStack -> itemStack == null ? null : itemStack.clone()).toArray(ItemStack[]::new);
        this.contents = Arrays.stream(player.getInventory().getContents()).map(itemStack -> itemStack == null ? null : itemStack.clone()).toArray(ItemStack[]::new);
        this.effects = player.getActivePotionEffects();
        this.hits = teamPlayer.getHits();
        this.blockedHits = teamPlayer.getBlockedHits();
        this.longestCombo = teamPlayer.getLongestCombo();
        this.healingMethod = HealingMethod.getHealingMethod(contents);
        this.potionsThrown = teamPlayer.getPotionsThrown();
        this.potionsMissed = teamPlayer.getPotionsMissed();
    }


    public double getPotionAccuracy() {
        if (potionsMissed == 0) {
            return 100.0;
        } else if (potionsThrown == potionsMissed) {
            return 50.0;
        }

        return Math.round(100.0D - (((double) potionsMissed / (double) potionsThrown) * 100.0D));
    }

    public void setSwitchTo(String name, UUID uuid) {
        this.switchTo = name;
        this.switchToUUID = uuid;
    }

}
