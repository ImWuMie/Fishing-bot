package systems.kinau.fishingbot.websocket.client;

import systems.kinau.fishingbot.websocket.NetworkHandle;
import systems.kinau.fishingbot.websocket.Packet;

public class PlayHandle {
    public static void applyPacket(Packet packet) {
        NetworkHandle.getPacket(packet).apply();
    }
}
