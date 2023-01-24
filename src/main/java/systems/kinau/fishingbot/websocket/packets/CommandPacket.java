package systems.kinau.fishingbot.websocket.packets;

import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.websocket.Packet;

public class CommandPacket extends Packet {
    public CommandPacket(String command,boolean botCmd) {
        super("command_"+(botCmd ? "a" : "b"), command);
    }

    @Override
    public void apply() {
        boolean botCmd = name.substring("command_".length()).equals("a");
        FishingBot.getInstance().getCurrentBot().runCommand(action,botCmd);
        super.apply();
    }
}
