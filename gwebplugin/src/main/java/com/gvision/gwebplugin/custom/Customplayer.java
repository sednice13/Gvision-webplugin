package com.gvision.gwebplugin.custom;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class Customplayer extends PlayerEvent{

    protected Customplayer(@NotNull Player player) {
        super(player);
        //TODO Auto-generated constructor stub
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getHandlers'");
    }

    public boolean isBannedFromWebchat() {
        //TODO implement method to check if player is banned from webchat
        return false;
    }

    
}
    

