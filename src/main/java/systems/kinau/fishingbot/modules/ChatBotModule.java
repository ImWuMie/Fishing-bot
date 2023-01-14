package systems.kinau.fishingbot.modules;

import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.event.EventHandler;
import systems.kinau.fishingbot.event.Listener;
import systems.kinau.fishingbot.event.play.ChatEvent;
import systems.kinau.fishingbot.io.config.SettingsConfig;

import java.util.List;

public class ChatBotModule extends Module implements Listener {
    public static List<String> admins = FishingBot.getInstance().getConfig().getAdminPlayers();
    private static String annoyTarget = "^&NullPlayerName&^"; //default name;

    @Override
    public void onEnable() {
        bot.getCurrentBot().getEventManager().registerListener(this);
    }

    @Override
    public void onDisable() {
        bot.getCurrentBot().getEventManager().unregisterListener(this);
    }

    @EventHandler
    public void onChatCommand(ChatEvent e) {
        admins = FishingBot.getInstance().getConfig().getAdminPlayers();
        String message = e.getText();
        if (message.isEmpty() || !message.contains(bot.getConfig().getSplitChar())) return;
        String[] texts = message.split(bot.getConfig().getSplitChar());
        String sender = getAdminByText(texts[0]);
        if (sender.isEmpty()) return;
        boolean hasSpace = bot.getConfig().isHasSpace();
        String txt = message.substring((texts[0] + (hasSpace ? (bot.getConfig().getSplitChar() + " ") : bot.getConfig().getSplitChar())).length());
        String prefix = bot.getCurrentBot().getAuthData().getUsername() + " -";
        // annoy
        if (!annoyTarget.equals("^&NullPlayerName&^")) {
            if (texts[0].contains(annoyTarget) || annoyTarget.equals(sender)) {
                bot.getCurrentBot().getPlayer().sendMessage(txt);
            }
        }

        if (isExists(sender) && (txt.startsWith(prefix) || txt.startsWith("all -"))) { // maybe...
            SettingsConfig config = bot.getConfig();
            String a = txt;
            boolean tpahereEnabled = config.isChatbotTpahere();
            boolean tpaEnabled = config.isChatbotTpa();
            boolean sayEnabled = config.isChatbotSay();
            boolean adminEnabled = config.isChatbotAdmin();
            boolean lookEnabled = config.isChatbotLook();
            boolean annoyEnabled = config.isChatbotAnnoy();
            boolean byeEnabled = config.isChatbotBye();
            boolean filter = config.isChatbotReplace();
            if (filter) {
                a = toMessage(txt);
            } else {
                if (IgnoreMessage(a)) {
                    return;
                }
            }
            String message1 = a.substring(prefix.length());

            // commands
            String tpahere = "tpahere";
            String tpa = "tpa";
            String look = "look";
            String bye = "bye";
            String say = "say";
            String addAdmin = "admin";
            String annoy = "annoy";

            if (startsWith(message1, tpa) && tpaEnabled && !IgnoreMessage(txt)) {
                String player = message1.substring((tpa + " ").length());
                bot.getCurrentBot().runCommand("/tpa " + player);
            }
            if (startsWith(message1, tpahere) && tpahereEnabled) {
                bot.getCurrentBot().runCommand("/tpa " + sender);
            }
            if (startsWith(message1, look) && lookEnabled) {
                String yp = message1.substring((look + " ").length());
                String yaw = (yp.split(" ")[0]);
                String pitch = (yp.split(" ")[1]);
                bot.getCurrentBot().runCommand("/look " + yaw + " " + pitch, true);
            }
            if (startsWith(message1, bye) && byeEnabled) {
                bot.getCurrentBot().runCommand("/bye", true);
            }
            if (startsWith(message1, say) && sayEnabled) {
                String sayText = message1.substring((say + " ").length());
                if (sayText.startsWith("/")) {
                    bot.getCurrentBot().runCommand(sayText);
                } else {
                    bot.getCurrentBot().getPlayer().sendMessage(sayText);
                }
            }
            if (startsWith(message1, addAdmin) && adminEnabled && !IgnoreMessage(txt)) {
                String arg = message1.substring((addAdmin + " ").length());
                String[] args = arg.split(" ");
                String name = args[1];
                if (!args[1].isEmpty()) {
                    if (args[0].equalsIgnoreCase("add")) {
                        bot.getCurrentBot().runCommand("/admin add " + name, true);
                        bot.getCurrentBot().runCommand(getTellCommand(sender, " 已添加" + name));
                    }
                    if (args[0].equalsIgnoreCase("del")) {
                        bot.getCurrentBot().runCommand("/admin del " + name, true);
                        bot.getCurrentBot().runCommand(getTellCommand(sender, " 已移除" + name));
                    }
                }
            }
            if (startsWith(message1, annoy) && annoyEnabled && !IgnoreMessage(txt)) {
                String player = message1.substring((annoy + " ").length());
                if (player.equals("^&NullPlayerName&^") || player.equals(annoyTarget)) {
                    annoyTarget = "^&NullPlayerName&^";
                    bot.getCurrentBot().runCommand(getTellCommand(sender, "取消annoy " + player));
                }
                if (!player.equals(annoyTarget)) {
                    annoyTarget = player;
                    bot.getCurrentBot().runCommand(getTellCommand(sender, "正在annoy " + player));
                }
            }
        }
    }

    public boolean IgnoreMessage(String message) {
        List<String> filters = bot.getConfig().getTextFilters();
        for (String filter : filters) {
            if (message.contains(filter)) {
                return true;
            }
        }
        return false;
    }

    public String toMessage(String message) {
        String out = message;
            List<String> filters = bot.getConfig().getTextFilters();
            for (String filter : filters) {
                while (out.contains(filter)) {
                    char[] size = filter.toCharArray();
                    StringBuilder builder = new StringBuilder();
                    for (char c : size) {
                        builder.append("*");
                    }
                    out = out.replace(filter,builder.toString());
                }
            }
        return out;
    }

    public static String getTellCommand(String to, String message) {
        return "/tell " + to + " " + message;
    }

    public static boolean startsWith(String message, String txt) {
        return message.startsWith(txt);
    }

    public static void addAdmin(String name) {
        if (!isExists(name)) FishingBot.getInstance().getConfig().getAdminPlayers().add(name);
        FishingBot.getLog().info("已添加" + name);
    }

    public static void delAdmin(String name) {
        if (isExists(name)) FishingBot.getInstance().getConfig().getAdminPlayers().remove(name);
        FishingBot.getLog().info("已移除" + name);
    }

    public static boolean isExists(String name) {
        for (String s : admins) {
            if (s.equals(name)) {
                return true;
            }
        }
        return false;
    }

    public static String getAdminByText(String txt) {
        for (String s : admins) {
            if (txt.contains(s)) {
                return s;
            }
        }
        return "";
    }
}
