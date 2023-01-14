/*
 * Created by David Luedtke (MrKinau)
 * 2019/10/18
 */

package systems.kinau.fishingbot.modules.fishing;

import lombok.Getter;
import lombok.Setter;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.bot.Enchantment;
import systems.kinau.fishingbot.bot.Item;
import systems.kinau.fishingbot.bot.Slot;
import systems.kinau.fishingbot.bot.loot.LootHistory;
import systems.kinau.fishingbot.bot.loot.LootItem;
import systems.kinau.fishingbot.event.EventHandler;
import systems.kinau.fishingbot.event.Listener;
import systems.kinau.fishingbot.event.custom.FishCaughtEvent;
import systems.kinau.fishingbot.event.play.*;
import systems.kinau.fishingbot.modules.Module;
import systems.kinau.fishingbot.network.protocol.ProtocolConstants;
import systems.kinau.fishingbot.network.protocol.play.PacketOutChatMessage;
import systems.kinau.fishingbot.network.protocol.play.PacketOutUseItem;
import systems.kinau.fishingbot.utils.ItemUtils;
import systems.kinau.fishingbot.utils.StringUtils;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class FishingModule extends Module implements Runnable, Listener {

    private static int BOBBER_ENTITY_TYPE;

    @Getter private List<Item> possibleCaughtItems = new CopyOnWriteArrayList<>();

    @Getter @Setter private int currentBobber = -1;
    @Getter @Setter private short lastY = -1;
    @Getter @Setter private boolean trackingNextBobberId = false;
    @Getter @Setter private boolean noRodAvailable = false;
    @Getter private boolean paused = false;
    @Getter private boolean trackingNextEntityMeta = false;
    @Getter private boolean waitForLookFinish = false;
    @Getter @Setter private long lastFish = System.currentTimeMillis();
    // skid from https://github.com/angushushu/MrKinau_fishingbot_DIY
    private boolean autoHeadshaking = FishingBot.getInstance().getCurrentBot().getConfig().isAutoHeadshakingEnabled();
    private float angle = FishingBot.getInstance().getCurrentBot().getConfig().getAutoHeadshakingAngle();
    private int frequency = FishingBot.getInstance().getCurrentBot().getConfig().getAutoHeadshakingFrequency();
    private static int fishCnt = 0;

    @Getter @Setter private int currentFishingRodValue;

    @Getter private Thread stuckingFix;
    @Getter private boolean joined;
    @Getter @Setter private LootHistory lootHistory;

    public FishingModule(LootHistory savedLootHistory) {
        this.lootHistory = savedLootHistory;
        int protocolId = FishingBot.getInstance().getCurrentBot().getServerProtocol();
        if (protocolId < ProtocolConstants.MINECRAFT_1_14) {
            if (protocolId <= ProtocolConstants.MINECRAFT_1_13_2)
                BOBBER_ENTITY_TYPE = 90;
            else
                BOBBER_ENTITY_TYPE = 101;
        } else
            BOBBER_ENTITY_TYPE = RegistryHandler.getEntityType("minecraft:fishing_bobber", FishingBot.getInstance().getCurrentBot().getServerProtocol());
        if (BOBBER_ENTITY_TYPE == 0)
            BOBBER_ENTITY_TYPE = 90;
    }

    public void setTrackingNextEntityMeta(boolean trackingNextEntityMeta) {
        this.trackingNextEntityMeta = trackingNextEntityMeta;
    }

    @Override
    public void onEnable() {
        FishingBot.getInstance().getCurrentBot().getEventManager().registerListener(this);
        if (FishingBot.getInstance().getCurrentBot().getConfig().isStuckingFixEnabled()) {
            stuckingFix = new Thread(this);
            stuckingFix.setName("stuckingFixThread");
            stuckingFix.start();
        }
    }

    @Override
    public void onDisable() {
        if (stuckingFix != null)
            stuckingFix.interrupt();
        FishingBot.getInstance().getCurrentBot().getEventManager().unregisterListener(this);
    }

    public void stuck() {
        if (isPaused())
            return;
        if (isNoRodAvailable())
            return;
        if (FishingBot.getInstance().getCurrentBot().getPlayer().isCurrentlyLooking()) {
            waitForLookFinish = true;
            return;
        }
        setLastFish(System.currentTimeMillis());
        setTrackingNextBobberId(true);
        try {
            Thread.sleep(200);
        } catch (InterruptedException ignore) { }
        FishingBot.getInstance().getCurrentBot().getNet().sendPacket(new PacketOutUseItem());
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
        if (paused) {
            if (getCurrentBobber() != -1 && !isTrackingNextEntityMeta())
                FishingBot.getInstance().getCurrentBot().getNet().sendPacket(new PacketOutUseItem());
        } else {
            stuck();
        }
    }

    public void fish() {
        // update
        autoHeadshaking = FishingBot.getInstance().getCurrentBot().getConfig().isAutoHeadshakingEnabled();
        frequency = FishingBot.getInstance().getCurrentBot().getConfig().getAutoHeadshakingFrequency();

        setLastFish(System.currentTimeMillis());
        setCurrentBobber(-1);
        setTrackingNextEntityMeta(true);
        if (isPaused())
            return;
        FishingBot.getInstance().getCurrentBot().getNet().sendPacket(new PacketOutUseItem());
        new Thread(() -> {
            float currentYaw = FishingBot.getInstance().getCurrentBot().getPlayer().getYaw();
            if(autoHeadshaking) FishingBot.getInstance().getCurrentBot().getPlayer().look(currentYaw + angle, 0, 5);

            try {
                int timeToWait = FishingBot.getInstance().getCurrentBot().getPlayer().getLastPing() + 200;
                Thread.sleep(timeToWait);
                setTrackingNextEntityMeta(false);
                getCaughtItem();
                Thread.sleep(200);
                if (FishingBot.getInstance().getCurrentBot().getConfig().isPreventRodBreaking() && ItemUtils.getDamage(FishingBot.getInstance().getCurrentBot().getPlayer().getHeldItem()) >= 63) {
                    noRod();
                    return;
                }
                if (isPaused())
                    return;
                setTrackingNextBobberId(true);
                Thread.sleep(200);

                if (FishingBot.getInstance().getCurrentBot().getPlayer() == null || FishingBot.getInstance().getCurrentBot().getPlayer().isCurrentlyLooking()) {
                    this.waitForLookFinish = true;
                    setTrackingNextBobberId(false);
                } else {
                    FishingBot.getInstance().getCurrentBot().getNet().sendPacket(new PacketOutUseItem());
                }
            } catch (InterruptedException ignore) {}
            if(autoHeadshaking){
                FishingBot.getInstance().getCurrentBot().getPlayer().look(currentYaw, 0, 5);//
                fishCnt++;
                if(fishCnt >= frequency){
                    angle *= -1;
                    fishCnt = 0;
                }
            }
        }).start();
    }

    public boolean containsPossibleItem(int eid) {
        return getPossibleCaughtItems().stream().anyMatch(item -> item.getEid() == eid);
    }

    public void addPossibleMotion(int eid, int motX, int motY, int motZ) {
        getPossibleCaughtItems().forEach(item -> {
            if (item.getEid() == eid) {
                item.setMotX(motX);
                item.setMotY(motY);
                item.setMotZ(motZ);
            }
        });
    }

    private void getCaughtItem() {
        if (getPossibleCaughtItems().size() < 1)
            return;
        Item currentMax = getPossibleCaughtItems().get(0);
        int currentMaxMot = getMaxMot(currentMax);
        for (Item possibleCaughtItem : getPossibleCaughtItems()) {
            int mot = getMaxMot(possibleCaughtItem);
            if (mot > currentMaxMot) {
                currentMax = possibleCaughtItem;
                currentMaxMot = mot;
            }
        }

        //Clear mem
        getPossibleCaughtItems().clear();

        //Print to console (based on announcetype)
        logItem(currentMax,
                FishingBot.getInstance().getCurrentBot().getConfig().getAnnounceTypeConsole(),
                FishingBot.getLog()::info,
                FishingBot.getLog()::info);

        //Print in mc chat (based on announcetype)
        logItem(currentMax,
                FishingBot.getInstance().getCurrentBot().getConfig().getAnnounceTypeChat(),
                (String str) -> FishingBot.getInstance().getCurrentBot().getNet().sendPacket(new PacketOutChatMessage(FishingBot.PREFIX + str)),
                (String str) -> {
                    // Delay the enchant messages to arrive after the item announcement
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException ignore) { }
                    FishingBot.getInstance().getCurrentBot().getNet().sendPacket(new PacketOutChatMessage(str));
                });

        LootItem lootItem = getLootHistory().registerItem(currentMax.getName(), currentMax.getEnchantments());

        FishingBot.getInstance().getCurrentBot().getEventManager().callEvent(new FishCaughtEvent(currentMax, lootItem));
    }


    private String stringify(Item item) {
        return FishingBot.getI18n().t("module-fishing-caught-item", item.getName());
    }

    public void logItem(Item item, AnnounceType noisiness, Consumer<String> announce, Consumer<String> announceEnchants) {
        if (noisiness == AnnounceType.NONE)
            return;
        else if (noisiness == AnnounceType.ALL)
            announce.accept(stringify(item));
        else if (noisiness == AnnounceType.ALL_BUT_FISH && !ItemUtils.isFish(FishingBot.getInstance().getCurrentBot().getServerProtocol(), item.getItemId()))
            announce.accept(stringify(item));

        if (item.getEnchantments().isEmpty())
            return;

        if (noisiness == AnnounceType.ONLY_ENCHANTED)
            announce.accept(stringify(item));
        else if (noisiness == AnnounceType.ONLY_BOOKS && ItemUtils.isEnchantedBook(FishingBot.getInstance().getCurrentBot().getServerProtocol(), item.getItemId()))
            announce.accept(stringify(item));
        if (noisiness == AnnounceType.ONLY_BOOKS && !ItemUtils.isEnchantedBook(FishingBot.getInstance().getCurrentBot().getServerProtocol(), item.getItemId()))
            return;

        if (!item.getEnchantments().isEmpty()) {
            for (Enchantment enchantment : item.getEnchantments()) {
                String asText = "-> "
                        + enchantment.getEnchantmentType().getName().toUpperCase()
                        + " "
                        + StringUtils.getRomanLevel(enchantment.getLevel());
                announceEnchants.accept(asText);
            }
        }
    }

    private int getMaxMot(Item item) {
        return Math.abs(item.getMotX()) + Math.abs(item.getMotY()) + Math.abs(item.getMotZ());
    }

    private void noRod() {
        if (FishingBot.getInstance().getCurrentBot().getConfig().isDisableRodChecking())
            return;
        boolean swapped = swapWithBestFishingRod();
        if (!swapped) {
            this.noRodAvailable = true;

            FishingBot.getI18n().warning("module-fishing-no-rod-available");
        }
    }

    private void reFish(int id) {
        setTrackingNextBobberId(false);
        new Thread(() -> {
            try { Thread.sleep(2500); } catch (InterruptedException ignored) { }     //Prevent Velocity grabbed from flying hook
            setCurrentBobber(id);
        }).start();
    }

    public boolean swapWithBestFishingRod() {
        int bestSlot = ItemUtils.getBestFishingRod(FishingBot.getInstance().getCurrentBot().getPlayer().getInventory());
        if (bestSlot < 0)
            return false;
        if (bestSlot == FishingBot.getInstance().getCurrentBot().getPlayer().getHeldSlot())
            return false;
        int newSlot = FishingBot.getInstance().getCurrentBot().getPlayer().getHeldSlot() - 36;
        FishingBot.getInstance().getCurrentBot().getPlayer().swapToHotBar(bestSlot, newSlot);
        return true;
    }

    public void finishedLooking() {
        if (!waitForLookFinish)
            return;
        this.waitForLookFinish = false;
        if (isPaused())
            return;
        new Thread(() -> {
            setTrackingNextBobberId(true);
            FishingBot.getInstance().getCurrentBot().getNet().sendPacket(new PacketOutUseItem());
        }).start();
    }

    @EventHandler
    public void onSetDifficulty(DifficultySetEvent event) {
        if (isJoined())
            return;
        this.joined = true;
        new Thread(() -> {
            if (isPaused())
                return;
            try {
                Thread.sleep(1500);
            } catch (InterruptedException ignore) { }
            setTrackingNextBobberId(true);
            if (ItemUtils.isFishingRod(FishingBot.getInstance().getCurrentBot().getPlayer().getHeldItem())) {
                FishingBot.getI18n().info("module-fishing-start-fishing");
                if (FishingBot.getInstance().getCurrentBot().getPlayer().isCurrentlyLooking()) {
                    this.waitForLookFinish = true;
                    return;
                }
                FishingBot.getInstance().getCurrentBot().getNet().sendPacket(new PacketOutUseItem());
            } else noRod();
        }).start();
    }

    //TODO: Caught detection may be much easier with the Is Caught field in EntityMetadataPacket (since MC-1.16)
    @EventHandler
    public void onEntityVelocity(EntityVelocityEvent event) {
        addPossibleMotion(event.getEid(), event.getX(), event.getY(), event.getZ());
        if (getCurrentBobber() != event.getEid())
            return;

        switch (FishingBot.getInstance().getCurrentBot().getServerProtocol()) {
            case ProtocolConstants.MINECRAFT_1_10:
            case ProtocolConstants.MINECRAFT_1_9_4:
            case ProtocolConstants.MINECRAFT_1_9_2:
            case ProtocolConstants.MINECRAFT_1_9_1:
            case ProtocolConstants.MINECRAFT_1_9:
            case ProtocolConstants.MINECRAFT_1_8: {
                FishingBot.getInstance().getCurrentBot().getFishingModule().fish();
                break;
            }
            case ProtocolConstants.MINECRAFT_1_13_2:
            case ProtocolConstants.MINECRAFT_1_13_1:
            case ProtocolConstants.MINECRAFT_1_13:
            case ProtocolConstants.MINECRAFT_1_12_2:
            case ProtocolConstants.MINECRAFT_1_12_1:
            case ProtocolConstants.MINECRAFT_1_12:
            case ProtocolConstants.MINECRAFT_1_11_1:
            case ProtocolConstants.MINECRAFT_1_11:
            case ProtocolConstants.MINECRAFT_1_14:
            case ProtocolConstants.MINECRAFT_1_14_1:
            case ProtocolConstants.MINECRAFT_1_14_2:
            case ProtocolConstants.MINECRAFT_1_14_3:
            case ProtocolConstants.MINECRAFT_1_14_4:
            default: {
                if (Math.abs(event.getY()) > 350) {
                    FishingBot.getInstance().getCurrentBot().getFishingModule().fish();
                } else if (lastY == 0 && event.getY() == 0) {    //Sometimes Minecraft does not push the bobber down, but this workaround works good
                    FishingBot.getInstance().getCurrentBot().getFishingModule().fish();
                }
                break;
            }
        }

        lastY = event.getY();
    }

    @EventHandler
    public void onUpdateSlot(UpdateSlotEvent event) {
        if (event.getWindowId() != 0)
            return;
        Slot slot = event.getSlot();

        updateInventory(slot, event.getSlotId());
    }

    @EventHandler
    public void onUpdateWindowItems(UpdateWindowItemsEvent event) {
        if (event.getWindowId() != 0)
            return;
        for (int i = 0; i < event.getSlots().size(); i++) {
            if (ItemUtils.isFishingRod(event.getSlots().get(i))) {
                updateInventory(event.getSlots().get(i), i);
                break;
            }
        }
    }

    private void updateInventory(Slot slot, int slotId) {
        new Thread(() -> {
            try { Thread.sleep(100); } catch (InterruptedException ignore) { }
            // check current fishing rod value and swap if a better one is in inventory
            if (ItemUtils.isFishingRod(slot) && !FishingBot.getInstance().getCurrentBot().getConfig().isDisableRodChecking()) {
                swapWithBestFishingRod();
            }

            if (isPaused() || bot.getCurrentBot().getPlayer() == null)
                return;

            if (FishingBot.getInstance().getCurrentBot().getPlayer().getHeldSlot() == slotId) {
                if (isNoRodAvailable() && ItemUtils.isFishingRod(slot)) {
                    if (FishingBot.getInstance().getCurrentBot().getConfig().isPreventRodBreaking() && ItemUtils.getDamage(slot) >= 63)
                        return;
                    FishingBot.getI18n().info("module-fishing-new-rod-available");
                    setLastFish(System.currentTimeMillis());
                    setNoRodAvailable(false);
                    setCurrentBobber(-1);
                    setTrackingNextEntityMeta(false);

                    if (FishingBot.getInstance().getCurrentBot().getPlayer().isCurrentlyLooking()) {
                        this.waitForLookFinish = true;
                        return;
                    }
                    setTrackingNextBobberId(true);
                    FishingBot.getInstance().getCurrentBot().getNet().sendPacket(new PacketOutUseItem());
                } else if (!isNoRodAvailable() && !ItemUtils.isFishingRod(slot)) {
                    noRod();
                }
                this.currentFishingRodValue = ItemUtils.getFishingRodValue(slot);
            }
        }).start();
    }

    @EventHandler
    public void onSpawnObject(SpawnEntityEvent event) {
        if (!FishingBot.getInstance().getCurrentBot().getFishingModule().isTrackingNextBobberId())
            return;

        if (FishingBot.getInstance().getCurrentBot().getPlayer().getEntityID() != -1 && event.getObjectData() != FishingBot.getInstance().getCurrentBot().getPlayer().getEntityID())
            return;

        if (event.getType() == BOBBER_ENTITY_TYPE) {
            reFish(event.getId());
        }
    }

    @EventHandler
    public void onDestroy(DestroyEntitiesEvent event) {
        if (getCurrentBobber() != -1 && event.getEntityIds().contains(getCurrentBobber()))
            stuck();
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            if (System.currentTimeMillis() - getLastFish() > 60000) {
                setLastFish(System.currentTimeMillis());
                if (isNoRodAvailable())
                    continue;
                if (isPaused())
                    continue;
                Slot curr = FishingBot.getInstance().getCurrentBot().getPlayer().getHeldItem();
                if (ItemUtils.isFishingRod(curr) && ItemUtils.getDamage(curr) >= 63) {
                    noRod();
                    continue;
                }
                setCurrentBobber(-1);
                setTrackingNextEntityMeta(false);
                FishingBot.getI18n().warning("module-fishing-bot-is-slow");

                if (FishingBot.getInstance().getCurrentBot().getPlayer().isCurrentlyLooking()) {
                    this.waitForLookFinish = true;
                    return;
                }
                setTrackingNextBobberId(true);
                FishingBot.getInstance().getCurrentBot().getNet().sendPacket(new PacketOutUseItem());
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
