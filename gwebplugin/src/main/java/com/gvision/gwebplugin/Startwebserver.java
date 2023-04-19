

package com.gvision.gwebplugin;

import org.bukkit.Bukkit;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import java.util.logging.Level;


import com.gvision.gwebplugin.Http.Httphandlers.MyHttpHandler;
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