package rip.diamond.practice.kits;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import rip.diamond.practice.Language;
import rip.diamond.practice.match.Match;
import rip.diamond.practice.queue.QueueType;

@Setter
public class KitGameRules {

	@Getter private boolean receiveKitLoadoutBook = true;
	@Getter private boolean deathOnWater = false;
	@Getter private boolean boxing = false;
	@Getter private boolean bed = false;
	@Getter private boolean goal = false;
	@Getter private boolean projectileOnly = false;
	@Getter private boolean hypixelUHC = false;
	@Getter private boolean spleef = false;
	@Getter private boolean healthRegeneration = true;
	@Getter private boolean showHealth = true;
	@Getter private boolean foodLevelChange = true;
	private boolean point = false;
	private boolean rankedPoint = false;
	@Getter private boolean resetArenaWhenGetPoint = false;
	@Getter private boolean build = false;
	@Getter private boolean startFreeze = false;
	@Getter private boolean noDamage = false;
	@Getter private boolean instantGapple = false;
	@Getter private boolean enderPearlCooldown = false;
	@Getter private boolean clearBlock = false;
	@Getter private boolean dropItemWhenDie = true;
	@Getter private boolean noFallDamage = false;
	@Getter private boolean giveBackArrow = false;

	@Getter private int respawnTime = 5;
	@Getter private int maximumPoints = 3;

	public boolean isPoint(Match match) {
		switch (match.getMatchType()) {
			case SOLO:
				return (point && match.getQueueType() != QueueType.RANKED) || (rankedPoint && match.getQueueType() == QueueType.RANKED);
			case SPLIT:
				//Need to check if the GameRule contains deathOnWater. This is to prevent if the GameRule contains point and the kit is sumo.
				//If it is sumo, then we should not display the point out because point should not be in sumo TeamMatch
				return ((point && match.getQueueType() != QueueType.RANKED) || (rankedPoint && match.getQueueType() == QueueType.RANKED)) && !match.getKit().getGameRules().isDeathOnWater();
			case FFA:
				return false;
		}
		return false;
	}

	@Getter
	@AllArgsConstructor
	public enum Readable {
		receiveKitLoadoutBook(Language.KIT_GAME_RULES_RECEIVE_KIT_LOADOUT_BOOK_RULES.toString(), Language.KIT_GAME_RULES_RECEIVE_KIT_LOADOUT_BOOK_DESCRIPTION.toString()),
		deathOnWater(Language.KIT_GAME_RULES_DEATH_ON_WATER_RULES.toString(), Language.KIT_GAME_RULES_DEATH_ON_WATER_DESCRIPTION.toString()),
		boxing(Language.KIT_GAME_RULES_BOXING_RULES.toString(), Language.KIT_GAME_RULES_BOXING_DESCRIPTION.toString()),
		bed(Language.KIT_GAME_RULES_BED_RULES.toString(), Language.KIT_GAME_RULES_BED_DESCRIPTION.toString()),
		goal(Language.KIT_GAME_RULES_GOAL_RULES.toString(), Language.KIT_GAME_RULES_GOAL_DESCRIPTION.toString()),
		projectileOnly(Language.KIT_GAME_RULES_PROJECTILE_ONLY_RULES.toString(), Language.KIT_GAME_RULES_PROJECTILE_ONLY_DESCRIPTION.toString()),
		hypixelUHC(Language.KIT_GAME_RULES_HYPIXELUHC_RULES.toString(), Language.KIT_GAME_RULES_HYPIXELUHC_DESCRIPTION.toString()),
		spleef(Language.KIT_GAME_RULES_SPLEEF_RULES.toString(), Language.KIT_GAME_RULES_SPLEEF_DESCRIPTION.toString()),
		healthRegeneration(Language.KIT_GAME_RULES_HEALTH_REGENERATION_RULES.toString(), Language.KIT_GAME_RULES_HEALTH_REGENERATION_DESCRIPTION.toString()),
		showHealth(Language.KIT_GAME_RULES_SHOW_HEALTH_RULES.toString(), Language.KIT_GAME_RULES_SHOW_HEALTH_DESCRIPTION.toString()),
		foodLevelChange(Language.KIT_GAME_RULES_FOOD_LEVEL_CHANGE_RULES.toString(), Language.KIT_GAME_RULES_FOOD_LEVEL_CHANGE_DESCRIPTION.toString()),
		point(Language.KIT_GAME_RULES_POINT_RULES.toString(), Language.KIT_GAME_RULES_POINT_DESCRIPTION.toString()),
		rankedPoint(Language.KIT_GAME_RULES_RANKED_POINT_RULES.toString(), Language.KIT_GAME_RULES_RANKED_POINT_DESCRIPTION.toString()),
		resetArenaWhenGetPoint(Language.KIT_GAME_RULES_RESET_ARENA_WHEN_GET_POINT_RULES.toString(), Language.KIT_GAME_RULES_RESET_ARENA_WHEN_GET_POINT_DESCRIPTION.toString()),
		build(Language.KIT_GAME_RULES_BUILD_RULES.toString(), Language.KIT_GAME_RULES_BUILD_DESCRIPTION.toString()),
		startFreeze(Language.KIT_GAME_RULES_START_FREEZE_RULES.toString(), Language.KIT_GAME_RULES_START_FREEZE_DESCRIPTION.toString()),
		noDamage(Language.KIT_GAME_RULES_NO_DAMAGE_RULES.toString(), Language.KIT_GAME_RULES_NO_DAMAGE_DESCRIPTION.toString()),
		instantGapple(Language.KIT_GAME_RULES_INSTANT_GAPPLE_RULES.toString(), Language.KIT_GAME_RULES_INSTANT_GAPPLE_DESCRIPTION.toString()),
		enderPearlCooldown(Language.KIT_GAME_RULES_ENDER_PEARL_COOLDOWN_RULES.toString(), Language.KIT_GAME_RULES_ENDER_PEARL_COOLDOWN_DESCRIPTION.toString()),
		clearBlock(Language.KIT_GAME_RULES_CLEAR_BLOCK_RULES.toString(), Language.KIT_GAME_RULES_CLEAR_BLOCK_DESCRIPTION.toString()),
		dropItemWhenDie(Language.KIT_GAME_RULES_DROP_ITEM_WHEN_DIE_RULES.toString(), Language.KIT_GAME_RULES_DROP_ITEM_WHEN_DIE_DESCRIPTION.toString()),
		noFallDamage(Language.KIT_GAME_RULES_NO_FALL_DAMAGE_RULES.toString(), Language.KIT_GAME_RULES_NO_FALL_DAMAGE_DESCRIPTION.toString()),
		giveBackArrow(Language.KIT_GAME_RULES_GIVE_BACK_ARROW_RULES.toString(), Language.KIT_GAME_RULES_GIVE_BACK_ARROW_DESCRIPTION.toString()),

		respawnTime(Language.KIT_GAME_RULES_RESPAWN_TIME_RULES.toString(), Language.KIT_GAME_RULES_RESPAWN_TIME_DESCRIPTION.toString()),
		maximumPoints(Language.KIT_GAME_RULES_MAXIMUM_POINTS_RULES.toString(), Language.KIT_GAME_RULES_MAXIMUM_POINTS_DESCRIPTION.toString()),
		;

		private final String rule;
		private final String description;
	}

}
