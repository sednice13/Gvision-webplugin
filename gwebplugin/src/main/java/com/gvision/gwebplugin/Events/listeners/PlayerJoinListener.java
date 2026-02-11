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
    private boolean isPlayerBanned(String playerName) {
        return bannedFile.getStringList("bannedPlayers").stream()
                .anyMatch(name -> name.equalsIgnoreCase(playerName));
    }
}
