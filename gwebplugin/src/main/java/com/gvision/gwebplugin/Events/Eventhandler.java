package com.gvision.gwebplugin.Events;

import com.gvision.gwebplugin.Plugin;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class Eventhandler implements Listener{
    private final Plugin plugin;

    public Eventhandler(Plugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Skicka ett välkomstmeddelande till nya spelare
        String message = "Välkommen till servern!";
        event.getPlayer().sendMessage(ChatColor.GREEN + message);
    }

    @EventHandler
    public void getPlayerMessage(AsyncPlayerChatEvent event) {
         String message = event.getMessage();
         String player = event.getPlayer().getName();

         Bukkit.getLogger().log(Level.INFO, "meddelande " + message );
        if (plugin.getWebsocketClient() != null) {
            plugin.getWebsocketClient().sendChatMessage(player, message);
        }
        

    }
}
