package com.gvision.gwebplugin.Events.listeners;

import java.util.logging.Level;

import com.gvision.gwebplugin.Plugin;
import com.gvision.gwebplugin.Commands.GwebCommandFolder.GwebCommand;

import io.papermc.paper.event.player.AsyncChatEvent;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class PlayerChatListener implements Listener {
    private final Plugin plugin;

    public PlayerChatListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerChat(AsyncChatEvent event) {
        String message = PlainTextComponentSerializer.plainText().serialize(event.message());
        String player = event.getPlayer().getName();

        if (GwebCommand.consumePendingSocketUpdate(event.getPlayer(), message)) {
            event.setCancelled(true);
            return;
        }

        Bukkit.getLogger().log(Level.INFO, "meddelande " + message);
        if (plugin.getWebsocketClient() != null) {
            plugin.getWebsocketClient().sendChatMessage(player, message);
        }
    }
}
