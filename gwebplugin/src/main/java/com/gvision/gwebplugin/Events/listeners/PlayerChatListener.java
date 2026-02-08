package com.gvision.gwebplugin.Events.listeners;

import java.util.ArrayList;
import java.util.logging.Level;

import com.gvision.gwebplugin.Plugin;
import com.gvision.gwebplugin.Commands.GwebCommandFolder.GwebCommand;
import com.gvision.gwebplugin.Configs.FileHanlder;

import io.papermc.paper.event.player.AsyncChatEvent;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class PlayerChatListener implements Listener {
    private final Plugin plugin;
    
    private final ArrayList<String> TempOffList;

    public PlayerChatListener(Plugin plugin,  ArrayList<String> TempOffList) {
        this.plugin = plugin;
        
        this.TempOffList = TempOffList;
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
        
        if (plugin.getWebsocketClient() != null && !TempOffList.contains(player)) {
            plugin.getWebsocketClient().sendChatMessage(player, message);
        }
    }
}
