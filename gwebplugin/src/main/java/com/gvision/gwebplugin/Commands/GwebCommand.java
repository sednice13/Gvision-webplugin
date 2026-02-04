package com.gvision.gwebplugin.Commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.gvision.gwebplugin.Plugin;
import com.gvision.gwebplugin.Configs.FileHanlder;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class GwebCommand implements CommandExecutor, TabCompleter {
    private static final Map<UUID, GwebCommand> PENDING_SOCKET_UPDATE = new ConcurrentHashMap<>();
    private final Plugin plugin;
    private final FileHanlder socketFile;

    public GwebCommand(Plugin plugin, FileHanlder Socketfile) {
        this.plugin = plugin;
        this.socketFile = Socketfile;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("modify")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(Component.text("Bara spelare kan ändra socket-adress.").color(NamedTextColor.RED));
                return true;
            }
            Player player = (Player) sender;
            PENDING_SOCKET_UPDATE.put(player.getUniqueId(), this);
            sender.sendMessage(Component.textOfChildren(Component.text("Modifierar socketadress").color(NamedTextColor.YELLOW)));
            sender.sendMessage(Component.textOfChildren(Component.text("Säg nya adressen i chatten.").color(NamedTextColor.GOLD)));
            return true;
        }

        return true;
    }

    public static boolean consumePendingSocketUpdate(Player player, String message) {
        GwebCommand handler = PENDING_SOCKET_UPDATE.remove(player.getUniqueId());
        if (handler == null) {
            return false;
        }
        handler.applySocketUpdate(player, message);
        return true;
    }

    private void applySocketUpdate(Player player, String message) {
        String trimmed = message == null ? "" : message.trim();
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            if (trimmed.isEmpty()) {
                player.sendMessage(Component.text("Ingen adress angiven. Försök igen.").color(NamedTextColor.RED));
                return;
            }
            socketFile.setString("websocketurl", trimmed);
            player.sendMessage(Component.text("Socket-adress uppdaterad: ").color(NamedTextColor.GREEN)
                .append(Component.text(trimmed).color(NamedTextColor.YELLOW)));
        });
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> options = new ArrayList<>();
            options.add("modify");
            String prefix = args[0].toLowerCase();
            options.removeIf(opt -> !opt.startsWith(prefix));
            return options;
        }
        return Collections.emptyList();
    }
}
