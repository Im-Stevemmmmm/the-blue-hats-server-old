package me.stevemmmmm.thepitremake.game;

import me.stevemmmmm.thepitremake.core.Main;
import me.stevemmmmm.thepitremake.managers.enchants.LoreBuilder;
import me.stevemmmmm.thepitremake.world.DeveloperUpdates;
import org.bukkit.*;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;

/*
 * Copyright (c) 2020. Created by Stevemmmmm.
 */

public class WorldSelection implements Listener {
    private static WorldSelection instance;

    private final String inventoryName = ChatColor.LIGHT_PURPLE + "World Selection";
    private final Inventory gui = Bukkit.createInventory(null, 9, inventoryName);

    private final ArrayList<UUID> mayExitGuiSelection = new ArrayList<>();

    public static WorldSelection getInstance() {
        if (instance == null) instance = new WorldSelection();

        return instance;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        displaySelectionMenu(event.getPlayer());
    }

    public void displaySelectionMenu(Player player) {
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.INSTANCE, () -> {
            generateGui();

            player.teleport(new Location(player.getWorld(), -90.5, 60, 0.5));
            player.openInventory(gui);
        }, 1L);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getName().equals(inventoryName)) {
            if (event.getRawSlot() == 3) {
                mayExitGuiSelection.add(event.getWhoClicked().getUniqueId());

                transportToWorld(event.getWhoClicked(), "world");

                event.getWhoClicked().closeInventory();
            }

            if (event.getRawSlot() == 5) {
                mayExitGuiSelection.add(event.getWhoClicked().getUniqueId());

                transportToWorld(event.getWhoClicked(), "ThePit_0");

                event.getWhoClicked().closeInventory();
            }

            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory().getName().equals(inventoryName)) {
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.INSTANCE, () -> {
                if (!mayExitGuiSelection.contains(event.getPlayer().getUniqueId())) {
                    event.getPlayer().openInventory(gui);
                } else {
                    mayExitGuiSelection.remove(event.getPlayer().getUniqueId());
                }
            }, 1L);
        }
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        System.out.println("CANCELLED");
        event.setCancelled(true);
    }

    private void transportToWorld(HumanEntity player, String worldName) {
        World world = Main.INSTANCE.getServer().createWorld(new WorldCreator(worldName));
        Location location = new Location(world,0.5, 86.5, 11.5, -180, 0);

        Chunk centerChunk = location.getChunk();
        centerChunk.load(true);

        player.sendMessage(ChatColor.GREEN + "You will be teleported soon...");

        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.INSTANCE, () -> {
            player.teleport(new Location(world,0.5, 86.5, 11.5, -180, 0));
            DeveloperUpdates.displayUpdate((Player) player);
        }, 60L);
    }

    private void generateGui() {
        gui.setItem(3, new ItemStack(Material.BLAZE_POWDER));

        ItemMeta meta = gui.getItem(3).getItemMeta();

        meta.setDisplayName(ChatColor.RED + "The Toxic World");

        meta.setLore(new LoreBuilder()
            .setColor(ChatColor.YELLOW)
            .write("A world where any").next()
            .write("enchants are allowed").next().next()
            .resetColor().write(ChatColor.ITALIC + "No token limit on items")
            .build());

        gui.getItem(3).setItemMeta(meta);

        gui.setItem(5, new ItemStack(Material.QUARTZ));

        meta = gui.getItem(5).getItemMeta();

        meta.setDisplayName(ChatColor.AQUA + "The Peaceful World");

        meta.setLore(new LoreBuilder()
                .setColor(ChatColor.WHITE)
                .write("A world where the most toxic").next()
                .write("enchants are removed from").next()
                .write("existance for peaceful").next()
                .write("gameplay and fair fights").next().next()
                .resetColor().write(ChatColor.ITALIC + "8 tokens maximum on items")
                .build());

        gui.getItem(5).setItemMeta(meta);
    }
}
