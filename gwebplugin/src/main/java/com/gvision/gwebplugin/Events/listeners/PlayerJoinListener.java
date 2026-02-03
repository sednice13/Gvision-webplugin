package com.gvision.gwebplugin.Events.listeners;


import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
      Component welcomeMessage = Component.text("Välkommen till servern, " + event.getPlayer().getName() + "!")
              .color(NamedTextColor.GREEN);
      event.getPlayer().sendMessage(welcomeMessage);
    }
}
