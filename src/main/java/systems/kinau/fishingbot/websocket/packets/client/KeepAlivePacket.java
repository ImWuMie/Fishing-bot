package systems.kinau.fishingbot.websocket.packets.client;

import systems.kinau.fishingbot.websocket.Packet;

public class KeepAlivePacket extends Packet {
    public KeepAlivePacket() {
        super("keepalive", "null");
    }
}
