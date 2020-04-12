package me.stevemmmmm.thehypixelpit.commands;

import me.stevemmmmm.thehypixelpit.managers.CustomEnchant;
import me.stevemmmmm.thehypixelpit.managers.enchants.CustomEnchantManager;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/*
 * Copyright (c) 2020. Created by the Pit Player: Stevemmmmm.
 */

public class EnchantCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (label.equalsIgnoreCase("pitenchant")) {
                if (args.length == 0) {
                    player.sendMessage(ChatColor.DARK_PURPLE + "Usage:" + ChatColor.RED + " /pitenchant <enchant> <level>");
                } else {
                    CustomEnchant customEnchant = null;

                    for (CustomEnchant enchant : CustomEnchantManager.getInstance().getEnchants()) {
                        if (enchant.getEnchantReferenceName().equalsIgnoreCase(args[0])) {
                            customEnchant = enchant;
                        }
                    }

                    if (customEnchant == null) {
                        player.sendMessage(ChatColor.DARK_PURPLE + "Error!" + ChatColor.RED + " This enchant does not exist!");
                        return true;
                    }

                    ItemStack item = player.getInventory().getItemInHand();

                    if (item.getType() == Material.AIR) {
                        player.sendMessage(ChatColor.DARK_PURPLE + "Error!" + ChatColor.RED + " You are not holding anything!");
                        return true;
                    }

                    if (args.length < 2) {
                        player.sendMessage(ChatColor.DARK_PURPLE + "Error!" + ChatColor.RED + " You did not specify an enchantment level!");
                        return true;
                    }

                    if (!StringUtils.isNumeric(args[1])) {
                        player.sendMessage(ChatColor.DARK_PURPLE + "Error!" + ChatColor.RED + " The enchantment level you entered is not a number!");
                        return true;
                    }

                    if (CustomEnchantManager.getInstance().containsEnchant(item, customEnchant)) {
                        player.sendMessage(ChatColor.DARK_PURPLE + "Error!" + ChatColor.RED + " This item already contains this enchantment!");
                        return true;
                    }

                    if (item.getType() != customEnchant.getEnchantItemType()) {
                        player.sendMessage(ChatColor.DARK_PURPLE + "Error!" + ChatColor.RED + " You can not enchant this enchant on this item!");
                        return true;
                    }

                    if (Integer.parseInt(args[1]) > 3 || Integer.parseInt(args[1]) < 1) {
                        player.sendMessage(ChatColor.DARK_PURPLE + "Error!" + ChatColor.RED + " The enchant level can only be 1, 2, or 3!");
                        return true;
                    }

                    CustomEnchantManager.getInstance().applyLore(item, customEnchant, Integer.parseInt(args[1]));
                    player.sendMessage(ChatColor.DARK_PURPLE + "Success!" + ChatColor.RED + " You applied the enchantment successfully!");
                    player.updateInventory();
                }
            }
        }

        return true;
    }
}
