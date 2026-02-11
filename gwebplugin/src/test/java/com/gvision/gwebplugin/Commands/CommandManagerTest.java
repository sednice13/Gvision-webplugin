package com.gvision.gwebplugin.Commands;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.gvision.gwebplugin.Plugin;

import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.PluginCommandUtils;

public class CommandManagerTest {
    private ServerMock server;
    private Plugin plugin;

    @BeforeEach
    void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.loadSimple(Plugin.class);
        PluginCommand command = PluginCommandUtils.createPluginCommand("gweb", plugin);
        server.getCommandMap().register(plugin.getName(), command);
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    void registerAllSetsExecutor() {
        CommandManager manager = new CommandManager(plugin);
        manager.registerAll();
        assertNotNull(plugin.getCommand("gweb").getExecutor());
    }
}
