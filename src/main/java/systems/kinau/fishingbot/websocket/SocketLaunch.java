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

    public static boolean clientRunning = false;
    public static boolean serverRunning = false;

    public static List<Packet> packets = new ArrayList<>();

    public static List<EventHandle> acceptsEvent = new ArrayList<>();

    public static void startClient() throws URISyntaxException, InterruptedException {
        packets.add(new KeepAlivePacket());
        SocketClient client = new SocketClient(new URI(FishingBot.getInstance().getConfig().getSocketIP()));
        mainClient = client;
        mainClient.connect();
        clientRunning = true;
        while (clientRunning) {
            if (!client.getReadyState().equals(ReadyState.OPEN)) {
                info("正在连接...");
                Thread.sleep(7500);
                continue;
            }

            for (EventHandle eventHandle : acceptsEvent) {
                eventHandle.onTick();
            }

            //client.send(new KeepAlivePacket().toString());
            Thread.sleep(100);
        }

        mainClient.close();
    }

    public static void addEvent(EventHandle e) {
        acceptsEvent.add(e);
    }

    public static void removeEvent(EventHandle e) {
        acceptsEvent.remove(e);
    }

    public static void start() throws InterruptedException, IOException {
        packets.add(new KeepAlivePacket());
        SocketServer server = new SocketServer(FishingBot.getInstance().getConfig().getSocketPort());
        mainServer = server;
        info("--正在启动WebSocket服务--");
        info("Port: " + server.getPort());
        mainServer.start();
        serverRunning = true;
        BufferedReader sIn = new BufferedReader(new InputStreamReader(System.in));
        while (serverRunning) {
            String in = sIn.readLine();
            if (in.equals("exit")) {
                server.stop(1000);
                break;
            }
            for (EventHandle eventHandle : acceptsEvent) {
                eventHandle.onTick();
            }
            Thread.sleep(100);
        }

        server.stop(1000);
    }

    public static void info(String message) {
        FishingBot.getLog().info("[Socket] " + message);
    }
}
