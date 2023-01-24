package systems.kinau.fishingbot.websocket.client;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.websocket.NetworkHandle;
import systems.kinau.fishingbot.websocket.Packet;
import systems.kinau.fishingbot.websocket.SocketLaunch;
import systems.kinau.fishingbot.websocket.packets.client.JoinSocketPacket;
import systems.kinau.fishingbot.websocket.packets.client.LeftSocketPacket;

import java.net.URI;

public class SocketClient extends WebSocketClient {
    public SocketClient(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        send(new JoinSocketPacket("[C] "+FishingBot.getInstance().getCurrentBot().getAuthData().getUsername()));
        SocketLaunch.info("成功连接服务器!");
    }

    @Override
    public void onMessage(String s) {
        //SocketLaunch.info("[Client] ReceivePacket: "+s);
        Packet packet = NetworkHandle.readPacket(s);
        if (packet.name.isEmpty()) return;
        NetworkHandle.applyPacket(packet);
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        send(new LeftSocketPacket("[C] "+FishingBot.getInstance().getCurrentBot().getAuthData().getUsername()));
        SocketLaunch.info("[Client] Stopping Socket Connect!");
    }

    @Override
    public void onError(Exception e) {

    }

    public void sendDebug(String s) {
        SocketLaunch.info("SendPacket: "+ s);
        send(s);
    }

    public void send(Packet packet) {
        send(packet.toString());
    }
}
