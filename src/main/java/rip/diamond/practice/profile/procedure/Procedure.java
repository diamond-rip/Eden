package rip.diamond.practice.profile.procedure;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.util.CC;
import rip.diamond.practice.util.Common;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

@Getter
@RequiredArgsConstructor
public class Procedure {

    @Getter private static final Map<UUID, Procedure> procedures = new HashMap<>();

    private final UUID uuid;
    private final ProcedureType procedureType;
    private final Consumer<Object> callback;

    public static void buildProcedure(Player player, ProcedureType procedureType, Consumer<Object> callback) {
        buildProcedure(player, null, procedureType, callback);
    }

    public static void buildProcedure(Player player, String instructions, ProcedureType procedureType, Consumer<Object> callback) {
        Common.sendMessage(player, instructions == null ? null : CC.YELLOW + CC.BOLD + instructions, Language.PROFILE_PROCEDURE_ENTER_CANCEL_TO_CANCEL.toString());

        Procedure procedure = new Procedure(player.getUniqueId(), procedureType, callback);
        procedures.put(player.getUniqueId(), procedure);
    }

    public void call(Object o) {
        remove();
        callback.accept(o);
    }

    public void remove() {
        procedures.remove(uuid);
    }

}
