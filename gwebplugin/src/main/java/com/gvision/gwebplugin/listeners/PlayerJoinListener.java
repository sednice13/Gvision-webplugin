package com.gvision.gwebplugin.listeners;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        String message = "Välkommen till servern!";
        event.getPlayer().sendMessage(ChatColor.GREEN + message);
    }
}
