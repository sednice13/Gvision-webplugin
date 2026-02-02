package com.gvision.gwebplugin.listeners;

import java.util.logging.Level;

import com.gvision.gwebplugin.Plugin;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PlayerChatListener implements Listener {
    private final Plugin plugin;

    public PlayerChatListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        String message = event.getMessage();
        String player = event.getPlayer().getName();

        Bukkit.getLogger().log(Level.INFO, "meddelande " + message);
        if (plugin.getWebsocketClient() != null) {
            plugin.getWebsocketClient().sendChatMessage(player, message);
        }
    }
}
