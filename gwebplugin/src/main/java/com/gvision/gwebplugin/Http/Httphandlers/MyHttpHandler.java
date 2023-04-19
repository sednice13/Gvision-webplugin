package com.gvision.gwebplugin.Http.Httphandlers;

import java.util.logging.Logger;

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
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.json.JSONObject;
import org.json.JSONTokener;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;


public class MyHttpHandler implements HttpHandler {

    public void handle(HttpExchange exchange) throws IOException {
       
        Bukkit.getLogger().log(Level.INFO, "HTTP-förfrågan mottagen på " + exchange.getRequestURI());
        
        String message = "En ny HTTP-förfrågan har mottagits på " + exchange.getRequestURI();
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            player.sendMessage(ChatColor.GREEN + message);
        }

        if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            Bukkit.getLogger().log(Level.INFO, "Test 1 " );
            sendMessageToPlayers(exchange);
        }
        
       
        
    }

    public void sendMessageToPlayers(HttpExchange exchange) throws IOException {
        InputStream requestBody = exchange.getRequestBody();
        InputStreamReader reader = new InputStreamReader(requestBody, StandardCharsets.UTF_8);
        Bukkit.getLogger().log(Level.INFO, "Test 2 " );
    
        Gson gson = new Gson();
        JsonObject jsonRequest = JsonParser.parseReader(reader).getAsJsonObject();
        Bukkit.getLogger().log(Level.INFO, "Test 3 " );
    
        if (jsonRequest.has("message")) {
            String message = jsonRequest.get("message").getAsString();
    
            Bukkit.getLogger().log(Level.INFO, "<WEBSITE> " + message);
        }
    
        String response = "Skickat meddelande";
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
   
    
    
    
    
    
}