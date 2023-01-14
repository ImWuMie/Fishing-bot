package systems.kinau.fishingbot.modules.command.commands;

import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.modules.command.Command;
import systems.kinau.fishingbot.modules.command.CommandExecutor;

public class SlotCommand extends Command {
    public SlotCommand() {
        super("slot", "切换你的slot", "slot");
    }

    @Override
    public void onCommand(String label, String[] args, CommandExecutor executor) {
        if (args.length == 1) {
            int slot = Integer.parseInt(args[0]) - 1;
            if (slot < 0 || slot > 8) {
                sendMessage(executor, "command-rightclick-invalid-slot", slot + 1);
                return;
            }
            FishingBot.getInstance().getCurrentBot().getPlayer().setHeldSlot(slot);
        }
    }
}
