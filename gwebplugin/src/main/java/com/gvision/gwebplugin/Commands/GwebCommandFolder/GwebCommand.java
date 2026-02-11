package com.gvision.gwebplugin.Commands.GwebCommandFolder;

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

public class GwebCommand implements CommandExecutor, TabCompleter  {
    private static final Map<UUID, GwebCommand> PENDING_SOCKET_UPDATE = new ConcurrentHashMap<>();
    private final Plugin plugin;
    private final FileHanlder socketFile;
    private final FileHanlder webchatOFFListFile;
    private final ArrayList<String> offList;
    private final FileHanlder bannedFile;

    public GwebCommand(Plugin plugin, FileHanlder Socketfile, FileHanlder webchatOFFListFile, ArrayList<String> offList, FileHanlder bannedFile) {
        this.plugin = plugin;
        this.socketFile = Socketfile;
        this.webchatOFFListFile = webchatOFFListFile;
        this.offList = offList;
        this.bannedFile = bannedFile;
    }

    /** 
     * /gweb <modify|reload|turnoff|turnon>
     * @param sender
     * @param command
     * @param label
     * @param args
     * @return boolean
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            return argMethod(args[0], null, sender);
        }
        if (args.length == 2 && (args[0].equalsIgnoreCase("ban") || args[0].equalsIgnoreCase("unban"))) {
            return argMethod(args[0], args[1], sender);
        }
        return false;
    }

    /** 
     * Args method for /gweb command
     * @param  arg  - the argument passed to the command
     * @param sender - the sender of the command
     * @return boolean - true if the command was handled, false otherwise
     */
    private boolean argMethod(String arg1, String arg2, CommandSender sender) {
        GwebChoises choice = GwebChoises.fromString(arg1);
        switch (choice) {
            case MODIFY:
                if (!(sender instanceof Player)) {
                    sender.sendMessage(Component.text("Bara spelare kan Ändra socket-adress.").color(NamedTextColor.RED));
                    return true;
                }
                Player player = (Player) sender;
                PENDING_SOCKET_UPDATE.put(player.getUniqueId(), this);
                sender.sendMessage(Component.textOfChildren(Component.text("Modifierar socketadress").color(NamedTextColor.YELLOW)));
                sender.sendMessage(Component.textOfChildren(Component.text("Säg nya adressen i chatten.").color(NamedTextColor.GOLD)));
                return true;
            case RELOAD:
                plugin.reloadConfig();
                sender.sendMessage(Component.text("Konfiguration laddad.").color(NamedTextColor.GREEN));
                return true;
            case TURNON:
                removePlayerFromTurnOFFFile((Player) sender);
                return true;
            case TURNOFF:
                addPlayerToTurnOFFFile((Player) sender);
                return true;
            case BAN:
                if (arg2 == null || arg2.isBlank()) {
                    sender.sendMessage(Component.text("Använd: /gweb ban <spelare>").color(NamedTextColor.RED));
                    return true;
                }
                    
                   if (!checkIfPlayerExists(arg2)) {
                        sender.sendMessage(Component.text("Spelaren finns inte.").color(NamedTextColor.RED));
                        return true;
                    }
                    Player targetBan = getPlayerByName(arg2);
                    banPlayerFromWebchat(targetBan);
                    targetBan.sendMessage(Component.text("Du har blivit bannlyst från webchatten.").color(NamedTextColor.RED));
                    sender.sendMessage(Component.text("Du har bannlyst " + arg2 + " från webchatten.").color(NamedTextColor.GREEN));
                    return true;
            case UNBAN:
                    if (arg2 == null || arg2.isBlank()) {
                        sender.sendMessage(Component.text("AnvÃ¤nd: /gweb unban <spelare>").color(NamedTextColor.RED));
                        return true;
                    }
                    if (!checkIfPlayerExists(arg2)) {
                        sender.sendMessage(Component.text("Spelaren finns inte.").color(NamedTextColor.RED));
                        return true;
                    }
                    Player targetUnban = getPlayerByName(arg2);
                    unbanPlayerFromWebchat(targetUnban);
                    targetUnban.sendMessage(Component.text("Du har blivit avbannlyst från webchatten.").color(NamedTextColor.GREEN));
                    sender.sendMessage(Component.text("Du har avbannlyst " + arg2 + " från webchatten.").color(NamedTextColor.GREEN));
                return true;
            case INVALID:
            default:
                sender.sendMessage(Component.text("Ogiltigt kommando.").color(NamedTextColor.RED));
                return true;
        }
    }

    /** 
     * Checks if a player with the given name exists on the server
     * @param playerName - the name of the player to check for existence
     * @return boolean - true if a player with the given name exists, false otherwise
     */
    private boolean checkIfPlayerExists(String playerName) {
        return plugin.getServer().getPlayerExact(playerName) != null;
    }

    /** 
     * Retrieves a Player object by their exact name
     * @param playerName - the exact name of the player to retrieve
     * @return Player - the Player object corresponding to the given name, or null if no such player is online
     */
    private Player getPlayerByName(String playerName) {
        return plugin.getServer().getPlayerExact(playerName);
    }

    /** 
     * Adds a player to the webchat off list and updates the file accordingly
     * @param player - the player to add to the off list
     */
    private void addPlayerToTurnOFFFile(Player player) {

        List<String> players = webchatOFFListFile.getStringList("players");
        if (players.contains(player.getName())) {
            player.sendMessage(Component.text("Du har redan stängt av webchatten.").color(NamedTextColor.YELLOW));
            return;
        }
        webchatOFFListFile.addStringtoArray("players", player.getName());
        if (!offList.contains(player.getName())) {
            offList.add(player.getName());
        }
        player.sendMessage(Component.text("Webchatten har stängts av för dig.").color(NamedTextColor.GREEN));

    }

    

    private void removePlayerFromTurnOFFFile(Player player) {
        List<String> players = webchatOFFListFile.getStringList("players");
        if (!players.contains(player.getName())) {
            player.sendMessage(Component.text("Du har inte stängt av webchatten.").color(NamedTextColor.YELLOW));
            return;
        }
        players.remove(player.getName());
        webchatOFFListFile.removeStringFromArray("players", player.getName());
        offList.remove(player.getName());
        player.sendMessage(Component.text("Webchatten har aktiverats för dig.").color(NamedTextColor.GREEN));

    }

    /** 
     * Consumes a pending socket update for a player if it exists, applying the new socket address
     * @param player - the player for whom to consume the pending socket update
     * @param message - the new socket address to apply
     * @return boolean - true if a pending socket update was consumed and applied, false otherwise
     */
    public static boolean consumePendingSocketUpdate(Player player, String message) {
        GwebCommand handler = PENDING_SOCKET_UPDATE.remove(player.getUniqueId());
        if (handler == null) {
            return false;
        }
        handler.applySocketUpdate(player, message);
        return true;
    }

    /** 
     * Applies a new socket address for a player, updating the configuration file and notifying the player
     * @param player - the player for whom to apply the new socket address
     * @param message - the new socket address to apply
     */
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

    /** 
     * Bans a player from using the webchat by adding them to the turn off file and the banned players list in the configuration
     * @param player - the player to ban from webchat
     */
    private void banPlayerFromWebchat(Player player) {
        addPlayerToTurnOFFFile(player);
        List<String> bannedPlayers = bannedFile.getStringList("bannedPlayers");
        if (!bannedPlayers.contains(player.getName())) {
            bannedFile.addStringtoArray("bannedPlayers", player.getName());
            
        }
    }
    /** 
     * Unbans a player from the webchat by removing them from the turn off file and the banned players list in the configuration
     * @param player - the player to unban from webchat
     */

    private void unbanPlayerFromWebchat(Player player) {
        removePlayerFromTurnOFFFile(player);
        List<String> bannedPlayers = bannedFile.getStringList("bannedPlayers");
        if (bannedPlayers.contains(player.getName())) {
            bannedFile.removeStringFromArray("bannedPlayers", player.getName());
        }
    }

    /** 
     * Provides tab completion options for the /gweb command, suggesting valid subcommands based on the current input
     * @param sender - the sender of the command
     * @param command - the command being executed
     * @param alias - the alias used for the command
     * @param args - the current arguments passed to the command
     * @return List<String> - a list of tab completion suggestions based on the current input
     */
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> options = new ArrayList<>();
            options.add("modify");
            options.add("reload");
            options.add("turnoff");
            options.add("turnon");
            String prefix = args[0].toLowerCase();
            options.removeIf(opt -> !opt.startsWith(prefix));
            return options;
        }
        return Collections.emptyList();
    }
}
