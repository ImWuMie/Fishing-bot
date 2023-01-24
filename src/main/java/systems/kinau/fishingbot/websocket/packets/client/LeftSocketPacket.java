package systems.kinau.fishingbot.websocket.packets.client;

import systems.kinau.fishingbot.websocket.Packet;
import systems.kinau.fishingbot.websocket.SocketLaunch;
import systems.kinau.fishingbot.websocket.server.SocketServer;

public class LeftSocketPacket extends Packet {
    public LeftSocketPacket(String id) {
        super("left", id);
    }

    @Override
    public void apply() {
        SocketServer server = SocketLaunch.mainServer;
        SocketLaunch.info("ID为: "+action + " 的断开连接.");
        server.ids.remove(action);
        super.apply();
    }
}
