package com.gvision.gwebplugin.Commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.bukkit.configuration.file.FileConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.gvision.gwebplugin.Configs.FileHanlder;
import com.gvision.gwebplugin.Plugin;
import com.gvision.gwebplugin.Commands.GwebCommandFolder.GwebCommand;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;

public class GwebCommandTest {
    private ServerMock server;
    private Plugin plugin;

    @BeforeEach
    void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(Plugin.class);
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    void modifyCommandUpdatesWebsocketUrl() {
        PlayerMock player = server.addPlayer();
        FileHanlder socketFile = new FileHanlder(plugin, "webSocket.yml");
        GwebCommand command = new GwebCommand(plugin, socketFile, socketFile, null);

        command.onCommand(player, null, "gweb", new String[] { "modify" });

        boolean consumed = GwebCommand.consumePendingSocketUpdate(player, "ws://localhost:9308/ws");
        assertTrue(consumed);

        FileConfiguration config = socketFile.load("webSocket.yml");
        assertEquals("ws://localhost:9308/ws", config.getString("websocketurl"));
    }
}
