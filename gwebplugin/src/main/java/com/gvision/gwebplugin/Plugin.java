package com.gvision.gwebplugin;

import com.gvision.gwebplugin.Events.Eventhandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import java.net.URI;
import java.util.logging.Level;

/*
 * gwebplugin java plugin
 */
public class Plugin extends JavaPlugin implements Listener {
    private WebsocketMessageClient websocketClient;
    private int reconnectTaskId = -1;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        boolean enabled = getConfig().getBoolean("websocket.enabled", false);
        String url = getConfig().getString("websocket.url", "");
        int reconnectSeconds = getConfig().getInt("websocket.reconnectSeconds", 5);

        if (enabled && url != null && !url.isBlank()) {
            connectWebsocket(url, reconnectSeconds);
            
        } else {
            getLogger().log(Level.WARNING, "WebSocket är avstängd eller saknar URL i config.yml");
        }

        // Registrera spelarhändelse
        getServer().getPluginManager().registerEvents(new Eventhandler(this), this);
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
