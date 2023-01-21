package systems.kinau.fishingbot.websocket.client;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import systems.kinau.fishingbot.websocket.NetworkHandle;
import systems.kinau.fishingbot.websocket.Packet;
import systems.kinau.fishingbot.websocket.SocketLaunch;

import java.net.URI;

public class SocketClient extends WebSocketClient {
    public SocketClient(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        SocketLaunch.info("成功连接服务器!");
    }

    @Override
    public void onMessage(String s) {
        //SocketLaunch.info("[Client] ReceivePacket: "+s);
        Packet packet = NetworkHandle.readPacket(s);
        if (packet.name.isEmpty()) return;
        PlayHandle.applyPacket(packet);
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        SocketLaunch.info("[Client] Stopping Socket Connect!");
    }

    @Override
    public void onError(Exception e) {

    }

    public void sendDebug(String s) {
        SocketLaunch.info("SendPacket: "+ s);
        send(s);
    }
}
