package systems.kinau.fishingbot.websocket.results;

import systems.kinau.fishingbot.utils.GsonUtils;
import systems.kinau.fishingbot.websocket.Result;

public class AtResult extends Result {
    @Override
    public String toJSON() {
        return GsonUtils.beanToJson(get());
    }

    @Override
    public Result get() {
        return this;
    }
}
