package me.stevemmmmm.thehypixelpit.world;

import me.stevemmmmm.thehypixelpit.core.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/*
 * Copyright (c) 2020. Created by Stevemmmmm.
 */

public class ClearArrows implements Listener {
    private final HashMap<UUID, ArrayList<Arrow>> playerToArrows = new HashMap<>();

    @EventHandler
    public void onProjectileShoot(EntityShootBowEvent event) {
        Entity projectile = event.getProjectile();

        if (event.getProjectile() instanceof Arrow) {
            if (((Arrow) event.getProjectile()).getShooter() instanceof Player) {
                Player player = (Player) ((Arrow) event.getProjectile()).getShooter();

                if (!playerToArrows.containsKey(player.getUniqueId())) {
                    playerToArrows.put(player.getUniqueId(), new ArrayList<>());
                }

                ArrayList<Arrow> data = playerToArrows.get(player.getUniqueId());

                if (data.size() >= 6) {
                    data.get(0).remove();
                    data.remove(0);
                }

                data.add((Arrow) event.getProjectile());

                playerToArrows.put(player.getUniqueId(), data);
            }
        }

        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.INSTANCE, () -> {
            if (projectile != null) {
                projectile.remove();
            }
        }, 100L);
    }
}
