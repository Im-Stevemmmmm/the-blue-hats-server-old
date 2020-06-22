package me.stevemmmmm.thepitremake.managers.enchants;

import me.stevemmmmm.thepitremake.core.Main;
import me.stevemmmmm.thepitremake.utils.SortCustomEnchantByName;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/*
 * Copyright (c) 2020. Created by Stevemmmmm.
 */

public class CustomEnchantManager {
    private static CustomEnchantManager instance;

    private final ArrayList<CustomEnchant> enchants = new ArrayList<>();

    private CustomEnchantManager() { }

    public static CustomEnchantManager getInstance() {
        if (instance == null) instance = new CustomEnchantManager();

        return instance;
    }

    public ArrayList<CustomEnchant> getEnchants() {
        return enchants;
    }

    public void registerEnchant(CustomEnchant enchant) {
        Main.INSTANCE.getServer().getPluginManager().registerEvents(enchant, Main.INSTANCE);

        enchants.add(enchant);
        enchants.sort(new SortCustomEnchantByName());
    }

    public boolean playerEnchantProcIsNotCanceled(Player player) {
        for (CustomEnchant enchant : getEnchants()) {
            if (enchant instanceof EnchantCanceler) {
                EnchantCanceler cancelEnchant = (EnchantCanceler) enchant;

                if (cancelEnchant.isCanceled(player)) return false;
            }
        }

        return true;
    }

    public void addEnchants(ItemStack item, int level, CustomEnchant... enchants) {
        boolean itemHasAlreadyTieredUp = false;

        for (CustomEnchant enchant : enchants) {
            ItemMeta meta = item.getItemMeta();
            String previousDisplayName = meta.getDisplayName();

            int tierValue = getItemTier(item);

            if (tierValue > 2) tierValue = 2;

            String tier = convertToRomanNumeral(tierValue + (itemHasAlreadyTieredUp ? 0 : 1));

            ChatColor tierColor = null;
            switch (tierValue + 1) {
                case 1:
                    tierColor = ChatColor.GREEN;
                    break;
                case 2:
                    tierColor = ChatColor.YELLOW;
                    break;
                case 3:
                    tierColor = ChatColor.RED;
                    break;
            }

            String itemIndentifier = "";

            if (getTokensOnItem(item) == 8) {
                itemIndentifier = "Legendary ";
            }

            if (getItemLives(item) == 100) {
                itemIndentifier = "Artifact ";
            }

            //TODO Extraordinary

            switch (item.getType()) {
                case GOLD_SWORD:
                    meta.setDisplayName(tierColor + itemIndentifier + "Tier " + tier + " Sword");
                    break;
                case BOW:
                    meta.setDisplayName(tierColor + itemIndentifier + "Tier " + tier + " Bow");
                    break;
                case LEATHER_LEGGINGS:
                    if (getItemTier(item) == 0) {
                        meta.setDisplayName(getChatColorFromPantsColor(ChatColor.stripColor(meta.getDisplayName().split(" ")[1])) + itemIndentifier + "Tier " + tier + " Pants");
                    } else {
                        meta.setDisplayName(meta.getDisplayName().substring(0, 2) + itemIndentifier + "Tier " + tier + " Pants");
                    }
            }

            //TODO Remove IF statement on LEATHER_LEGGINGS case

            String rare = ChatColor.LIGHT_PURPLE + "RARE! " + ChatColor.BLUE + enchant.getName() + (level != 1 ? " " + convertToRomanNumeral(level) : "");
            String normal = ChatColor.BLUE + enchant.getName() + (level != 1 ? " " + convertToRomanNumeral(level) : "");

            List<String> enchantLore = enchant.getDescription(level);

            if (enchantLore == null) return;

            enchantLore.add(0, enchant.isRareEnchant() ? rare : normal);
            if (meta.getLore() != null) enchantLore.add(0, " ");

            List<String> lore = meta.getLore() != null ? meta.getLore() : new ArrayList<>();

            if (previousDisplayName != null && item.getType() == Material.LEATHER_LEGGINGS) {
                if (ChatColor.stripColor(previousDisplayName.split(" ")[0]).equalsIgnoreCase("Fresh")) {
                    lore = new ArrayList<>();

                    //TODO Random lives
                    lore.add(ChatColor.GRAY + "Lives: " + ChatColor.GREEN + "69" + ChatColor.GRAY + "/420");
                    lore.add(" ");

                    meta.setDisplayName(getChatColorFromPantsColor(ChatColor.stripColor(previousDisplayName.split(" ")[1])) + meta.getDisplayName());
                    enchantLore.remove(0);
                }
            }

            if (previousDisplayName != null && item.getType() == Material.GOLD_SWORD) {
                if (ChatColor.stripColor(previousDisplayName.split(" ")[0]).equalsIgnoreCase("Mystic"))
                {
                    lore = new ArrayList<>();

                    lore.add(ChatColor.GRAY + "Lives: " + ChatColor.GREEN + "69" + ChatColor.GRAY + "/420");
                    lore.add(" ");

                    meta.addEnchant(Enchantment.DAMAGE_ALL, 2, true);

                    enchantLore.remove(0);
                }
            }

            if (previousDisplayName != null && item.getType() == Material.BOW) {
                if (ChatColor.stripColor(previousDisplayName.split(" ")[0]).equalsIgnoreCase("Mystic")) {
                    lore = new ArrayList<>();

                    lore.add(ChatColor.GRAY + "Lives: " + ChatColor.GREEN + "69" + ChatColor.GRAY + "/420");
                    lore.add(" ");

                    meta.addEnchant(Enchantment.DURABILITY, 1, true);

                    enchantLore.remove(0);
                }
            }

            if (item.getType() == Material.LEATHER_LEGGINGS) {
                if (lore.contains(meta.getDisplayName().substring(0, 2) + "As strong as iron")) {
                    lore.remove(lore.size() - 1);
                    lore.remove(lore.size() - 1);
                }
            }

            lore.addAll(enchantLore);

            if (item.getType() == Material.LEATHER_LEGGINGS) {
                lore.add(" ");
                lore.add(meta.getDisplayName().substring(0, 2) + "As strong as iron");
            }

            meta.setLore(lore);

            item.setItemMeta(meta);
            itemHasAlreadyTieredUp = true;
        }
    }

    public int getItemLives(ItemStack item) {
        if (item.getItemMeta().getLore() == null) return 0;

        ArrayList<String> keyWords = new ArrayList<>(Arrays.asList(ChatColor.stripColor(item.getItemMeta().getDisplayName()).split(" ")));

        if (keyWords.contains("Fresh") || keyWords.contains("Mystic")) return 0;

        List<String> lore = item.getItemMeta().getLore();
        String livesLine = lore.get(0);

        return Integer.parseInt(ChatColor.stripColor(livesLine.split(" ")[1]).split("/")[0]);
    }

    public int getMaximumItemLives(ItemStack item) {
        if (item.getItemMeta().getLore() == null) return 0;

        ArrayList<String> keyWords = new ArrayList<>(Arrays.asList(ChatColor.stripColor(item.getItemMeta().getDisplayName()).split(" ")));

        if (keyWords.contains("Fresh") || keyWords.contains("Mystic")) return 0;

        List<String> lore = item.getItemMeta().getLore();
        String livesLine = lore.get(0);

        return Integer.parseInt(ChatColor.stripColor(livesLine.split(" ")[1]).split("/")[1]);
    }

    public void setItemLives(ItemStack item, int value) {
        if (item.getItemMeta().getLore() == null) return;

        List<String> lore = item.getItemMeta().getLore();
        lore.set(0, ChatColor.GRAY + "Lives: " + (value > 3 ? ChatColor.GREEN : ChatColor.RED) + value + ChatColor.GRAY + "/" + getMaximumItemLives(item));

        ItemMeta meta = item.getItemMeta();
        meta.setLore(lore);

        item.setItemMeta(meta);
    }

    public void setMaximumItemLives(ItemStack item, int value) {
        if (item.getItemMeta().getLore() == null) return;

        int lives = getItemLives(item);

        if (lives > value) lives = value;

        List<String> lore = item.getItemMeta().getLore();
        lore.set(0, ChatColor.GRAY + "Lives: " + (value > 3 ? ChatColor.GREEN : ChatColor.RED) + lives + ChatColor.GRAY + "/" + value);

        ItemMeta meta = item.getItemMeta();
        meta.setLore(lore);

        item.setItemMeta(meta);

    }

    public void removeEnchant(ItemStack item, CustomEnchant enchant) {
        ItemMeta meta = item.getItemMeta();

        List<String> lore = meta.getLore();

        if (lore == null) return;

        List<String> formattedLore = new ArrayList<>();

        for (String string : lore) {
            ArrayList<String> enchantData = new ArrayList<>(Arrays.asList(string.split(" ")));
            StringBuilder enchantName = new StringBuilder();

            if (enchantData.size() == 0) {
                formattedLore.add(" ");
                continue;
            }

            String chatColor = string.substring(0, 2);

            for (int i = 0; i < enchantData.size(); i++) {
                enchantData.set(i, ChatColor.stripColor(enchantData.get(i)));
            }

            if (enchantData.get(0).equalsIgnoreCase("RARE!")) {
                enchantData.remove(0);
            }

            for (int i = 0; i < enchantData.size(); i++) {
                if (convertRomanNumeralToInteger(enchantData.get(i)) == -1) {
                    enchantName.append(enchantData.get(i));
                    if (i != enchantData.size() - 1) enchantName.append(" ");
                }
            }

            String name = enchantName.toString().trim();

            formattedLore.add(chatColor + name);
        }

        int index = -1;
        for (int i = 0; i < formattedLore.size(); i++) {
            if (ChatColor.stripColor(formattedLore.get(i)).equalsIgnoreCase(enchant.getName())) {
                index = i;
                break;
            }
        }

        List<String> finalLore = meta.getLore();

        boolean oneBackIndex = false;
        while (!finalLore.get(index).equals(" ")) {
            finalLore.remove(index);

            if (index == finalLore.size()) {
                oneBackIndex = true;
                break;
            }
        }

        if (oneBackIndex) {
            finalLore.remove(index - 1);
        } else {
            finalLore.remove(index);
        }

        meta.setLore(finalLore);

        item.setItemMeta(meta);
    }

    public boolean itemContainsEnchant(ItemStack item, CustomEnchant enchant) {
        if (item.getItemMeta().getLore() == null || enchant == null) return false;

        List<String> lore = item.getItemMeta().getLore();

        String appendRare = "";

        if (enchant.isRareEnchant()) appendRare = ChatColor.LIGHT_PURPLE + "RARE! ";

        if (lore.contains(appendRare + ChatColor.BLUE + enchant.getName())) return true;

        for (int i = 2; i <= 3; i++) {
            if (lore.contains(appendRare + ChatColor.BLUE + enchant.getName() + " " + CustomEnchantManager.getInstance().convertToRomanNumeral(i))) return true;
        }

        return false;
    }

    public HashMap<CustomEnchant, Integer> getItemEnchants(ItemStack item) {
        HashMap<CustomEnchant, Integer> enchantsToLevels = new HashMap<>();

        if (item.getType() == Material.AIR) return enchantsToLevels;
        if (item.getItemMeta().getLore() == null) return enchantsToLevels;

        for (String string : item.getItemMeta().getLore()) {
            ArrayList<String> enchantData = new ArrayList<>(Arrays.asList(string.split(" ")));
            StringBuilder enchantName = new StringBuilder();
            int level = 0;

            if (enchantData.size() == 0) {
                continue;
            }

            for (int i = 0; i < enchantData.size(); i++) {
                enchantData.set(i, ChatColor.stripColor(enchantData.get(i)));
            }

            if (enchantData.get(0).equalsIgnoreCase("RARE!")) {
                enchantData.remove(0);
            }

            for (int i = 0; i < enchantData.size(); i++) {
                if (convertRomanNumeralToInteger(enchantData.get(i)) == -1) {
                    enchantName.append(enchantData.get(i));
                    if (i != enchantData.size() - 1) enchantName.append(" ");
                } else {
                    level = convertRomanNumeralToInteger(enchantData.get(i));
                }
            }

            String name = enchantName.toString().trim();

            for (CustomEnchant enchant : getEnchants()) {
                if (enchant.getName().equals(name)) {
                    enchantsToLevels.put(enchant, level);
                    break;
                }
            }
        }

        return enchantsToLevels;
    }

    public int getTokensOnItem(ItemStack item) {
        if (item.getType() == Material.AIR || item.getItemMeta().getLore() == null) return 0;

        int tokens = 0;

        for (Map.Entry<CustomEnchant, Integer> entry : getItemEnchants(item).entrySet()) {
            tokens += entry.getValue();
        }

        return tokens;
    }

    public List<CustomEnchant> getRawItemEnchants(ItemStack item) {
        if (item == null) return new ArrayList<>();

        ArrayList<CustomEnchant> enchants = new ArrayList<>();

        for (CustomEnchant enchant : getEnchants()) {
            if (itemContainsEnchant(item, enchant)) {
                enchants.add(enchant);
            }
        }

        return enchants;
    }

    public int getItemTier(ItemStack item) {
        ItemMeta meta = item.getItemMeta();

        ArrayList<String> tokens = new ArrayList<>(Arrays.asList(ChatColor.stripColor(meta.getDisplayName()).split(" ")));

        if (tokens.contains("I")) {
            return 1;
        } else if (tokens.contains("II")) {
            return 2;
        } else if (tokens.contains("III")) {
            return 3;
        } else if (tokens.contains("Fresh") || tokens.contains("Mystic")) {
            return 0;
        }

        return -1;
    }

    public boolean percentChance(double percent) {
        return Double.parseDouble(new DecimalFormat("#0.0").format(ThreadLocalRandom.current().nextDouble(0, 99))) <= percent;
    }

    public String convertToRomanNumeral(int value) {
        switch (value) {
            case 0:
                return "None";
            case 1:
                return "I";
            case 2:
                return "II";
            case 3:
                return "III";
            case 4:
                return "IV";
            case 5:
                return "V";
            case 6:
                return "VI";
            case 7:
                return "VII";
            case 8:
                return "VIII";
            case 9:
                return "IX";
            case 10:
                return "X";
            case 11:
                return "XI";
            case 12:
                return "XII";
            case 13:
                return "XIII";
            case 14:
                return "XIV";
            case 15:
                return "XV";
            case 16:
                return "XVI";
            case 17:
                return "XVII";
            case 18:
                return "XVIII";
            case 19:
                return "XIX";
            case 20:
                return "XX";
            case 21:
                return "XXI";
            case 22:
                return "XXII";
            case 23:
                return "XXIII";
            case 24:
                return "XXIV";
            case 25:
                return "XXV";
            case 26:
                return "XXVI";
            case 27:
                return "XXVII";
            case 28:
                return "XXVIII";
            case 29:
                return "XXIX";
            case 30:
                return "XXX";
            case 35:
                return "XXXV";
        }

        return null;
    }

    public int convertRomanNumeralToInteger(String numeral) {
        switch (numeral) {
            case "I":
                return 1;
            case "II":
                return 2;
            case "III":
                return 3;
        }

        return -1;
    }

    public ChatColor getChatColorFromPantsColor(String color) {
        switch (color.toLowerCase()) {
            case "red":
                return ChatColor.RED;
            case "green":
                return ChatColor.GREEN;
            case "blue":
                return ChatColor.BLUE;
            case "orange":
                return ChatColor.GOLD;
            case "yellow":
                return ChatColor.YELLOW;
            case "dark":
                return ChatColor.DARK_PURPLE;
            case "aqua":
                return ChatColor.AQUA;
            case "sewer":
                return ChatColor.DARK_AQUA;
        }

        return ChatColor.WHITE;
    }
}
