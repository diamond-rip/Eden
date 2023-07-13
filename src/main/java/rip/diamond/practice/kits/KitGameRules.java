package rip.diamond.practice.kits;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.kits.menu.button.impl.KitRulesSetIntegerButton;
import rip.diamond.practice.kits.menu.button.impl.KitRulesSetStringButton;
import rip.diamond.practice.kits.menu.button.impl.KitRulesToggleButton;
import rip.diamond.practice.match.Match;
import rip.diamond.practice.queue.QueueType;

import java.lang.reflect.Field;

// TODO: 8/7/2023 Add time limit (#420)

@Setter
public class KitGameRules implements Cloneable {

	@Getter private boolean receiveKitLoadoutBook = true;
	@Getter private boolean deathOnWater = false;
	@Getter private boolean boxing = false;
	@Getter private boolean bed = false;
	@Getter private boolean breakGoal = false;
	@Getter private boolean portalGoal = false;
	@Getter private boolean projectileOnly = false;
	@Getter private boolean hypixelUHC = false;
	@Getter private boolean spleef = false;
	@Getter private boolean healthRegeneration = true;
	@Getter private boolean showHealth = true;
	@Getter private boolean foodLevelChange = true;
	private boolean point = false;
	private boolean rankedPoint = false;
	@Getter private boolean resetArenaWhenGetPoint = false;
	@Getter private boolean onlyLoserResetPositionWhenGetPoint = false;
	@Getter private boolean build = false;
	@Getter private boolean startFreeze = false;
	@Getter private boolean noDamage = false;
	@Getter private boolean instantGapple = false;
	@Getter private boolean enderPearlCooldown = false;
	@Getter private boolean clearBlock = false;
	@Getter private boolean dropItemWhenDie = true;
	@Getter private boolean noFallDamage = false;
	@Getter private boolean giveBackArrow = false;
	@Getter private boolean dropItems = true;
	@Getter private boolean teamProjectile = true;
	@Getter private boolean bowBoosting = true;
	@Getter private int respawnTime = 5;
	@Getter private int maximumPoints = 3;
	@Getter private int matchCountdownDuration = 5;
	@Getter private int newRoundTime = 5;
	@Getter private int clearBlockTime = 10;
	@Getter private String knockbackName = "default";

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

	@Override
	public KitGameRules clone() {
		KitGameRules rules = new KitGameRules();
		for (Field field : this.getClass().getDeclaredFields()) {
			field.setAccessible(true);
		}
		for (Field field : rules.getClass().getDeclaredFields()) {
			field.setAccessible(true);
			try {
				Object obj = this.getClass().getDeclaredField(field.getName()).get(this);
				field.set(rules, obj);
			} catch (IllegalAccessException | NoSuchFieldException e) {
				throw new RuntimeException(e);
			}
		}
		return rules;
	}

	@Getter
	@AllArgsConstructor
	public enum Readable {
		//boolean
		receiveKitLoadoutBook(Language.KIT_GAME_RULES_RECEIVE_KIT_LOADOUT_BOOK_RULES.toString(), Language.KIT_GAME_RULES_RECEIVE_KIT_LOADOUT_BOOK_DESCRIPTION.toString()),
		deathOnWater(Language.KIT_GAME_RULES_DEATH_ON_WATER_RULES.toString(), Language.KIT_GAME_RULES_DEATH_ON_WATER_DESCRIPTION.toString()),
		boxing(Language.KIT_GAME_RULES_BOXING_RULES.toString(), Language.KIT_GAME_RULES_BOXING_DESCRIPTION.toString()),
		bed(Language.KIT_GAME_RULES_BED_RULES.toString(), Language.KIT_GAME_RULES_BED_DESCRIPTION.toString()),
		breakGoal(Language.KIT_GAME_RULES_BREAK_GOAL_RULES.toString(), Language.KIT_GAME_RULES_BREAK_GOAL_DESCRIPTION.toString()),
		portalGoal(Language.KIT_GAME_RULES_PORTAL_GOAL_RULES.toString(), Language.KIT_GAME_RULES_PORTAL_GOAL_DESCRIPTION.toString()),
		projectileOnly(Language.KIT_GAME_RULES_PROJECTILE_ONLY_RULES.toString(), Language.KIT_GAME_RULES_PROJECTILE_ONLY_DESCRIPTION.toString()),
		hypixelUHC(Language.KIT_GAME_RULES_HYPIXELUHC_RULES.toString(), Language.KIT_GAME_RULES_HYPIXELUHC_DESCRIPTION.toString()),
		spleef(Language.KIT_GAME_RULES_SPLEEF_RULES.toString(), Language.KIT_GAME_RULES_SPLEEF_DESCRIPTION.toString()),
		healthRegeneration(Language.KIT_GAME_RULES_HEALTH_REGENERATION_RULES.toString(), Language.KIT_GAME_RULES_HEALTH_REGENERATION_DESCRIPTION.toString()),
		showHealth(Language.KIT_GAME_RULES_SHOW_HEALTH_RULES.toString(), Language.KIT_GAME_RULES_SHOW_HEALTH_DESCRIPTION.toString()),
		foodLevelChange(Language.KIT_GAME_RULES_FOOD_LEVEL_CHANGE_RULES.toString(), Language.KIT_GAME_RULES_FOOD_LEVEL_CHANGE_DESCRIPTION.toString()),
		point(Language.KIT_GAME_RULES_POINT_RULES.toString(), Language.KIT_GAME_RULES_POINT_DESCRIPTION.toString()),
		rankedPoint(Language.KIT_GAME_RULES_RANKED_POINT_RULES.toString(), Language.KIT_GAME_RULES_RANKED_POINT_DESCRIPTION.toString()),
		resetArenaWhenGetPoint(Language.KIT_GAME_RULES_RESET_ARENA_WHEN_GET_POINT_RULES.toString(), Language.KIT_GAME_RULES_RESET_ARENA_WHEN_GET_POINT_DESCRIPTION.toString()),
		onlyLoserResetPositionWhenGetPoint(Language.KIT_GAME_RULES_ONLY_LOSER_RESET_POSITION_WHEN_GET_POINT_RULES.toString(), Language.KIT_GAME_RULES_ONLY_LOSER_RESET_POSITION_WHEN_GET_POINT_DESCRIPTION.toString()),
		build(Language.KIT_GAME_RULES_BUILD_RULES.toString(), Language.KIT_GAME_RULES_BUILD_DESCRIPTION.toString()),
		startFreeze(Language.KIT_GAME_RULES_START_FREEZE_RULES.toString(), Language.KIT_GAME_RULES_START_FREEZE_DESCRIPTION.toString()),
		noDamage(Language.KIT_GAME_RULES_NO_DAMAGE_RULES.toString(), Language.KIT_GAME_RULES_NO_DAMAGE_DESCRIPTION.toString()),
		instantGapple(Language.KIT_GAME_RULES_INSTANT_GAPPLE_RULES.toString(), Language.KIT_GAME_RULES_INSTANT_GAPPLE_DESCRIPTION.toString()),
		enderPearlCooldown(Language.KIT_GAME_RULES_ENDER_PEARL_COOLDOWN_RULES.toString(), Language.KIT_GAME_RULES_ENDER_PEARL_COOLDOWN_DESCRIPTION.toString()),
		clearBlock(Language.KIT_GAME_RULES_CLEAR_BLOCK_RULES.toString(), Language.KIT_GAME_RULES_CLEAR_BLOCK_DESCRIPTION.toString()),
		dropItemWhenDie(Language.KIT_GAME_RULES_DROP_ITEM_WHEN_DIE_RULES.toString(), Language.KIT_GAME_RULES_DROP_ITEM_WHEN_DIE_DESCRIPTION.toString()),
		noFallDamage(Language.KIT_GAME_RULES_NO_FALL_DAMAGE_RULES.toString(), Language.KIT_GAME_RULES_NO_FALL_DAMAGE_DESCRIPTION.toString()),
		giveBackArrow(Language.KIT_GAME_RULES_GIVE_BACK_ARROW_RULES.toString(), Language.KIT_GAME_RULES_GIVE_BACK_ARROW_DESCRIPTION.toString()),
		dropItems(Language.KIT_GAME_RULES_DROP_ITEMS_RULES.toString(), Language.KIT_GAME_RULES_DROP_ITEMS_DESCRIPTION.toString()),
		teamProjectile(Language.KIT_GAME_RULES_TEAM_PROJECTILE_RULES.toString(), Language.KIT_GAME_RULES_TEAM_PROJECTILE_DESCRIPTION.toString()),
		bowBoosting(Language.KIT_GAME_RULES_BOW_BOOSTING_RULES.toString(), Language.KIT_GAME_RULES_BOW_BOOSTING_DESCRIPTION.toString()),
		//integer
		respawnTime(Language.KIT_GAME_RULES_RESPAWN_TIME_RULES.toString(), Language.KIT_GAME_RULES_RESPAWN_TIME_DESCRIPTION.toString()),
		maximumPoints(Language.KIT_GAME_RULES_MAXIMUM_POINTS_RULES.toString(), Language.KIT_GAME_RULES_MAXIMUM_POINTS_DESCRIPTION.toString()),
		matchCountdownDuration(Language.KIT_GAME_RULES_MATCH_COUNTDOWN_DURATION_RULES.toString(), Language.KIT_GAME_RULES_MATCH_COUNTDOWN_DURATION_DESCRIPTION.toString()),
		newRoundTime(Language.KIT_GAME_RULES_NEW_ROUND_TIME_RULES.toString(), Language.KIT_GAME_RULES_NEW_ROUND_TIME_DESCRIPTION.toString()),
		clearBlockTime(Language.KIT_GAME_RULES_CLEAR_BLOCK_TIME_RULES.toString(), Language.KIT_GAME_RULES_CLEAR_BLOCK_TIME_DESCRIPTION.toString()),
		//String
		knockbackName(Language.KIT_GAME_RULES_KNOCKBACK_NAME_RULES.toString(), Language.KIT_GAME_RULES_KNOCKBACK_NAME_DESCRIPTION.toString()),
		;

		private final String rule;
		private final String description;
	}

}
