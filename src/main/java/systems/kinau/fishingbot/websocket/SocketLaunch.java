package systems.kinau.fishingbot.websocket;

import org.java_websocket.enums.ReadyState;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.websocket.client.SocketClient;
import systems.kinau.fishingbot.websocket.packets.client.KeepAlivePacket;
import systems.kinau.fishingbot.websocket.server.SocketServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class SocketLaunch {
    public static SocketServer mainServer;
    public static SocketClient mainClient;

    public static List<Packet> packets = new ArrayList<>();

    public static void startClient() throws URISyntaxException, InterruptedException {
        packets.add(new KeepAlivePacket());

        SocketClient client = new SocketClient(new URI(FishingBot.getInstance().getConfig().getSocketIP()));
        client.connect();
        mainClient = client;
        while (true) {
            if (!client.getReadyState().equals(ReadyState.OPEN)) {
                info("正在连接...");
                Thread.sleep(3000);
                continue;
            }

            client.send(new KeepAlivePacket().toString());
            Thread.sleep(5000);
        }
    }

    public static void start() throws InterruptedException, IOException {
        packets.add(new KeepAlivePacket());
        SocketServer server = new SocketServer(FishingBot.getInstance().getConfig().getSocketPort());
        server.start();
        mainServer = server;
        info("--正在启动WebSocket服务--");
        info("Port: " + server.getPort());
        BufferedReader sIn = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            String in = sIn.readLine();
            if (in.equals("exit")) {
                server.stop(1000);
                break;
            }
        }
    }

    public static void info(String message) {
        FishingBot.getLog().info("[Socket] " + message);
    }
}
