package com.gvision.gwebplugin;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import java.net.URI;
import java.util.logging.Level;
import com.gvision.gwebplugin.Configs.FileHanlder;
import com.gvision.gwebplugin.listeners.PlayerChatListener;
import com.gvision.gwebplugin.listeners.PlayerJoinListener;

/*
 * gwebplugin java plugin
 */
public class Plugin extends JavaPlugin implements Listener {
    private WebsocketMessageClient websocketClient;
    private int reconnectTaskId = -1;
    private FileHanlder websocketUrlFile;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        boolean enabled = getConfig().getBoolean("websocket.enabled", true);
        FileHanlder websocketUrlFile = new FileHanlder(this, "webSocket.yml");
       
        
       
        int reconnectSeconds = getConfig().getInt("websocket.reconnectSeconds", 5);

        if (enabled) {
            connectWebsocket("TEMPfakeurl", reconnectSeconds);
            
        } else {
            getLogger().log(Level.WARNING, "WebSocket är avstängd eller saknar URL i config.yml");
        }

        // Registrera spelarhändelser
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerChatListener(this), this);
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
}
