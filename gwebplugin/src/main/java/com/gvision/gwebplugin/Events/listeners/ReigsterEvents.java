package com.gvision.gwebplugin.Events.listeners;

import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.ArrayList;

import org.bukkit.event.EventHandler;
import org.bukkit.event.server.ServerLoadEvent;

import com.gvision.gwebplugin.Plugin;
import com.gvision.gwebplugin.Configs.FileHanlder;

public class ReigsterEvents implements Listener {
    private final Plugin plugin;
    private boolean eventsRegistered = false;
    private ArrayList<String> offList;
    private FileHanlder webchatOFFListFile;

    public ReigsterEvents(Plugin plugin, FileHanlder webchatOFFListFile, ArrayList<String> OffList) {
        this.plugin = plugin;
        this.offList = OffList;
        this.webchatOFFListFile = webchatOFFListFile;
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
        plugin.getServer().getPluginManager().registerEvents(new PlayerJoinListener(offList, webchatOFFListFile), plugin);
        plugin.getServer().getPluginManager().registerEvents(new PlayerChatListener(plugin, offList), plugin);
    }
}
