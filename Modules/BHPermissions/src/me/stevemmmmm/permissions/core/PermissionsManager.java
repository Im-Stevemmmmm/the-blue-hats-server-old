package me.stevemmmmm.permissions.core;

/*
 * Copyright (c) 2020. Created by the Pit Player: Stevemmmmm.
 */

import me.stevemmmmm.configapi.core.ConfigAPI;
import me.stevemmmmm.configapi.core.ConfigWriter;
import me.stevemmmmm.permissions.ranks.RankManager;
import me.stevemmmmm.permissions.ranks.RankType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PermissionsManager implements Listener, ConfigWriter {
    private static PermissionsManager instance;

    private HashMap<UUID, Rank> playerRanks = new HashMap<>();
    private HashMap<UUID, Rank> playerStaffRanks = new HashMap<>();

    private HashMap<UUID, PermissionAttachment> playerPermissions = new HashMap<>();

    private PermissionsManager() { }

    public static PermissionsManager getInstance() {
        if (instance == null) instance = new PermissionsManager();

        return instance;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!playerRanks.containsKey(event.getPlayer().getUniqueId())) {
            playerRanks.put(event.getPlayer().getUniqueId(), RankManager.getInstance().getRankByName("Noob"));
        }

        if (!playerStaffRanks.containsKey(event.getPlayer().getUniqueId())) {
            playerStaffRanks.put(event.getPlayer().getUniqueId(), RankManager.getInstance().getRankByName("None"));
        }

        updatePermissions(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        PermissionAttachment attachment = playerPermissions.get(event.getPlayer().getUniqueId());

        for (Map.Entry<String, Boolean> permission : attachment.getPermissions().entrySet()) {
            attachment.unsetPermission(permission.getKey());
        }

        playerPermissions.get(event.getPlayer().getUniqueId()).remove();
        playerPermissions.remove(event.getPlayer().getUniqueId());
    }

    public Rank getPlayerRank(Player player) {
        if (!playerStaffRanks.get(player.getUniqueId()).getName().equals("None")) {
            return playerStaffRanks.get(player.getUniqueId());
        }

        return playerRanks.get(player.getUniqueId());
    }

    public void setPlayerRank(Player player, Rank rank) {
        if (rank.getRankType() == RankType.STAFF) {
            playerStaffRanks.put(player.getUniqueId(), rank);
        }

        if (rank.getRankType() == RankType.PLAYER) {
            playerRanks.put(player.getUniqueId(), rank);
        }

        updatePermissions(player);
    }

    public boolean playerIsStaff(Player player) {
        return !playerStaffRanks.get(player.getUniqueId()).getName().equals("None");
    }

    public void updatePermissions(Player player) {
        if (!playerPermissions.containsKey(player.getUniqueId())) {
            playerPermissions.put(player.getUniqueId(), player.addAttachment(Main.INSTANCE));
        }

        PermissionAttachment attachment = playerPermissions.get(player.getUniqueId());

        for (Map.Entry<String, Boolean> permission : attachment.getPermissions().entrySet()) {
            attachment.unsetPermission(permission.getKey());
        }

        //Default Permissions
        attachment.setPermission("minecraft.command.me", false);
        attachment.setPermission("bukkit.command.help", false);
        attachment.setPermission("bukkit.command.plugins", false);

        for (Map.Entry<String, Boolean> entry : getPlayerRank(player).getPermissions().entrySet()) {
            attachment.setPermission(entry.getKey(), entry.getValue());
        }
    }

    public void readConfig() {
        for (Map.Entry<UUID, String> entry : ConfigAPI.read(Main.INSTANCE, "PlayerRanks").entrySet()) {
            playerRanks.put(entry.getKey(), RankManager.getInstance().getRankByName(entry.getValue()));
        }

        for (Map.Entry<UUID, String> entry : ConfigAPI.read(Main.INSTANCE, "StaffRanks").entrySet()) {
            playerStaffRanks.put(entry.getKey(), RankManager.getInstance().getRankByName(entry.getValue()));
        }
    }

    @Override
    public void writeToConfig() {
        HashMap<UUID, String> finalizedA = new HashMap<>();
        HashMap<UUID, String> finalizedB = new HashMap<>();

        for (Map.Entry<UUID, Rank> entry : playerRanks.entrySet()) {
            finalizedA.put(entry.getKey(), entry.getValue().getName());
        }

        for (Map.Entry<UUID, Rank> entry : playerStaffRanks.entrySet()) {
            finalizedB.put(entry.getKey(), entry.getValue().getName());
        }

        ConfigAPI.write(Main.INSTANCE, "PlayerRanks", finalizedA);
        ConfigAPI.write(Main.INSTANCE, "StaffRanks", finalizedB);
    }
}
