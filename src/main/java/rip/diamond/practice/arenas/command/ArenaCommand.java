package rip.diamond.practice.arenas.command;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.arenas.Arena;
import rip.diamond.practice.arenas.ArenaDetail;
import rip.diamond.practice.arenas.menu.ArenaEditMenu;
import rip.diamond.practice.arenas.menu.ArenasMenu;
import rip.diamond.practice.profile.procedure.Procedure;
import rip.diamond.practice.profile.procedure.ProcedureType;
import rip.diamond.practice.util.CC;
import rip.diamond.practice.util.Checker;
import rip.diamond.practice.util.command.Command;
import rip.diamond.practice.util.command.CommandArgs;
import rip.diamond.practice.util.command.argument.CommandArguments;

import java.util.Arrays;
import java.util.List;

public class ArenaCommand extends Command {
    @CommandArgs(name = "arena", permission = "eden.command.arena")
    public void execute(CommandArguments command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();
        if (args.length == 0) {
            new ArenasMenu().openMenu(player);
            return;
        }if (args.length == 1) {
            if (args[0].equalsIgnoreCase("list")) {
                new ArenasMenu().openMenu(player);
                return;
            } else if (args[0].equalsIgnoreCase("saveall")) {
                plugin.getArenaFile().getConfiguration().set("arenas", null);
                plugin.getArenaFile().save();

                Arena.getArenas().forEach(Arena::save);
                Language.ARENA_SAVED_ALL.sendMessage(player);
                return;
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("create")) {
                String name = args[1];
                if (Arena.getArena(name) != null) {
                    Language.ARENA_ALREADY_EXISTS.sendMessage(player, name);
                    return;
                }
                Arena arena = new Arena(name);
                arena.getArenaDetails().add(new ArenaDetail(arena));
                Arena.getArenas().add(arena);
                arena.autoSave();
                Language.ARENA_CREATED.sendMessage(player, name);
                return;
            } else if (args[0].equalsIgnoreCase("edit")) {
                String name = args[1];
                Arena arena = Arena.getArena(name);
                if (arena == null) {
                    Language.ARENA_NOT_EXISTS.sendMessage(player, name);
                    return;
                }
                new ArenaEditMenu(arena).openMenu(player);
                return;
            } else if (args[0].equalsIgnoreCase("save")) {
                String name = args[1];
                Arena arena = Arena.getArena(name);
                if (arena == null) {
                    Language.ARENA_NOT_EXISTS.sendMessage(player, name);
                    return;
                }
                arena.save();
                Language.ARENA_SAVED.sendMessage(player, arena.getName());
                return;
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("setup")) {
                String name = args[1];
                Arena arena = Arena.getArena(name);
                if (arena == null) {
                    Language.ARENA_NOT_EXISTS.sendMessage(player, name);
                    return;
                }
                switch (args[2].toLowerCase()) {
                    case "a":
                        if (arena.hasClone()) {
                            Language.ARENA_CANNOT_SET_BECAUSE_CLONE_FOUND.sendMessage(player);
                            return;
                        }
                        arena.setA(player.getLocation());
                        Language.ARENA_SUCCESSFULLY_SET.sendMessage(player, "A");
                        arena.setEdited(true);
                        arena.autoSave();
                        return;
                    case "b":
                        if (arena.hasClone()) {
                            Language.ARENA_CANNOT_SET_BECAUSE_CLONE_FOUND.sendMessage(player);
                            return;
                        }
                        arena.setB(player.getLocation());
                        Language.ARENA_SUCCESSFULLY_SET.sendMessage(player, "B");
                        arena.setEdited(true);
                        arena.autoSave();
                        return;
                    case "spectator":
                        if (arena.hasClone()) {
                            Language.ARENA_CANNOT_SET_BECAUSE_CLONE_FOUND.sendMessage(player);
                            return;
                        }
                        arena.setSpectator(player.getLocation());
                        Language.ARENA_SUCCESSFULLY_SET.sendMessage(player, "Spectator");
                        arena.setEdited(true);
                        arena.autoSave();
                        return;
                    case "min":
                        if (arena.hasClone()) {
                            Language.ARENA_CANNOT_SET_BECAUSE_CLONE_FOUND.sendMessage(player);
                            return;
                        }
                        Procedure.buildProcedure(player, Language.ARENA_EDIT_MIN.toString(arena.getName()), ProcedureType.BREAK_BLOCK, (b) -> {
                            Block block = (Block) b;
                            arena.setMin(block.getLocation());
                            Language.ARENA_SUCCESSFULLY_SET.sendMessage(player, "min");
                            arena.setEdited(true);
                            arena.autoSave();
                        });
                        return;
                    case "max":
                        if (arena.hasClone()) {
                            Language.ARENA_CANNOT_SET_BECAUSE_CLONE_FOUND.sendMessage(player);
                            return;
                        }
                        Procedure.buildProcedure(player, Language.ARENA_EDIT_MAX.toString(arena.getName()), ProcedureType.BREAK_BLOCK, (b) -> {
                            Block block = (Block) b;
                            arena.setMax(block.getLocation());
                            Language.ARENA_SUCCESSFULLY_SET.sendMessage(player, "max");
                            arena.setEdited(true);
                            arena.autoSave();
                        });
                        return;
                    case "y-limit":
                        Procedure.buildProcedure(player, Language.ARENA_EDIT_Y_LIMIT.toString(arena.getName()), ProcedureType.CHAT, (s) -> {
                            String message = (String) s;
                            if (!Checker.isInteger(message)) {
                                Language.INVALID_SYNTAX.sendMessage(player);
                                return;
                            }
                            int i = Integer.parseInt(message);
                            arena.setYLimit(i);
                            Language.ARENA_SUCCESSFULLY_SET.sendMessage(player, "y-limit");
                            arena.setEdited(true);
                            arena.autoSave();
                        });
                        return;
                    case "build-max":
                        Procedure.buildProcedure(player, Language.ARENA_EDIT_BUILD_MAX.toString(arena.getName()), ProcedureType.CHAT, (s) -> {
                            String message = (String) s;
                            if (!Checker.isInteger(message)) {
                                Language.INVALID_SYNTAX.sendMessage(player);
                                return;
                            }
                            int i = Integer.parseInt(message);
                            arena.setBuildMax(i);
                            Language.ARENA_SUCCESSFULLY_SET.sendMessage(player, "build-max");
                            arena.setEdited(true);
                            arena.autoSave();
                        });
                        return;
                    case "portal-protection-radius":
                        Procedure.buildProcedure(player, Language.ARENA_EDIT_PORTAL_PROTECTION_RADIUS.toString(arena.getName()), ProcedureType.CHAT, (s) -> {
                            String message = (String) s;
                            if (!Checker.isInteger(message)) {
                                Language.INVALID_SYNTAX.sendMessage(player);
                                return;
                            }
                            int i = Integer.parseInt(message);
                            arena.setPortalProtectionRadius(i);
                            Language.ARENA_SUCCESSFULLY_SET.sendMessage(player, "portal-protection-radius");
                            arena.setEdited(true);
                            arena.autoSave();
                        });
                        return;
                    case "toggle":
                        if (!arena.isFinishedSetup()) {
                            Language.ARENA_EDIT_CANNOT_EDIT_NOT_FINISHED_SETUP.sendMessage(player);
                            return;
                        }
                        if (arena.isEdited()) {
                            Language.ARENA_EDIT_CANNOT_EDIT_NEED_RESTART.sendMessage(player);
                            return;
                        }
                        arena.setEnabled(!arena.isEnabled());
                        Language.ARENA_SUCCESSFULLY_SET.sendMessage(player, "status " + CC.GRAY + "(" + (arena.isEnabled() ? Language.ENABLED.toString() : Language.DISABLED.toString()) + ")");
                        arena.autoSave();
                        return;
                }
            }
        }
        Language.INVALID_SYNTAX.sendMessage(player);
    }

    @Override
    public List<String> getDefaultTabComplete(CommandArguments command) {
        return Arrays.asList("list", "create", "edit", "setup", "save", "saveall");
    }
}
