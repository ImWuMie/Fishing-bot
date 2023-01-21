package systems.kinau.fishingbot.websocket.packets;

import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.websocket.Packet;

public class CommandPacket extends Packet {
    public CommandPacket(String command,boolean botCmd) {
        super("command", command+(botCmd ? "/-/true" : "/-/false"));
    }

    @Override
    public void apply() {
        String[] s = action.split("/-/");
        FishingBot.getInstance().getCurrentBot().runCommand(s[0],Boolean.parseBoolean(s[1]));
        super.apply();
    }
}
