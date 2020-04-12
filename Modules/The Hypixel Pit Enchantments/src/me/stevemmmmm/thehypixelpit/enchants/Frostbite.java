package me.stevemmmmm.thehypixelpit.enchants;

/*
 * Copyright (c) 2020. Created by the Pit Player: Stevemmmmm.
 */

import me.stevemmmmm.animationapi.core.Sequence;
import me.stevemmmmm.animationapi.core.SequenceAPI;
import me.stevemmmmm.thehypixelpit.managers.CustomEnchant;
import me.stevemmmmm.thehypixelpit.managers.enchants.DamageManager;
import me.stevemmmmm.thehypixelpit.managers.enchants.LevelVariable;
import me.stevemmmmm.thehypixelpit.managers.enchants.LoreBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;

public class Frostbite extends CustomEnchant {
    private LevelVariable<Integer> hitsNeeded = new LevelVariable<>(5, 4, 4);

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            attemptEnchantExecution(this, ((Player) event.getDamager()).getInventory().getItemInHand(), event.getEntity(), event.getDamager());
        }
    }

    @Override
    public void applyEnchant(int level, Object... args) {
        Player hit = (Player) args[0];
        Player damager = (Player) args[1];

        updateHitCount(damager);

        if (hasRequiredHits(damager, hitsNeeded.at(level))) {
            hit.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 1), true);
            hit.getWorld().playSound(hit.getLocation(), Sound.GLASS, 1, 1);

            if (level != 1) {
                Sequence damageSequence = new Sequence() {{
                    addKeyFrame(20, () -> DamageManager.getInstance().doTrueDamage(hit, 2, damager));

                    addKeyFrame(40, () -> DamageManager.getInstance().doTrueDamage(hit, 2, damager));

                    if (level == 3) {
                        addKeyFrame(60, () -> DamageManager.getInstance().doTrueDamage(hit, 2, damager));

                        addKeyFrame(80, () -> DamageManager.getInstance().doTrueDamage(hit, 2, damager));
                    }
                }};

                SequenceAPI.startSequence(damageSequence);
            }
        }
    }

    @Override
    public String getName() {
        return "Combo: Frostbite";
    }

    @Override
    public String getEnchantReferenceName() {
        return "Frostbite";
    }

    @Override
    public ArrayList<String> getDescription(int level) {
        return new LoreBuilder()
                .addVariable("", "2s", "4s")
                .setWriteCondition(level == 1)
                .write("Every ").write(ChatColor.YELLOW, "fifth").write(" strike ").write(ChatColor.AQUA, "freezes ").write("enemies,").nextLine()
                .write("applying slowness for 3s")
                .setWriteCondition(level != 1)
                .write("Every ").write(ChatColor.YELLOW, "fourth").write(" strike ").write(ChatColor.AQUA, "freezes ").write("enemies,").nextLine()
                .write("applying slowness for 3s and dealing").nextLine()
                .write(ChatColor.RED, "1❤").write(" of ").write("true damage each second for ").writeVariable(0, level)
                .build();
    }

    @Override
    public boolean isTierTwoEnchant() {
        return false;
    }

    @Override
    public boolean isRareEnchant() {
        return true;
    }

    @Override
    public Material getEnchantItemType() {
        return Material.GOLD_SWORD;
    }
}
