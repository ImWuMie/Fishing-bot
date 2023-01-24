package systems.kinau.fishingbot.modules.command.commands;

import systems.kinau.fishingbot.modules.command.Command;
import systems.kinau.fishingbot.modules.command.CommandExecutor;
import systems.kinau.fishingbot.websocket.SocketLaunch;
import systems.kinau.fishingbot.websocket.packets.CommandPacket;
import systems.kinau.fishingbot.websocket.packets.QQActionPacket;
import systems.kinau.fishingbot.websocket.packets.SayPacket;

public class PacketCommand extends Command {
    public PacketCommand() {
        super("packet", "发送packet执行指令", "packet");
    }

    @Override
    public void onCommand(String label, String[] args, CommandExecutor executor) {
        switch (args[0]) {
            case "chat" : {
                int i = 0;
                StringBuilder message = new StringBuilder();
                for (String s : args) {
                    i++;
                    if (i == 1) continue;
                    message.append(s).append(" ");
                }
                if (message.toString().endsWith(" ")) message.setLength(message.length()-1);

                if (SocketLaunch.mainClient != null) SocketLaunch.mainClient.sendDebug(new SayPacket(message.toString()).toString());
                if (SocketLaunch.mainServer != null) SocketLaunch.mainServer.broadcast(new SayPacket(message.toString()).toString());
            }

            case "qq" : {
                String group = args[1];
                int i = 0;
                StringBuilder message = new StringBuilder();
                for (String s : args) {
                    i++;
                    if (i == 1 || i == 2) continue;
                    message.append(s).append(" ");
                }
                if (message.toString().endsWith(" ")) message.setLength(message.length()-1);

                SocketLaunch.info("已发送Action为: "+ QQActionPacket.Action.message.name()+" 内容为: "+message.toString()+" QQ群为: "+group);
                if (SocketLaunch.mainClient != null) new QQActionPacket(QQActionPacket.Action.message,message.toString(),group,"").apply();
                if (SocketLaunch.mainServer != null) new QQActionPacket(QQActionPacket.Action.message,message.toString(),group,"").apply();
            }

            case "cmd" : {
                int i = 0;
                StringBuilder message = new StringBuilder();
                for (String s : args) {
                    i++;
                    if (i == 1) continue;
                    message.append(s).append(" ");
                }
                if (message.toString().endsWith(" ")) message.setLength(message.length()-1);

                if (!message.toString().startsWith("/")) break;
                if (SocketLaunch.mainClient != null) SocketLaunch.mainClient.sendDebug(new CommandPacket(message.toString(),false).toString());
                if (SocketLaunch.mainServer != null) SocketLaunch.mainServer.broadcast(new CommandPacket(message.toString(),false).toString());
            }

            case "botcmd" : {
                int i = 0;
                StringBuilder message = new StringBuilder();
                for (String s : args) {
                    i++;
                    if (i == 1) continue;
                    message.append(s).append(" ");
                }
                if (message.toString().endsWith(" ")) message.setLength(message.length()-1);

                if (!message.toString().startsWith("/")) break;
                if (SocketLaunch.mainClient != null) SocketLaunch.mainClient.sendDebug(new CommandPacket(message.toString(),true).toString());
                if (SocketLaunch.mainServer != null) SocketLaunch.mainServer.broadcast(new CommandPacket(message.toString(),true).toString());
            }
        }
    }
}
