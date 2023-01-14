package systems.kinau.fishingbot.modules;

import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.event.EventHandler;
import systems.kinau.fishingbot.event.Listener;
import systems.kinau.fishingbot.event.play.PosLookChangeEvent;
import systems.kinau.fishingbot.event.play.UpdateHealthEvent;
import systems.kinau.fishingbot.gui.GUIController;

public class PlayerUpdateModule extends Module implements Listener {
    private final static GUIController gui = FishingBot.getInstance().getMainGUIController();

    @Override
    public void onEnable() {
        bot.getCurrentBot().getEventManager().registerListener(this);
    }

    @Override
    public void onDisable() {
        bot.getCurrentBot().getEventManager().unregisterListener(this);
        gui.setTextVisible(false);
    }

    @EventHandler
    public void onUpdateHealth(UpdateHealthEvent ev) {
        gui.setHealth(ev.getHealth());
    }

    @EventHandler
    public void onUpdatePos(PosLookChangeEvent ev) {
        gui.setPos((int) ev.getX(), (int) ev.getY(), (int) ev.getZ());
    }
}
