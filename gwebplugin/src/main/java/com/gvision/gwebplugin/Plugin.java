package com.gvision.gwebplugin;


import com.gvision.gwebplugin.Events.Eventhandler;

import org.bukkit.event.Listener;

import org.bukkit.plugin.java.JavaPlugin;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.Level;

/*
 * gwebplugin java plugin
 */
public class Plugin extends JavaPlugin implements Listener {

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
        getServer().getPluginManager().registerEvents(new Eventhandler(), this);

    }


}
