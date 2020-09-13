package me.stevemmmmm.thepitremake.commands;

import me.stevemmmmm.thepitremake.managers.enchants.CustomEnchant;
import me.stevemmmmm.thepitremake.managers.enchants.CustomEnchantManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/*
 * Copyright (c) 2020. Created by Stevemmmmm.
 */

public class UnenchantCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (label.equalsIgnoreCase("unenchant")) {
                if (args.length == 0) {
                    player.sendMessage(ChatColor.DARK_PURPLE + "Usage:" + ChatColor.RED + " /unenchant <enchant>");
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

                    if (args.length > 1) {
                        player.sendMessage(ChatColor.DARK_PURPLE + "Error!" + ChatColor.RED + " Too many arguments!");
                        return true;
                    }

                    if (item.getType() != Material.LEATHER_LEGGINGS && item.getType() != Material.GOLD_SWORD && item.getType() != Material.BOW) {
                        player.sendMessage(ChatColor.DARK_PURPLE + "Error!" + ChatColor.RED + " You can not enchant this item!");
                        return true;
                    }

                    if (!CustomEnchantManager.getInstance().itemContainsEnchant(item, customEnchant)) {
                        player.sendMessage(ChatColor.DARK_PURPLE + "Error!" + ChatColor.RED + " This item does not have the specified enchant!");
                        return true;
                    }

                    CustomEnchantManager.getInstance().removeEnchant(item, customEnchant);
                    player.sendMessage(ChatColor.DARK_PURPLE + "Success!" + ChatColor.RED + " You unenchanted the enchant successfully!");
                    player.updateInventory();
                }
            }
        }

        return true;
    }
}
