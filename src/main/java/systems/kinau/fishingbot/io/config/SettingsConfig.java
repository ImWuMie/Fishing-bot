/*
 * Created by David Luedtke (MrKinau)
 * 2019/8/29
 */
package systems.kinau.fishingbot.io.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import systems.kinau.fishingbot.i18n.Language;
import systems.kinau.fishingbot.modules.ejection.EjectionRule;
import systems.kinau.fishingbot.modules.fishing.AnnounceType;
import systems.kinau.fishingbot.modules.timer.Timer;
import systems.kinau.fishingbot.network.protocol.ProtocolConstants;
import systems.kinau.fishingbot.settings.AuthMode;
import systems.kinau.fishingbot.settings.AutoRegisterMode;
import systems.kinau.fishingbot.utils.LocationUtils;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

@Getter
@ToString
public class SettingsConfig implements Config {
    @Property(key = "server.ip", description = "config-server-ip") private String serverIP = "127.0.0.1";
    @Property(key = "server.port", description = "config-server-port") private int serverPort = 25565;
    @Property(key = "server.realm-id", description = "config-server-realm-id") @Setter private long realmId = -1;
    @Property(key = "server.realm-accept-tos", description = "config-server-realm-accept-tos") @Setter private boolean realmAcceptTos = false;
    @Property(key = "server.default-protocol", description = "config-server-default-protocol") private String defaultProtocol = ProtocolConstants.getVersionString(ProtocolConstants.AUTOMATIC);
    @Property(key = "server.spoof-forge", description = "config-server-spoof-forge") private boolean spoofForge = false;


    @Property(key = "auto.chat-bot.enabled", description = "config-auto-chat-bot") private boolean chatbot = false;

    @Property(key = "auto.chat-bot.say", description = "config-auto-chat-bot-say") private boolean chatbotSay = true;
    @Property(key = "auto.chat-bot.tpa", description = "config-auto-chat-bot-tpa") private boolean chatbotTpa = true;
    @Property(key = "auto.chat-bot.tpahere", description = "config-auto-chat-bot-tpahere") private boolean chatbotTpahere = true;
    @Property(key = "auto.chat-bot.look", description = "config-auto-chat-bot-look") private boolean chatbotLook = true;
    @Property(key = "auto.chat-bot.bye", description = "config-auto-chat-bot-bye") private boolean chatbotBye = true;
    @Property(key = "auto.chat-bot.admin", description = "config-auto-chat-bot-admin") private boolean chatbotAdmin = true;
    //@Property(key = "auto.chat-bot.ms", description = "config-auto-chat-bot-ms") private boolean chatbotMs = true;
    @Property(key = "auto.chat-bot.annoy", description = "config-auto-chat-bot-annoy") private boolean chatbotAnnoy = true;

    @Setter @Property(key = "auto.chat-bot.text-filters", description = "config-auto-text-filters") private List<String> textFilters = Arrays.asList("pay","res give","res padd","res pdel","res set","sb","傻逼");
    @Property(key = "auto.chat-bot.replace", description = "config-auto-replace") private boolean chatbotReplace = true;
    @Property(key = "auto.chat-bot.space", description = "config-auto-space") private boolean hasSpace = true;
    @Setter @Property(key = "auto.chat-bot.split-char", description = "config-auto-split-char") private String splitChar = ":";
    @Setter @Property(key = "auto.chat-bot.admin-players", description = "config-auto-admin-players") private List<String> adminPlayers = Arrays.asList("ImWuMie","WuMieNB","MuXi");

    @Property(key = "auto.auto-headshaking.enabled", description = "config-auto-auto-headshaking") private boolean autoHeadshakingEnabled = false;
    @Property(key = "auto.auto-headshaking.angle", description = "config-auto-auto-headshaking-angle") private float autoHeadshakingAngle = 20;
    @Property(key = "auto.auto-headshaking.frequency", description = "config-auto-auto-headshaking-frequency") private int autoHeadshakingFrequency = 2;

    @Property(key = "auto.auto-reconnect", description = "config-auto-auto-reconnect") private boolean autoReconnect = true;
    @Property(key = "auto.auto-reconnect-time", description = "config-auto-auto-reconnect-time") private int autoReconnectTime = 5;
    @Property(key = "auto.auto-disconnect", description = "config-auto-auto-disconnect") private boolean autoDisconnect = false;
    @Property(key = "auto.auto-sneak", description = "config-auto-auto-sneak") private boolean autoSneak = false;
    @Property(key = "auto.auto-disconnect-players-threshold", description = "config-auto-auto-disconnect-players-threshold") private int autoDisconnectPlayersThreshold = 5;

    @Property(key = "auto.auto-command-on-respawn.enabled", description = "config-auto-auto-command-on-respawn") private boolean autoCommandOnRespawnEnabled = false;
    @Property(key = "auto.auto-command-on-respawn.delay", description = "config-auto-auto-command-on-respawn-delay") private long autoCommandOnRespawnDelay = 1000;
    @Property(key = "auto.auto-command-on-respawn.commands", description = "config-auto-auto-command-on-respawn-commands") private List<String> autoCommandOnRespawn = Arrays.asList("%prefix%I respawned", "/home fishing");

    @Property(key = "auto.auto-command-before-death.enabled", description = "config-auto-auto-command-before-death") private boolean autoCommandBeforeDeathEnabled = false;
    @Property(key = "auto.auto-command-before-death.commands", description = "config-auto-auto-command-before-death-commands") private List<String> autoCommandBeforeDeath = Arrays.asList("%prefix%I am about to die", "/home");
    @Property(key = "auto.auto-command-before-death.min-health-before-death", description = "config-auto-auto-command-before-death-min-health-before-death") private float minHealthBeforeDeath = 6.0F;

    @Property(key = "auto.auto-quit-before-death.enabled", description = "config-auto-auto-quit-before-death") private boolean autoQuitBeforeDeathEnabled = false;
    @Property(key = "auto.auto-quit-before-death.min-health-before-quit", description = "config-auto-auto-quit-before-death-min-health-before-quit") private float minHealthBeforeQuit = 6.0F;
    @Property(key = "auto.auto-eject.enabled", description = "config-auto-auto-eject") private boolean autoLootEjectionEnabled = false;
    @Property(key = "auto.auto-eject.rules", description = "config-auto-auto-eject") private List<EjectionRule> autoLootEjectionRules = Arrays.asList(
            new EjectionRule("fish", LocationUtils.Direction.WEST, Arrays.asList("cod", "salmon", "pufferfish", "tropical_fish"), EjectionRule.EjectionType.DROP),
            new EjectionRule("treasure", LocationUtils.Direction.EAST, Arrays.asList("bow", "enchanted_book", "name_tag", "nautilus_shell", "saddle"), EjectionRule.EjectionType.DROP),
            new EjectionRule("junk", LocationUtils.Direction.SOUTH, Arrays.asList("lily_pad", "bowl", "leather", "leather_boots", "rotten_flesh", "stick", "string", "potion", "bone", "ink_sac", "tripwire_hook", "bamboo"), EjectionRule.EjectionType.DROP));

    @Property(key = "auto.timer.enabled", description = "config-auto-timer") private boolean timerEnabled = false;
    @Property(key = "auto.timer.timers", description = "config-auto-timers") private List<Timer> timers = Collections.singletonList(new Timer("test", 5, TimeUnit.MINUTES, Collections.singletonList("Every five minutes")));

    @Property(key = "account.authmode", description = "config-account-auth-mode") private AuthMode authMode = AuthMode.Online;
    @Property(key = "account.authusername", description = "config-account-auth-username") private String authUserName = "WuMie";
    @Property(key = "account.authpassword", description = "config-account-auth-password") private String authPassword = "123";

    @Property(key = "account.autoregister-mode", description = "config-account-autoregister-mode") private AutoRegisterMode autoRegisterMode = AutoRegisterMode.TwoPassword;
    @Property(key = "account.autologin-message", description = "config-account-autologin-message") private String autoLoginMessage = "/login";
    @Property(key = "account.autologin", description = "config-account-autologin") private int autoLoginTrys = 2;
    @Property(key = "account.autoregister-message", description = "config-account-autoregister-message") private String autoRegisterMessage = "/reg";
    @Property(key = "account.autoregister", description = "config-account-autoregister") private int autoRegisterTrys = 2;
    @Property(key = "account.autoregister-email", description = "config-account-autoregister-email") private String autoRegisterEmail = "114514@qq.com";
    @Property(key = "account.bypass-captcha", description = "config-account-bypass-captcha") private boolean bypassCaptcha = false;

    @Property(key = "logs.log-count", description = "config-logs-log-count") private int logCount = 15;
    @Property(key = "logs.log-packets", description = "config-logs-log-packets") private boolean logPackets = false;

    @Property(key = "announces.discord.enabled", description = "config-announces-discord") private boolean webHookEnabled = false;
    @Property(key = "announces.discord.web-hook", description = "config-announces-discord-web-hook") private String webHook = "YOURWEBHOOK";
    @Property(key = "announces.discord.announce-type-discord", description = "config-announces-discord-announce-type-discord") private AnnounceType announceTypeDiscord = AnnounceType.ONLY_ENCHANTED;
    @Property(key = "announces.discord.alert-on-attack", description = "config-announces-discord-alert-on-attack") private boolean alertOnAttack = true;
    @Property(key = "announces.discord.alert-on-respawn", description = "config-announces-discord-alert-on-respawn") private boolean alertOnRespawn = true;
    @Property(key = "announces.discord.alert-on-level-update", description = "config-announces-discord-alert-on-level-update") private boolean alertOnLevelUpdate = true;
    @Property(key = "announces.discord.ping-on-enchantment.enabled", description = "config-announces-discord-ping-on-enchantment") private boolean pingOnEnchantmentEnabled = false;
    @Property(key = "announces.discord.ping-on-enchantment.mention", description = "config-announces-discord-ping-on-enchantment-mention") private String pingOnEnchantmentMention = "<@USER_ID>";
    @Property(key = "announces.discord.ping-on-enchantment.items", description = "config-announces-discord-ping-on-enchantment-items") private List<String> pingOnEnchantmentItems = Collections.singletonList("enchanted_book");
    @Property(key = "announces.discord.ping-on-enchantment.enchantments", description = "config-announces-discord-ping-on-enchantment-enchantments") private List<String> pingOnEnchantmentEnchantments = Arrays.asList("MENDING","UNBREAKING");

    @Property(key = "announces.announce-type-chat", description = "config-announces-announce-type-chat") private AnnounceType announceTypeChat = AnnounceType.ONLY_ENCHANTED;
    @Property(key = "announces.announce-type-console", description = "config-announces-announce-type-console") private AnnounceType announceTypeConsole = AnnounceType.ALL;
    @Property(key = "announces.announce-lvl-up", description = "config-announces-announce-lvl-up") private boolean announceLvlUp = true;
    @Property(key = "announces.announce-lvl-up-text", description = "config-announces-announce-lvl-up-text") private String announceLvlUpText = "I am level %lvl% now";

    @Property(key = "start-text.enabled", description = "config-start-text-enabled") private boolean startTextEnabled = true;
    @Property(key = "start-text.text", description = "config-start-text-text") private List<String> startText = Arrays.asList("%prefix%Starting fishing", "/trigger Bot");

    @Property(key = "misc.stucking-fix-enabled", description = "config-misc-stucking-fix-enabled") private boolean stuckingFixEnabled = true;
    @Property(key = "misc.prevent-rod-breaking", description = "config-misc-prevent-rod-breaking") private boolean preventRodBreaking = true;
    @Property(key = "misc.disable-rod-checking", description = "config-misc-disable-rod-checking") private boolean disableRodChecking = false;
    @Property(key = "misc.gui-console-max-lines", description = "config-misc-gui-console-max-lines") private int guiConsoleMaxLines = 1000;
    @Property(key = "misc.language", description = "config-misc-language") private Language language = Language.getByLocale(Locale.getDefault());
    @Property(key = "misc.look-speed", description = "config-misc-look-speed") private int lookSpeed = 32;
    @Property(key = "misc.wiki", description = "") private String readme = "https://github.com/MrKinau/FishingBot/wiki/config";

    @Getter private final String path;

    public SettingsConfig(String path) {
        this.path = path;
        init(path);

        // fix: User do not understand split of port and IP
        if (serverIP.contains(":")) {
            serverPort = Integer.parseInt(serverIP.split(":")[1]);
            serverIP = serverIP.split(":")[0];
        }
    }

    public void save() {
        new PropertyProcessor().saveConfig(this, new File(path));
    }
}
