package me.stevemmmmm.thehypixelpit.game;

/*
 * Copyright (c) 2020. Created by the Pit Player: Stevemmmmm.
 */

import me.stevemmmmm.thehypixelpit.core.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.UUID;

public class ChatCooldown implements Listener {
    private HashMap<UUID, Integer> tasks = new HashMap<>();
    private HashMap<UUID, Integer> time = new HashMap<>();

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (tasks.containsKey(event.getPlayer().getUniqueId()) && !event.getPlayer().isOp()) {
            if (!event.getPlayer().hasPermission("bhperms.chatcooldownbypass")) {
                event.getPlayer().sendMessage(ChatColor.AQUA.toString() + ChatColor.BOLD + "OOF!" + ChatColor.RED + " Your chat is on cooldown for " + ChatColor.YELLOW + time.get(event.getPlayer().getUniqueId()) + "s" + ChatColor.RED + "!");
                event.setCancelled(true);
                return;
            }
        }

        if (!tasks.containsKey(event.getPlayer().getUniqueId())) {
            tasks.put(event.getPlayer().getUniqueId(), Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Main.INSTANCE, () -> {
                time.put(event.getPlayer().getUniqueId(), time.getOrDefault(event.getPlayer().getUniqueId(), 4) - 1);

                if (time.get(event.getPlayer().getUniqueId()) <= 0) {
                    Bukkit.getServer().getScheduler().cancelTask(tasks.get(event.getPlayer().getUniqueId()));

                    tasks.remove(event.getPlayer().getUniqueId());
                    time.remove(event.getPlayer().getUniqueId());
                }
            }, 0L, 20L));
        }
    }
}
