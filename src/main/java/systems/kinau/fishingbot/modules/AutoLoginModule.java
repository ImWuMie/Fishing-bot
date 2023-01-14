package systems.kinau.fishingbot.modules;

import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.event.EventHandler;
import systems.kinau.fishingbot.event.Listener;
import systems.kinau.fishingbot.event.play.ChatEvent;

public class AutoLoginModule extends Module implements Listener {
    private String password = "";
    int trys = 0;

    @Override
    public void onEnable() {
        FishingBot.getInstance().getCurrentBot().getEventManager().registerListener(this);
        this.password = FishingBot.getInstance().getCurrentBot().getUserPassword();
    }

    @Override
    public void onDisable() {
        FishingBot.getInstance().getCurrentBot().getEventManager().unregisterListener(this);
    }

    @EventHandler
    public void onChat(ChatEvent e) {
        if (e.getText().contains(bot.getCurrentBot().getConfig().getAutoLoginMessage())) {
            if (trys < bot.getCurrentBot().getConfig().getAutoLoginTrys()) {
                bot.getCurrentBot().runCommand(bot.getCurrentBot().getConfig().getAutoLoginMessage()+" "+password,false);
                trys++;
            }
        }

    }
}
