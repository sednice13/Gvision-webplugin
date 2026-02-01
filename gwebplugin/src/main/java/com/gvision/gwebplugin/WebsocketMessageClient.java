package com.gvision.gwebplugin;

import java.net.URI;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class WebsocketMessageClient extends WebSocketClient {
    private final JavaPlugin plugin;

    public WebsocketMessageClient(JavaPlugin plugin, URI serverUri) {
        super(serverUri);
        this.plugin = plugin;
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        Bukkit.getLogger().log(Level.INFO, "WebSocket ansluten: " + getURI());
    }

    @Override
    public void onMessage(String message) {
        try {
            JsonObject json = JsonParser.parseString(message).getAsJsonObject();
            if (json.has("message") && json.has("mcname")) {
                String text = json.get("message").getAsString();
                String mcname = json.get("mcname").getAsString();
                Bukkit.getScheduler().runTask(plugin, () -> {
                    Bukkit.getServer().getOnlinePlayers().forEach(player -> {
                        player.sendMessage(ChatColor.RED + "<WEBSITE> " + ChatColor.GREEN + mcname + " " + text);
                    });
                });
            }
        } catch (Exception ex) {
            Bukkit.getLogger().log(Level.WARNING, "Ogiltigt WebSocket-meddelande", ex);
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        Bukkit.getLogger().log(Level.INFO, "WebSocket stängd: " + reason);
    }

    @Override
    public void onError(Exception ex) {
        Bukkit.getLogger().log(Level.SEVERE, "WebSocket-fel", ex);
    }

    public void sendChatMessage(String playerName, String message) {
        if (!isOpen()) {
            return;
        }
        JsonObject payload = new JsonObject();
        payload.addProperty("type", "mc-chat");
        payload.addProperty("player", playerName);
        payload.addProperty("message", message);
        send(payload.toString());
    }
}
