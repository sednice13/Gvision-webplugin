package com.gvision.gwebplugin.Events;

import java.io.IOException;

import com.gvision.gwebplugin.Http.Post.Postmessage;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class Eventhandler implements Listener{
    
    Postmessage postHandler = new Postmessage();
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Skicka ett välkomstmeddelande till nya spelare
        String message = "Välkommen till servern!";
        event.getPlayer().sendMessage(ChatColor.GREEN + message);
    }

    @EventHandler
    public void getPlayerMessage(AsyncPlayerChatEvent event) throws IOException {
         
        postHandler.sendHttpmessageToChat(event.getMessage());
        

    }
}
