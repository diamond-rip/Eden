package rip.diamond.practice.kits.command;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.util.CC;
import rip.diamond.practice.util.Checker;
import rip.diamond.practice.util.Common;
import rip.diamond.practice.util.command.Command;
import rip.diamond.practice.util.command.CommandArgs;
import rip.diamond.practice.util.command.argument.CommandArguments;
import rip.diamond.practice.util.exception.PracticeUnexpectedException;
import rip.diamond.practice.util.serialization.BukkitSerialization;

public class GoldenHeadCommand extends Command {
    @CommandArgs(name = "goldenhead", permission = "eden.command.goldenhead")
    public void execute(CommandArguments command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        ItemStack goldenHead = BukkitSerialization.itemStackFromBase64("rO0ABXNyABpvcmcuYnVra2l0LnV0aWwuaW8uV3JhcHBlcvJQR+zxEm8FAgABTAADbWFwdAAPTGphdmEvdXRpbC9NYXA7eHBzcgA1Y29tLmdvb2dsZS5jb21tb24uY29sbGVjdC5JbW11dGFibGVNYXAkU2VyaWFsaXplZEZvcm0AAAAAAAAAAAIAAlsABGtleXN0ABNbTGphdmEvbGFuZy9PYmplY3Q7WwAGdmFsdWVzcQB+AAR4cHVyABNbTGphdmEubGFuZy5PYmplY3Q7kM5YnxBzKWwCAAB4cAAAAAR0AAI9PXQABHR5cGV0AAZkYW1hZ2V0AARtZXRhdXEAfgAGAAAABHQAHm9yZy5idWtraXQuaW52ZW50b3J5Lkl0ZW1TdGFja3QAClNLVUxMX0lURU1zcgAPamF2YS5sYW5nLlNob3J0aE03EzRg2lICAAFTAAV2YWx1ZXhyABBqYXZhLmxhbmcuTnVtYmVyhqyVHQuU4IsCAAB4cAADc3EAfgAAc3EAfgADdXEAfgAGAAAABXEAfgAIdAAJbWV0YS10eXBldAAMZGlzcGxheS1uYW1ldAAIaW50ZXJuYWx0AAtza3VsbC1vd25lcnVxAH4ABgAAAAV0AAhJdGVtTWV0YXQABVNLVUxMdAAOwqc2R29sZGVuIEhlYWR0BXhINHNJQUFBQUFBQUFBSzJTeVpLalJoQ0c4WVFkN3Vud1kvalcwU0VvUUlpRER5d1NtNm9RRXZ0bGdsVXFWQWdraEJBODB6eWttWjZPc0gzM3BUSWlzeXIvTC8rc1Y0cDZwZjQ0bkh0Q2RyZW14S1I0b2I0WU9mVW56d3Badm1MNWR6R2hoWGN1VGRsM01lZVc3eHdRYzVyTE9KNEIvQ3YxT2o5cWk5c2RGOTFYNnVWZVBPLzlyZWhlS1lyNjVZWDZlc0RIUy9JajgrVzd4VzZ0cVhrVzdKTzhJZElWZXIrbUEzQzcxRm5ZRzRlYlZrelIzby9qWEJ2TzdzVUVQR1pPVEhHYWozVUZCaUJZZ3hqY1BVWlBPL1Y1WDV3bnQvYWVlbkpjSlFLMkJDdkxyTWNEYkRPZTlDM2c2NnVWbldPRjU4T205c3dIRTRUT1hVZXhXa2Rnb1NPQm13UWk2K3NOdzF3bnpvclIzZ2lRQzA3MVdlWThRRWgxQVhsSnhPd3VQd0dkMHNMWjBzWWhqUlNzR0t6STM4TzBxM2ZqeHV1bnVtdWgyREQyK3V6aTYrN3FlUFhOVy9ianhhRHhibGk4NmJmRGZtMCtIcFp6TlFBZnVBV1BHNkY5c3pIck5ERFM4aXNuaTFvcFBhL0FrNXRyRnErczNxUWRlWnFZMjU1OWl5WXZYL2FLbi9yeW14K3ZoT2FhWHNiN3llVmNmMldQUW94cFoyenk2WHdKMkhKYnlFbTJVMjBhVlVpdzQwdSsxTGlwRUUzQjNna2xIZEhsaGxZY2s2ZkJ0b3NRR0R1MmFQVG1xTmlrWThBSVdkcmNDSGEyWWs3UEJ5WVhBWWFKbUxMKzFpQlhrOG40RFcvU0lkYnFOQk5VVFZ6R0xrS3JSOW5uSzZtenBteWdWeUR6cENrNXdtWENPWm9ERnp5ekZjd3B1QjQwVnpvZ3U4VzdkYk10bXRhb2pjV3dqUGNKZDJHeTFaMHdiaWQzL0tJd2RyYVM3L3pMOE9DZmJDb3BkenlKU0ovS1l5azhIQ1lNZzRNNkRvRVRHUkNmY3Z0eHhHVzB3dU5iWHJXNitMU09HUjZlZVZnV0xkUHNXMHZwL0dNRTcyRWlSMjJwTjN3cGJvOFY1bHU1dHp5ckN0cHJ2ZGIyVFFTRm1ubE9uWGE0SnRkZ2NsSXZPSFVtZmVMalJkcnIwbDh2MUc5K1F2cmlpMXNNemRGUVREb0pHSkt4KzFNYVN0aFFteU4wSTRBcVNNTnBUZHR1UnNQRFlCbUtoRFBkZk1RMTZXS1BuQTBzTFEzRllPQ1VWN0Y2bkpCTFRsRE42S2cySmp0dzVtMDU0eHdIcEVFYXVlc1JIWXhPd2RMUnVNaGpDdUkyMVh3N21uVi85akhMQSt0anFLUFN3SjlhZ0Z6U2VrUG5vVW04Mm4vbUFSbmp3UG5neTNXVGlUK1pjczNuY3QwZjR4QisxSDdPTk9zUXREMTQzSDl6UCs2SFp2ZXAyZVQ2ZnJEeDZ2R3ZIbjBha0Q0TzBCZ0ZNYjJ0ZVpJckloMkhwdytPN2VRQlZLODVORGtBcW1zV2dzMDVyaVNBQU1Kd3JrV0JOMEpWUGtNVkRzaVZadjh5YnY3cURLeTlDYWtHRjFVT2k2WjlOWHZ5aE1HYXNWMVN3Y3JCSDc0b3NyaVZQam5QU1BiVzN2L0xEaUkrY2pPQVhNak91MkZ0OThqSDFid3p6UUN4S2cwdzhDWll5ZWNvZ0RQWHBwcm40V09YRUZSdFRuRGExRWhEQk01NVd6M1ZzSkxZdUpKUHNmb1ArNDlZT3ExSVVTL1VyeWlwQytyM2IxYWYwdDAzaXZvYlBDMWxSb1lGQUFBPXQAB19LdWIwc18=");

        if (goldenHead == null) {
            throw new PracticeUnexpectedException("Generated Golden Head should not be null");
        }

        if (args.length != 1) {
            player.getInventory().addItem(goldenHead);
        } else {
            if (!Checker.isInteger(args[0])) {
                Language.INVALID_SYNTAX.sendMessage(player);
                return;
            }
            goldenHead.setAmount(Integer.parseInt(args[0]));
            player.getInventory().addItem(goldenHead);
        }

        Common.sendMessage(player, CC.YELLOW + "[Eden] Added " + CC.GREEN + (args.length != 1 ? 1 : args[0]) + CC.GOLD + " Golden Head " + CC.YELLOW + "into your inventory.");
    }
}
