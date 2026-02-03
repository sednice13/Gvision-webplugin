package com.gvision.gwebplugin.Events.listeners;

import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.server.ServerLoadEvent;

import com.gvision.gwebplugin.Plugin;

public class ReigsterEvents implements Listener {
    private final Plugin plugin;
    private boolean eventsRegistered = false;

    public ReigsterEvents(Plugin plugin) {
        this.plugin = plugin;
    }

    public void registerAllEventsListeners() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onServerLoad(ServerLoadEvent event) {
        registerEventListeners();
        HandlerList.unregisterAll(this);
    }

    private void registerEventListeners() {
        if (eventsRegistered) {
            return;
        }
        eventsRegistered = true;
        plugin.getServer().getPluginManager().registerEvents(new PlayerJoinListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new PlayerChatListener(plugin), plugin);
    }
}
