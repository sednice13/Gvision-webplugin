package com.gvision.gwebplugin.Commands;

import java.util.LinkedHashMap;
import java.util.Map;


import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;

import com.gvision.gwebplugin.Plugin;
import com.gvision.gwebplugin.Commands.GwebCommandFolder.GwebChoises;
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


    /** 
     * Registers built-in commands.
     */
    private void registerBuiltIns() {
        GwebCommand gweb = new GwebCommand(plugin, new FileHanlder(plugin, "webSocket.yml"));
        register("gweb", gweb, gweb);
    }


    /** 
     * Registers all commands with the server.
      * This should be called during plugin initialization after all commands have been added.
      * It will log a warning for any command that is defined in this manager but not found in plugin.yml.
      * Commands are registered in the order they were added to ensure predictable behavior.
      */
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

    /** 
     * Registers a command with its executor and optional tab completer.
     * @param name The name of the command as defined in plugin.yml.
     * @param executor The CommandExecutor that will handle the command logic.
     * @param completer An optional TabCompleter for providing tab completion suggestions. Can be null if not needed.
      * Commands are stored in a LinkedHashMap to preserve the order of registration, which can be important for command handling and tab completion behavior.
      * This method does not interact with the server directly; it only stores the command information. The actual registration with the server happens in registerAll().
      * It is important to ensure that the command name matches an entry in plugin.yml, otherwise it will not be registered properly and a warning will be logged during registerAll().
      */
     
    private void register(String name, CommandExecutor executor, TabCompleter completer) {
        executors.put(name, executor);
        if (completer != null) {
            completers.put(name, completer);
        }
    }

    /** 
     * @param input
     * @return GwebChoises
     */
    private GwebChoises parseChoice(String input) {
        try {
            return GwebChoises.valueOf(input.toUpperCase());
        } catch (IllegalArgumentException ex) {
            return GwebChoises.INVALID;
        }
    }
}
