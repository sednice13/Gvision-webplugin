package com.gvision.gwebplugin.Commands;

import java.util.LinkedHashMap;
import java.util.Map;


import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;

import com.gvision.gwebplugin.Plugin;
import com.gvision.gwebplugin.Commands.GwebCommandFolder.GwebCommand;
import com.gvision.gwebplugin.Configs.FileHanlder;

public class CommandManager {
    private final Plugin plugin;
    private final Map<String, CommandExecutor> executors = new LinkedHashMap<>();
    private final Map<String, TabCompleter> completers = new LinkedHashMap<>();

    public CommandManager(Plugin plugin) {
        this.plugin = plugin;
        registerBuiltIns();
    }

    private void registerBuiltIns() {
        GwebCommand gweb = new GwebCommand(
            plugin,
            new FileHanlder(plugin, "webSocket.yml"),
            new FileHanlder(plugin, "webchatOFFList.yml"),
            plugin.getOffList()
        );
        register("gweb", gweb, gweb);
    }

    public void registerAll() {
        for (Map.Entry<String, CommandExecutor> entry : executors.entrySet()) {
            String name = entry.getKey();
            PluginCommand command = plugin.getCommand(name);
            if (command == null) {
                plugin.getLogger().warning("Command not found in plugin.yml: " + name);
                continue;
            }
            command.setExecutor(entry.getValue());
            TabCompleter completer = completers.get(name);
            if (completer != null) {
                command.setTabCompleter(completer);
            }
        }
    }

    private void register(String name, CommandExecutor executor, TabCompleter completer) {
        executors.put(name, executor);
        if (completer != null) {
            completers.put(name, completer);
        }
    }
}
