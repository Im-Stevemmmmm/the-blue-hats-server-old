package me.stevemmmmm.thepitremake.enchants;

import me.stevemmmmm.thepitremake.core.Main;
import me.stevemmmmm.thepitremake.managers.enchants.CustomEnchant;
import me.stevemmmmm.thepitremake.managers.enchants.LevelVariable;
import me.stevemmmmm.thepitremake.managers.enchants.LoreBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/*
 * Copyright (c) 2020. Created by Stevemmmmm.
 */

public class BooBoo extends CustomEnchant {
    private final HashMap<UUID, Integer> tasks = new HashMap<>();

    private final LevelVariable<Integer> secondsNeeded = new LevelVariable<>(5, 4, 3);

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (!tasks.containsKey(player.getUniqueId())) {
            tasks.put(player.getUniqueId(), Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Main.INSTANCE, () -> attemptEnchantExecution(player.getInventory().getLeggings(), player), 0L, 20L));
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        tasks.remove(player.getUniqueId());
    }

    @Override
    public void applyEnchant(int level, Object... args) {
        Player player = (Player) args[0];

        updateHitCount(player);

        if (hasRequiredHits(player, secondsNeeded.at(level))) {
            player.setHealth(Math.min(player.getHealth() + 2, player.getMaxHealth()));
        }
    }

    @Override
    public String getName() {
        return "Boo-boo";
    }

    @Override
    public String getEnchantReferenceName() {
        return "Booboo";
    }

    @Override
    public ArrayList<String> getDescription(int level) {
        return new LoreBuilder()
                .addVariable("5", "4", "3")
                .write("Passively regain ").write(ChatColor.RED, "1❤").write(" every ").writeVariable(0, level).nextLine()
                .write("seconds")
                .build();
    }

    @Override
    public boolean isTierTwoEnchant() {
        return false;
    }

    @Override
    public boolean isRareEnchant() {
        return false;
    }

    @Override
    public Material getEnchantItemType() {
        return Material.LEATHER_LEGGINGS;
    }
}
