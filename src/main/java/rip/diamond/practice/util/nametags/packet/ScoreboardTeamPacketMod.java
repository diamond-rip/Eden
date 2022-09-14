package rip.diamond.practice.util.nametags.packet;

import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardTeam;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;

public final class ScoreboardTeamPacketMod {

	private final PacketPlayOutScoreboardTeam packet;

	public ScoreboardTeamPacketMod(String name, String prefix, String suffix, Collection<String> players, int paramInt) {
		packet = new PacketPlayOutScoreboardTeam();

		setField("a", name);
		setField("h", paramInt);

		if(paramInt == 0 || paramInt == 2) {
			setField("b", name);
			setField("c", prefix);
			setField("d", suffix);
			setField("i", 1);
		}

		if (paramInt == 0) addAll(players);
	}

	public ScoreboardTeamPacketMod(String name, Collection<String> players, int paramInt) {
		packet = new PacketPlayOutScoreboardTeam();

		if (players == null) players = new ArrayList<String>();

		setField("a", name);
		setField("h", paramInt);
		addAll(players);
	}

	public void sendToPlayer(Player bukkitPlayer) {
		((CraftPlayer) bukkitPlayer).getHandle().playerConnection.sendPacket(packet);
	}

	private void setField(String field, Object value) {
		try {
			Field fieldObject = packet.getClass().getDeclaredField(field);

			fieldObject.setAccessible(true);
			fieldObject.set(packet, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void addAll(Collection col) {
		try {
			Field fieldObject = packet.getClass().getDeclaredField("g");

			fieldObject.setAccessible(true);
			((Collection) fieldObject.get(packet)).addAll(col);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}