package com.gvision.gwebplugin.Http.Httphandlers;


import org.bukkit.Bukkit;
import java.io.IOException;
import java.io.OutputStream;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;


public class MyHttpHandler implements HttpHandler {

    public void handle(HttpExchange exchange) throws IOException {
        
        
       
        if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            
            sendMessageToPlayers(exchange);
        }
        
       
        
    }

    public void sendMessageToPlayers(HttpExchange exchange) throws IOException {
        InputStream requestBody = exchange.getRequestBody();
        InputStreamReader reader = new InputStreamReader(requestBody, StandardCharsets.UTF_8);
       

        JsonObject jsonRequest = JsonParser.parseReader(reader).getAsJsonObject();
    
        if (jsonRequest.has("message")) {
            String message = jsonRequest.get("message").getAsString();
    
            

            for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                player.sendMessage(ChatColor.RED + "<WEBSITE> " + ChatColor.GREEN + message);
            }
    
        }
    
        String response = "Skickat meddelande";
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
   
    
    
    
    
    
}