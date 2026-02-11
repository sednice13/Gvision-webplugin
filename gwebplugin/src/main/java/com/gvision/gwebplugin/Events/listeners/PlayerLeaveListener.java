package com.gvision.gwebplugin.Events.listeners;

import java.util.ArrayList;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerLeaveListener implements Listener {

    private ArrayList<String> Offlist;





    PlayerLeaveListener(ArrayList<String> Offlist) {
    this.Offlist = Offlist;
    }

        
    


  @EventHandler
    public void onPlayerLeave( PlayerQuitEvent event) {
        //TODO implement player leave event handling
        String playerName = event.getPlayer().getName();
        Offlist.removeIf(name -> name.equalsIgnoreCase(playerName));
    }

}
   
