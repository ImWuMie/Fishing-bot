package systems.kinau.fishingbot.modules.command.commands;

import systems.kinau.fishingbot.modules.command.Command;
import systems.kinau.fishingbot.modules.command.CommandExecutor;
import systems.kinau.fishingbot.websocket.SocketLaunch;
import systems.kinau.fishingbot.websocket.packets.SayPacket;

public class PacketCommand extends Command {
    public PacketCommand() {
        super("packet", "发送packet执行指令", "packet");
    }

    @Override
    public void onCommand(String label, String[] args, CommandExecutor executor) {
        switch (args[0]) {
            case "say" : {
                int i = 0;
                StringBuilder message = new StringBuilder();
                for (String s : args) {
                    i++;
                    if (i == 1) continue;
                    message.append(s).append(" ");
                }
                if (SocketLaunch.mainClient != null) SocketLaunch.mainClient.sendDebug(new SayPacket(message.toString()).toString());
            }
        }
    }
}
