package systems.kinau.fishingbot.modules.command.commands;

import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.event.EventHandler;
import systems.kinau.fishingbot.event.Listener;
import systems.kinau.fishingbot.event.play.ChatEvent;
import systems.kinau.fishingbot.modules.command.Command;
import systems.kinau.fishingbot.modules.command.CommandExecutor;

public class MessageCommand extends Command implements Listener {
    private String group_id = "null";
    private String prefix = "[MC]";

    public MessageCommand() {
        super("smessage", "转发服务器消息到指定qq群", "smessage");
    }

    @Override
    public void onCommand(String label, String[] args, CommandExecutor executor) {
        if (args.length == 2) {
            if (args[0].equals("set")) {
                this.group_id = args[1];
                FishingBot.getLog().info("设置QQ群为: " + group_id);
            }
            if (args[0].equals("prefix")) {
                this.prefix = args[1];
                FishingBot.getLog().info("设置Prefix为: " + prefix);
            }
        }

        if (args.length == 1) {
            if (args[0].equals("start")) {
                FishingBot.getLog().info("已开启消息转发QQ群为: " + group_id);
                FishingBot.getInstance().getCurrentBot().getEventManager().registerListener(this);
            }
            if (args[0].equals("stop")) {
                FishingBot.getInstance().getCurrentBot().getEventManager().unregisterListener(this);
                FishingBot.getLog().info("已关闭消息转发");
            }
        }
    }

    @EventHandler
    public void onChat(ChatEvent e) {
        FishingBot.getInstance().getCurrentBot().runCommand("/packet qq "+group_id+" "+prefix+e.getText(),true);
    }
}
