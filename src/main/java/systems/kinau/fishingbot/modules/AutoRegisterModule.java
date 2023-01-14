package systems.kinau.fishingbot.modules;

import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.event.EventHandler;
import systems.kinau.fishingbot.event.Listener;
import systems.kinau.fishingbot.event.play.ChatEvent;

import java.util.Random;

public class AutoRegisterModule extends Module implements Listener {
    private String password,command;
    int trys = 0;

    @Override
    public void onEnable() {
        FishingBot.getInstance().getCurrentBot().getEventManager().registerListener(this);
        this.password = bot.getCurrentBot().getUserPassword();
        switch (bot.getCurrentBot().getConfig().getAutoRegisterMode()) {
            case TwoPassword:{
                this.command = bot.getCurrentBot().getConfig().getAutoRegisterMessage()+" "+password+" "+password;
            }
            case PasswordAndEmail:{
                this.command = bot.getCurrentBot().getConfig().getAutoRegisterMessage()+" "+password+" "+bot.getCurrentBot().getConfig().getAutoRegisterEmail();
            }
            case PasswordAndRandomEmail:{
                char[] chars = "aefthuibbfp".toCharArray();
                char s = chars[new Random().nextInt(9)];
                char s2 = chars[new Random().nextInt(9)];
                char s3 = chars[new Random().nextInt( 9)];
                char s4 = chars[new Random().nextInt(9)];
                int i = new Random().nextInt(12);
                int i2 = new Random().nextInt( 74);
                String email = "a" + i + s + s3 + i2 + s4 + i + s2 + "@qq.com";
                this.command = bot.getCurrentBot().getConfig().getAutoRegisterMessage() + " " + password + " "+email;
            }
        }
    }

    @Override
    public void onDisable() {
        FishingBot.getInstance().getCurrentBot().getEventManager().unregisterListener(this);
    }

    @EventHandler
    public void onChat(ChatEvent e) {
        if (e.getText().contains(bot.getCurrentBot().getConfig().getAutoRegisterMessage())) {
            if (trys < bot.getCurrentBot().getConfig().getAutoRegisterTrys()) {
                bot.getCurrentBot().runCommand(command,false);
                trys++;
            }
        }
    }
}
