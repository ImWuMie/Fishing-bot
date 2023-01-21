package systems.kinau.fishingbot.websocket.server;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import systems.kinau.fishingbot.websocket.NetworkHandle;
import systems.kinau.fishingbot.websocket.Packet;
import systems.kinau.fishingbot.websocket.SocketLaunch;

import java.net.InetSocketAddress;

public class SocketServer extends WebSocketServer {
    private int connections = 0;

    public SocketServer(int port) {
        super(new InetSocketAddress(port));
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        connections++;
        SocketLaunch.info("一个客户端连接到此,当前连接数: "+connections);
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
        connections--;
        SocketLaunch.info("一个客户端断开连接,当前连接数: "+connections);
    }

    @Override
    public void onMessage(WebSocket webSocket, String s) {
        //SocketLaunch.info("[Server] ReceivePacket: "+s);
        Packet packet = NetworkHandle.readPacket(s);
        if (packet.name.isEmpty()) return;
        NetworkHandle.applyPacket(packet);
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {

    }

    @Override
    public void onStart() {

    }
}
