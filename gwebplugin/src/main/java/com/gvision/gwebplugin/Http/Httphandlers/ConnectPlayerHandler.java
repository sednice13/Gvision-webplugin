package com.gvision.gwebplugin.Http.Httphandlers;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.logging.Level;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;


public class ConnectPlayerHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // TODO Auto-generated method stub
        if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
           
            getUUIDwithUsername(exchange);
        }
    }

    public void getUUIDwithUsername(HttpExchange exchange) throws IOException {
        InputStream requestBody = exchange.getRequestBody();
        InputStreamReader reader = new InputStreamReader(requestBody, StandardCharsets.UTF_8);
        
        JsonObject jsonRequest = JsonParser.parseReader(reader).getAsJsonObject();

        
       
        JsonObject jsonResponse = new JsonObject();
    
        if (jsonRequest.has("playername")) {
           
            String plname = jsonRequest.get("playername").getAsString();

            
    
            Player player = Bukkit.getPlayer(plname);

           
    
            if(player != null) {
              
                Bukkit.getLogger().log(Level.INFO, "Test 4 " );
                UUID uuid = player.getUniqueId(); 
    
                jsonResponse.addProperty("playername", plname);
                jsonResponse.addProperty("uuid", uuid.toString());

                
               
            } else {
                jsonResponse.addProperty("error", "Player not found or not online");
            }
    
        } else {
            jsonResponse.addProperty("error", "No playername provided");
        }
    
        String response = jsonResponse.toString();
        
        
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
    
}
