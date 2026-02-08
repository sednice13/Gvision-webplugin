package com.gvision.gwebplugin;

import org.bukkit.plugin.java.JavaPlugin;
import java.net.URI;
import java.util.ArrayList;
import java.util.logging.Level;
import com.gvision.gwebplugin.Configs.FileHanlder;
import com.gvision.gwebplugin.Commands.CommandManager;
import com.gvision.gwebplugin.Events.listeners.ReigsterEvents;

/*
 * gwebplugin java plugin
 */
public class Plugin extends JavaPlugin {
    private WebsocketMessageClient websocketClient;
    private int reconnectTaskId = -1;
    private FileHanlder websocketUrlFile;
    private ArrayList<FileHanlder> customconfigs;
    private FileHanlder webchatOFFListFile;
    private ArrayList<String> offList;

    

    @Override
    public void onEnable() {
        saveDefaultConfig();

        boolean enabled = getConfig().getBoolean("websocket.enabled", true);
        websocketUrlFile = new FileHanlder(this, "webSocket.yml");
        webchatOFFListFile = new FileHanlder(this, "webchatOFFList.yml");
        offList = new ArrayList<>();
           


       
        
       
        int reconnectSeconds = getConfig().getInt("websocket.reconnectSeconds", 5);

        if (enabled) {
            String url = websocketUrlFile.load("webSocket.yml").getString("websocketurl");
            if (url != null) {
                url = url.trim();
            }
            if (url == null || url.isEmpty()) {
                getLogger().log(Level.WARNING, "WebSocket �r avst�ngd eller saknar URL i webSocket.yml");
            } else {
                connectWebsocket(url, reconnectSeconds);
            }
            
        } else {
            getLogger().log(Level.WARNING, "WebSocket är avstängd eller saknar URL i config.yml");
        }

        new CommandManager(this).registerAll();
        new ReigsterEvents(this, webchatOFFListFile, offList).registerAllEventsListeners();
    }

    @Override
    public void onDisable() {
        if (reconnectTaskId != -1) {
            getServer().getScheduler().cancelTask(reconnectTaskId);
            reconnectTaskId = -1;
        }
        if (websocketClient != null) {
            try {
                websocketClient.close();
            } catch (Exception ex) {
                getLogger().log(Level.WARNING, "Kunde inte stänga WebSocket-klienten", ex);
            }
        }
    }

    private void connectWebsocket(String url, int reconnectSeconds) {
        try {
            websocketClient = new WebsocketMessageClient(this, new URI(url));
            websocketClient.connect();
        } catch (Exception ex) {
            getLogger().log(Level.SEVERE, "Kunde inte ansluta WebSocket", ex);
        }

        if (reconnectSeconds > 0) {
            reconnectTaskId = getServer().getScheduler().scheduleSyncRepeatingTask(this, () -> {
                if (websocketClient == null || websocketClient.isOpen()) {
                    return;
                }
                try {
                    websocketClient.reconnect();
                } catch (Exception ex) {
                    getLogger().log(Level.WARNING, "Misslyckades att återansluta WebSocket", ex);
                }
            }, reconnectSeconds * 20L, reconnectSeconds * 20L);
        }
    }

    public WebsocketMessageClient getWebsocketClient() {
        return websocketClient;
    }

    public ArrayList<String> getOffList() {
        return offList;
    }

}
