package com.gvision.gwebplugin;

import java.util.logging.Logger;
import org.bukkit.event.Listener;

import org.bukkit.plugin.java.JavaPlugin;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.Level;

import org.bukkit.ChatColor;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;


/*
 * gwebplugin java plugin
 */
public class Plugin extends JavaPlugin implements Listener
{
  private static final Logger LOGGER=Logger.getLogger("gwebplugin");

  @Override
  public void onEnable() {
      // Starta HTTP-servern när plugin laddas
      try {
         Startwebserver server = new Startwebserver();

         server.startHttpServer();
          
      } catch (IOException | URISyntaxException ex) {
          getLogger().log(Level.SEVERE, "Kunde inte starta HTTP-server", ex);
      }
      
      // Registrera spelarhändelse
      getServer().getPluginManager().registerEvents(this, this);
  }
  
  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
      // Skicka ett välkomstmeddelande till nya spelare
      String message = "Välkommen till servern!";
      event.getPlayer().sendMessage(ChatColor.GREEN + message);
  }

  @EventHandler 
  public void getPlayerMessage(AsyncPlayerChatEvent event) {

    event.getMessage();
    
  }
  
}

