package com.gvision.gwebplugin.Commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import com.gvision.gwebplugin.Plugin;

public class GwebCommand implements CommandExecutor, TabCompleter {
    private final Plugin plugin;

    public GwebCommand(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1 && "reload".equalsIgnoreCase(args[0])) {
            plugin.reloadConfig();
            sender.sendMessage(ChatColor.GREEN + "gwebplugin konfiguration laddad.");
            return true;
        }

        String version = plugin.getDescription().getVersion();
        sender.sendMessage(ChatColor.YELLOW + "gwebplugin v" + version);
        sender.sendMessage(ChatColor.GRAY + "Använd: /" + label + " reload");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> options = new ArrayList<>();
            options.add("reload");
            String prefix = args[0].toLowerCase();
            options.removeIf(opt -> !opt.startsWith(prefix));
            return options;
        }
        return Collections.emptyList();
    }
}
