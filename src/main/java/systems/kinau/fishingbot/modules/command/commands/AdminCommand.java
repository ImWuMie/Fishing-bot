package systems.kinau.fishingbot.modules.command.commands;

import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.modules.ChatBotModule;
import systems.kinau.fishingbot.modules.command.Command;
import systems.kinau.fishingbot.modules.command.CommandExecutor;

public class AdminCommand extends Command {
    public AdminCommand() {
        super("admin", "添加chatbot的信任/管理成员", "admin");
    }

    @Override
    public void onCommand(String label, String[] args, CommandExecutor executor) {
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("get")) {
                FishingBot.getLog().info("Admin(s) >");
                int y = 0;
                for (String s : ChatBotModule.admins) {
                    y++;
                    FishingBot.getLog().info(y + ". " + s);
                }
            }
        }
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("add")) {
                ChatBotModule.addAdmin(args[1]);
            }
            if (args[0].equalsIgnoreCase("del")) {
                ChatBotModule.delAdmin(args[1]);
            }
            if (args[0].equalsIgnoreCase("set")) {
                String splitChar = args[1];
                FishingBot.getInstance().getConfig().setSplitChar(splitChar);
                FishingBot.getInstance().getConfig().save();
            }
        }
        if (args.length != 2 && args.length != 1) {
            FishingBot.getLog().warning("Using /admin add/del/get <name>   --添加/移除/获取信任成员");
        }
    }
}
