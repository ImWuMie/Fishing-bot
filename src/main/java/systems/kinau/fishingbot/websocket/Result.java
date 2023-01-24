package systems.kinau.fishingbot.websocket;

public abstract class Result {
    public abstract String toJSON();
    public abstract Result get();
}
