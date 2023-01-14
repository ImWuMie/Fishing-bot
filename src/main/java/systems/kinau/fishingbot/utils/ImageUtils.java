package systems.kinau.fishingbot.utils;

import javafx.scene.image.Image;
import systems.kinau.fishingbot.Main;
import systems.kinau.fishingbot.bot.Enchantment;
import systems.kinau.fishingbot.bot.Item;
import systems.kinau.fishingbot.bot.loot.LootItem;

import java.io.InputStream;
import java.util.List;

public class ImageUtils {

    public static String getItemURL(String filename) {
        return String.format("https://raw.githubusercontent.com/MrKinau/FishingBot/master/src/main/resources/img/items/%s", filename.toLowerCase()).replace(" ", "%20");
    }

    public static String getItemURL(Item item) {
        String fileType = (item.getEnchantments() == null || item.getEnchantments().isEmpty()) ? ".png" : ".gif";
        return getItemURL(item.getName() + fileType);
    }

    public static Image getImage(String filename) {
        InputStream inputStream = Main.class.getClassLoader().getResourceAsStream("img/items/" + filename.toLowerCase());
        if (inputStream == null)
            return null;
        return new Image(inputStream);
    }

    public static Image getImage(LootItem item) {
        String fileType = (item.getEnchantments() == null || item.getEnchantments().isEmpty()) ? ".png" : ".gif";
        return getImage(item.getName() + fileType);
    }

    public static String getFileName(String itemName, List<Enchantment> enchantments) {
        String fileType = (enchantments == null || enchantments.isEmpty()) ? ".png" : ".gif";
        return itemName + fileType;
    }
}
