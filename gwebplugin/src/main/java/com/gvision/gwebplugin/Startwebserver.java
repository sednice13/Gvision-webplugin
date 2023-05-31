

package com.gvision.gwebplugin;

import org.bukkit.Bukkit;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import java.util.logging.Level;

import com.gvision.gwebplugin.Http.Httphandlers.ConnectPlayerHandler;
import com.gvision.gwebplugin.Http.Httphandlers.MyHttpHandler;
import com.sun.net.httpserver.HttpServer;


public class Startwebserver {
    
    public void startHttpServer() throws IOException, URISyntaxException {
        
        HttpServer server = HttpServer.create(new InetSocketAddress(9308), 0);
        
        
        server.createContext("/sendmessage", new MyHttpHandler());
        server.createContext("/connectuser", new ConnectPlayerHandler());
        server.start();
        Bukkit.getLogger().log(Level.INFO, "HTTP-server startad på port 8000");
    }
}