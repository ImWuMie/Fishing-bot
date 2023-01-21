package systems.kinau.fishingbot.websocket;

import systems.kinau.fishingbot.websocket.packets.CommandPacket;
import systems.kinau.fishingbot.websocket.packets.SayPacket;

public class NetworkHandle {
    public static Packet readPacket(String p) {
        String txt = p.substring("Packet".length());
        String t1 = txt.substring("[".length(),txt.length()-1);
        String[] strings = t1.split(",");
        String name = strings[0].substring("name:".length());
        String action = strings[1].substring("action:".length());
        return new Packet(name,action);
    }

    public static void applyPacket(Packet packet) {
        getPacket(packet).apply();
    }

    public static Packet getPacket(Packet p) {
        switch (p.name) {
            case "say" : {
                return new SayPacket(p.action);
            }
            case "command" : {
                return new CommandPacket(p.action,Boolean.parseBoolean(p.action.split("/-/")[1]));
            }
        }
        for (Packet packet : SocketLaunch.packets) {
            if (packet.name.equals(p.name) && packet.action.equals(p.action)) {
                return packet;
            }
        }
        return null;
    }
}
