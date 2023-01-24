package systems.kinau.fishingbot.websocket.packets;

import systems.kinau.fishingbot.utils.GsonUtils;
import systems.kinau.fishingbot.websocket.Packet;
import systems.kinau.fishingbot.websocket.SocketLaunch;
import systems.kinau.fishingbot.websocket.results.MessageResult;
import systems.kinau.fishingbot.websocket.server.GMessage;

public class QQActionPacket extends Packet {
    public Action action;
    public String group_id;
    public String text;
    public GMessage message;

    public QQActionPacket(Action action, String text, String group_id, String message) {
        super("qqAction_"+action.toString(), "[text:"+text+";group:"+group_id+";message:"+message+"]");
        this.action = action;
        this.group_id = group_id;
        this.text = text;
        this.message = GsonUtils.jsonToBean(message,GMessage.class);
    }

    public enum Action {
        message;

        public static Action getAction(String name) {
            for (Action a : values()) {
                if (a.toString() == name) {
                    return a;
                }
            }
            return Action.message;
        }
    }

    @Override
    public void apply() {
        switch (action) {
            case message: {
                MessageResult result = new MessageResult(new MessageResult.Params(group_id,text));
                if (SocketLaunch.mainServer != null) {
                    SocketLaunch.mainServer.broadcast(result.toJSON());
                }
                if (SocketLaunch.mainClient != null) {
                    SocketLaunch.mainClient.send(result.toJSON());
                }
            }
        }

        super.apply();
    }
}
