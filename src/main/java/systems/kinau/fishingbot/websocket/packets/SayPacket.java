package systems.kinau.fishingbot.websocket.packets;

import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.websocket.Packet;

public class SayPacket extends Packet {
    public SayPacket(String message) {
        super("say", message);
    }

    @Override
    public void apply() {
        FishingBot.getInstance().getCurrentBot().getPlayer().sendMessage(action);
        super.apply();
    }
}
