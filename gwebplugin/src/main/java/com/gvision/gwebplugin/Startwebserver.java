

package com.gvision.gwebplugin;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.gvision.gwebplugin.Httphandlers.MyHttpHandler;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;


public class Startwebserver {
    
    public void startHttpServer() throws IOException, URISyntaxException {
        // Skapa en ny HTTP-server på port 8000
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        
        
        server.createContext("/sendmessage", new MyHttpHandler());
        
        // Starta HTTP-servern
        server.start();
        
        // skicka ett meddelande till konsolen när HTTP-servern startas
        Bukkit.getLogger().log(Level.INFO, "HTTP-server startad på port 8000");
    }
}