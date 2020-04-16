package me.stevemmmmm.thehypixelpit.enchants;

/*
 * Copyright (c) 2020. Created by the Pit Player: Stevemmmmm.
 */

import me.stevemmmmm.thehypixelpit.managers.CustomEnchant;
import me.stevemmmmm.thehypixelpit.managers.enchants.CalculationMode;
import me.stevemmmmm.thehypixelpit.managers.enchants.DamageManager;
import me.stevemmmmm.thehypixelpit.managers.enchants.LoreBuilder;
import me.stevemmmmm.thehypixelpit.managers.enchants.LevelVariable;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.ArrayList;

public class KingBuster extends CustomEnchant {
    private final LevelVariable<Float> damageIncrease = new LevelVariable<>(.7f, .13f, .2f);

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            attemptEnchantExecution(this, ((Player) event.getDamager()).getInventory().getItemInHand(), event.getEntity(), event);
        }
    }

    @Override
    public void applyEnchant(int level, Object... args) {
        Player damaged = (Player) args[0];
        EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) args[1];

        if (damaged.getHealth() > damaged.getMaxHealth() / 2) {
            DamageManager.getInstance().addDamage(event, damageIncrease.at(level), CalculationMode.ADDITIVE);
        }
    }

    @Override
    public String getName() {
        return "King Buster";
    }

    @Override
    public String getEnchantReferenceName() {
        return "Kingbuster";
    }

    @Override
    public ArrayList<String> getDescription(int level) {
        return new LoreBuilder()
                .addVariable("+7%", "+13%", "+20%")
                .write("Deal ").writeVariable(ChatColor.RED, 0, level).write(" damage vs. player").nextLine()
                .write("above 50% HP")
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
        return Material.GOLD_SWORD;
    }
}
