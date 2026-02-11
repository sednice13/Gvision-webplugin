package com.gvision.gwebplugin.Events.listeners;


import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.gvision.gwebplugin.Configs.FileHanlder;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class PlayerJoinListener implements Listener {

  private final ArrayList<String> tempOffList;
  private final FileHanlder webchatOFFListFile;
  private final FileHanlder bannedFile;

  public PlayerJoinListener(ArrayList<String> tempOffList, FileHanlder webchatOFFListFile, FileHanlder bannedFile) { 
    this.tempOffList = tempOffList;
    this.webchatOFFListFile = webchatOFFListFile;
    this.bannedFile = bannedFile;
  }

    /** 
     * Handles the PlayerJoinEvent by sending a welcome message to the player, checking if they are banned from webchat, and checking if webchat is turned off for them. If the player is banned, they are added to the tempOffList and sent a message. If webchat is turned off for them, they are also added to the tempOffList and sent a message.
     * @param event - the PlayerJoinEvent triggered when a player joins the server
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
      Component welcomeMessage = Component.text("Välkommen till servern, " + event.getPlayer().getName() + "!")
              .color(NamedTextColor.GREEN);
      event.getPlayer().sendMessage(welcomeMessage);

      String playerName = event.getPlayer().getName();
      
      if (isPlayerBanned(playerName)) {
          Component bannedMessage = Component.text("Du är bannad från webchat.").color(NamedTextColor.RED);
          event.getPlayer().sendMessage(bannedMessage);
          tempOffList.add(playerName);
          return;
      }

      webchatOFFListFile.getStringList("players").forEach(name -> {
          if (name.equalsIgnoreCase(playerName)) {
              tempOffList.add(playerName);
              Component webchatOffMessage = Component.text("Webchat är avstängt för dig.").color(NamedTextColor.RED);
              event.getPlayer().sendMessage(webchatOffMessage);
          }
      });

     
    }
    /** 
     * Checks if a player is banned by comparing their name against the list of banned players in the bannedFile.
     * @param playerName - the name of the player to check
     * @return boolean - true if the player is banned, false otherwise
     */
    private boolean isPlayerBanned(String playerName) {
        return bannedFile.getStringList("bannedPlayers").stream()
                .anyMatch(name -> name.equalsIgnoreCase(playerName));
    }
}
