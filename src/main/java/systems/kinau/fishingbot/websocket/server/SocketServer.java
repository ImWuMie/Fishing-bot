package systems.kinau.fishingbot.websocket.server;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import systems.kinau.fishingbot.utils.GsonUtils;
import systems.kinau.fishingbot.websocket.NetworkHandle;
import systems.kinau.fishingbot.websocket.Packet;
import systems.kinau.fishingbot.websocket.SocketLaunch;
import systems.kinau.fishingbot.websocket.packets.QQActionPacket;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public class SocketServer extends WebSocketServer {
    private int connections = 0;
    public List<String> ids = new ArrayList<>();

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
        GMessage message = GsonUtils.jsonToBean(s,GMessage.class);
        if (message.getPost_type().equalsIgnoreCase("message")) {
            QQActionPacket p = new QQActionPacket(QQActionPacket.Action.message, message.raw_message, message.group_id, GsonUtils.beanToJson(message));
            broadcast(p.toString());
            return;
        }
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
