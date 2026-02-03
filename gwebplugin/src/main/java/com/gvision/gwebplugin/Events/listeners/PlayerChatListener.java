package com.gvision.gwebplugin.Events.listeners;

import java.util.logging.Level;

import com.gvision.gwebplugin.Plugin;

import io.papermc.paper.event.player.AsyncChatEvent;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;


public class PlayerChatListener implements Listener {
    private final Plugin plugin;

    public PlayerChatListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerChat(AsyncChatEvent event) {
        String message = event.message().toString();
        String player = event.getPlayer().getName();

        Bukkit.getLogger().log(Level.INFO, "meddelande " + message);
        if (plugin.getWebsocketClient() != null) {
            plugin.getWebsocketClient().sendChatMessage(player, message);
        }
    }
}
