package systems.kinau.fishingbot.websocket.packets.client;

import systems.kinau.fishingbot.websocket.Packet;
import systems.kinau.fishingbot.websocket.SocketLaunch;
import systems.kinau.fishingbot.websocket.server.SocketServer;

public class JoinSocketPacket extends Packet {
    public JoinSocketPacket(String id) {
        super("join", id);
    }

    @Override
    public void apply() {
        SocketServer server = SocketLaunch.mainServer;
        SocketLaunch.info("ID为: "+action + " 的加入了Socket.");
        server.ids.add(action);
        super.apply();
    }
}
