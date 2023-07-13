package rip.diamond.practice.match.team;

import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.chat.BaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.github.paperspigot.Title;
import rip.diamond.practice.util.CC;
import rip.diamond.practice.util.Common;
import rip.diamond.practice.util.TitleSender;
import rip.diamond.practice.util.serialization.LocationSerialization;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class Team {

	@Setter private TeamPlayer leader;
	@Getter @Setter private TeamColor teamColor;
	private final List<TeamPlayer> teamPlayers;
	@Setter private Location spawnLocation;
	@Getter @Setter private Location bedLocation;
	@Getter @Setter private boolean bedDestroyed = false;
	@Getter private int points = 0;

	public Team(TeamPlayer leader) {
		this.leader = leader;
		this.teamColor = TeamColor.WHITE;
		this.teamPlayers = new ArrayList<>();
		this.teamPlayers.add(this.leader);
	}

	public boolean isLeader(UUID uuid) {
		return this.leader.getUuid().equals(uuid);
	}

	public boolean containsPlayer(Player player) {
		for (TeamPlayer playerInfo : this.teamPlayers) {
			if (playerInfo.getUuid().equals(player.getUniqueId())) {
				return true;
			}
		}

		return false;
	}

	public List<UUID> getPlayersUUID() {
		List<UUID> players = new ArrayList<>();
		this.teamPlayers.forEach(matchPlayer -> players.add(matchPlayer.getUuid()));
		return players;
	}

	public List<Player> getPlayers() {
		List<Player> players = new ArrayList<>();

		this.teamPlayers.forEach(matchPlayer -> {
			Player player = matchPlayer.getPlayer();
			if (player != null) {
				players.add(player);
			}
		});

		return players;
	}

	/**
	 * Returns a list of objects that extend {@link TeamPlayer} whose {@link TeamPlayer#isAlive()} returns true.
	 *
	 * @return A list of team players that are alive.
	 */
	public List<TeamPlayer> getAliveTeamPlayers() {
		List<TeamPlayer> alive = new ArrayList<>();

		this.teamPlayers.forEach(teamPlayer -> {
			if (teamPlayer.isAlive()) {
				alive.add(teamPlayer);
			}
		});

		return alive;
	}

	/**
	 * Returns an integer that is incremented for each {@link TeamPlayer} element in the {@code teamPlayers} list whose
	 * {@link TeamPlayer#isAlive()} returns true.
	 * <p>
	 * Use this method rather than calling {@link List#size()} on the result of {@code getAliveTeamPlayers}.
	 *
	 * @return The count of team players that are alive.
	 */
	public int getAliveCount() {
		if (this.teamPlayers.size() == 1) {
			return this.leader.isAlive() ? 1 : 0;
		} else {
			int alive = 0;

			for (TeamPlayer teamPlayer : this.teamPlayers) {
				if (teamPlayer.isAlive()) {
					alive++;
				}
			}

			return alive;
		}
	}

	public int getDisconnectedCount() {
		int disconnected = 0;

		for (TeamPlayer teamPlayer : getTeamPlayers()) {
			if (teamPlayer.isDisconnected()) {
				disconnected++;
			}
		}

		return disconnected;
	}

	public int getHits() {
		return teamPlayers.stream().mapToInt(TeamPlayer::getHits).sum();
	}

	public int getGotHits() {
		return teamPlayers.stream().mapToInt(TeamPlayer::getGotHits).sum();
	}

	public int getCombo() {
		return teamPlayers.stream().mapToInt(TeamPlayer::getCombo).sum();
	}

	/**
	 * Returns a list of objects that extend {@link TeamPlayer} whose {@link TeamPlayer#isAlive()} returns false.
	 *
	 * @return A list of team players that are dead.
	 */
	public List<TeamPlayer> getDeadTeamPlayers() {
		List<TeamPlayer> dead = new ArrayList<>();

		this.teamPlayers.forEach(teamPlayer -> {
			if (!teamPlayer.isAlive()) {
				dead.add(teamPlayer);
			}
		});

		return dead;
	}

	/**
	 * Subtracts the result of {@code getAliveCount} from the size of the {@code teamPlayers} list.
	 *
	 * @return The count of team players that are dead.
	 */
	public int getDeadCount() {
		return this.teamPlayers.size() - this.getAliveCount();
	}

	public boolean isEliminated() {
		return teamPlayers.stream().noneMatch(TeamPlayer::isAlive);
	}

	public Location getSpawnLocation() {
		return spawnLocation.clone().add(0, 1, 0);
	}

	public void broadcast(String messages) {
		this.getPlayers().forEach(player -> player.sendMessage(messages));
	}

	public void broadcastTitle(String title, String subtitle) {
		broadcastTitle(title, subtitle, 5, 60, 5);
	}

	public void broadcastTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
		this.getPlayers().forEach(player -> {
			TitleSender.sendTitle(player, title, PacketPlayOutTitle.EnumTitleAction.TITLE, fadeIn, stay, fadeOut);
			TitleSender.sendTitle(player, subtitle, PacketPlayOutTitle.EnumTitleAction.SUBTITLE, fadeIn, stay, fadeOut);
		});
	}

	public void broadcast(List<String> messages) {
		this.getPlayers().forEach(player -> messages.forEach(player::sendMessage));
	}

	public void broadcastComponents(List<BaseComponent[]> components) {
		this.getPlayers().forEach(player -> components.forEach(array -> player.spigot().sendMessage(array)));
	}

	public void teleport(Location location) {
		if (!location.getChunk().isLoaded()) {
			Common.debug(LocationSerialization.toReadable(location) + CC.RED + " 的區塊還沒加載, 可是系統正在傳送玩家到該位置, 這可能會造成伺服器卡頓, 請盡快修復");
		}
		for (TeamPlayer teamPlayer : getTeamPlayers()) {
			if (teamPlayer.getPlayer() == null || teamPlayer.isDisconnected()) {
				continue;
			}
			teamPlayer.teleport(location);
		}
	}

	public void handlePoint() {
		points++;
	}

	public void dye(TeamPlayer teamPlayer) {
		Player player = teamPlayer.getPlayer();
		if (player == null) {
			return;
		}
		for (ItemStack armorContent : player.getInventory().getArmorContents()) {
			if (armorContent == null || armorContent.getType() == Material.AIR) {
				continue;
			}
			if (armorContent.getType().name().contains("LEATHER")) {
				LeatherArmorMeta meta = (LeatherArmorMeta) armorContent.getItemMeta();
				meta.setColor(Color.fromRGB(getTeamColor().getRgb()));
				armorContent.setItemMeta(meta);
			}
		}
		for (ItemStack content : player.getInventory().getContents()) {
			if (content == null || content.getType() == Material.AIR) {
				continue;
			}
			if (content.getType() == Material.WOOL || content.getType() == Material.STAINED_CLAY) {
				content.setDurability((short) getTeamColor().getDyeColor().ordinal());
			}
		}
	}

}
