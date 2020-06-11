package me.stevemmmmm.thepitremake.world;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/*
 * Copyright (c) 2020. Created by Stevemmmmm.
 */

public class PlayerUtility implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.setJoinMessage(ChatColor.GOLD.toString() + ChatColor.BOLD.toString() + "WELCOME BACK! " + ChatColor.AQUA + event.getPlayer().getName() + ChatColor.GREEN + " just joined the server!");
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        event.setQuitMessage(ChatColor.RED.toString() + ChatColor.BOLD.toString() + "OOF! " + ChatColor.AQUA + event.getPlayer().getName() + ChatColor.RED + " just left the server!");
    }
}
