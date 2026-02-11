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

import com.gvision.gwebplugin.Configs.FileHanlder;
import com.gvision.gwebplugin.Plugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class GwebCommand implements CommandExecutor, TabCompleter {
    private static final String PERM_MODIFY = "gwebplugin.gweb.modify";
    private static final String PERM_RELOAD = "gwebplugin.gweb.reload";
    private static final String PERM_TURNOFF = "gwebplugin.gweb.turnoff";
    private static final String PERM_TURNON = "gwebplugin.gweb.turnon";
    private static final String PERM_BAN = "gwebplugin.gweb.ban";
    private static final String PERM_UNBAN = "gwebplugin.gweb.unban";

    private static final Map<UUID, GwebCommand> PENDING_SOCKET_UPDATE = new ConcurrentHashMap<>();

    private final Plugin plugin;
    private final FileHanlder socketFile;
    private final FileHanlder webchatOFFListFile;
    private final ArrayList<String> offList;
    private final FileHanlder bannedFile;

    public GwebCommand(
            Plugin plugin,
            FileHanlder socketfile,
            FileHanlder webchatOFFListFile,
            ArrayList<String> offList,
            FileHanlder bannedFile) {
        this.plugin = plugin;
        this.socketFile = socketfile;
        this.webchatOFFListFile = webchatOFFListFile;
        this.offList = offList;
        this.bannedFile = bannedFile;
    }

    /** 
     * Handles the execution of the /gweb command with various subcommands. It checks the number of arguments and the first argument to determine which subcommand to execute (e.g., "modify", "reload", "turnon", "turnoff", "ban", "unban"). It also checks the sender's permissions for each subcommand and performs the corresponding actions, such as modifying the websocket URL, reloading the configuration, toggling webchat for the sender, and banning or unbanning players from webchat. The method returns true if a valid subcommand was executed (regardless of success), and false if the command was not recognized or had incorrect arguments.
     * @param sender - the CommandSender who issued the command, used to check permissions and send feedback messages
     * @param command - the Command object representing the executed command
     * @param label - the alias of the command used
     * @param args - the arguments passed with the command, where args[0] determines the subcommand and args[1] is used for the "ban" and "unban" subcommands to specify the target player's name
     * @return boolean - true if a valid subcommand was executed, false if the command was not recognized or had incorrect arguments
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
     * Handles the logic for each subcommand based on the first argument (arg1). It checks the sender's permissions for each command and performs the corresponding action, such as modifying the websocket URL, reloading the configuration, turning webchat on or off for the sender, and banning or unbanning players from webchat. The method also sends appropriate feedback messages to the sender based on the outcome of each command.
     * @param arg1 - the first argument which determines the subcommand to execute (e.g., "modify", "reload", "turnon", "turnoff", "ban", "unban")
     * @param arg2 - the second argument used for the "ban" and "unban" subcommands to specify the target player's name
     * @param sender - the CommandSender who issued the command, used to check permissions and send feedback messages
     * @return boolean - true if the command was handled (regardless of success), false if the command was not recognized or had incorrect arguments
     */
    private boolean argMethod(String arg1, String arg2, CommandSender sender) {
        GwebChoises choice = GwebChoises.fromString(arg1);
        switch (choice) {
            case MODIFY:
                if (!hasPermission(sender, PERM_MODIFY)) {
                    return true;
                }
                if (!(sender instanceof Player)) {
                    sender.sendMessage(Component.text("Bara spelare kan andra socket-adress.").color(NamedTextColor.RED));
                    return true;
                }
                Player player = (Player) sender;
                PENDING_SOCKET_UPDATE.put(player.getUniqueId(), this);
                sender.sendMessage(Component.text("Modifierar socketadress").color(NamedTextColor.YELLOW));
                sender.sendMessage(Component.text("Sag nya adressen i chatten.").color(NamedTextColor.GOLD));
                return true;
            case RELOAD:
                if (!hasPermission(sender, PERM_RELOAD)) {
                    return true;
                }
                plugin.reloadConfig();
                sender.sendMessage(Component.text("Konfiguration laddad.").color(NamedTextColor.GREEN));
                return true;
            case TURNON:
                if (!hasPermission(sender, PERM_TURNON)) {
                    return true;
                }
                if (!(sender instanceof Player)) {
                    sender.sendMessage(Component.text("Bara spelare kan anvanda detta kommando.").color(NamedTextColor.RED));
                    return true;
                }
                removePlayerFromTurnOFFFile((Player) sender);
                return true;
            case TURNOFF:
                if (!hasPermission(sender, PERM_TURNOFF)) {
                    return true;
                }
                if (!(sender instanceof Player)) {
                    sender.sendMessage(Component.text("Bara spelare kan anvanda detta kommando.").color(NamedTextColor.RED));
                    return true;
                }
                addPlayerToTurnOFFFile((Player) sender);
                return true;
            case BAN:
                if (!hasPermission(sender, PERM_BAN)) {
                    return true;
                }
                if (arg2 == null || arg2.isBlank()) {
                    sender.sendMessage(Component.text("Anvand: /gweb ban <spelare>").color(NamedTextColor.RED));
                    return true;
                }
                if (!checkIfPlayerExists(arg2)) {
                    sender.sendMessage(Component.text("Spelaren finns inte.").color(NamedTextColor.RED));
                    return true;
                }
                Player targetBan = getPlayerByName(arg2);
                banPlayerFromWebchat(targetBan);
                targetBan.sendMessage(Component.text("Du har blivit bannlyst fran webchatten.").color(NamedTextColor.RED));
                sender.sendMessage(Component.text("Du har bannlyst " + arg2 + " fran webchatten.").color(NamedTextColor.GREEN));
                return true;
            case UNBAN:
                if (!hasPermission(sender, PERM_UNBAN)) {
                    return true;
                }
                if (arg2 == null || arg2.isBlank()) {
                    sender.sendMessage(Component.text("Anvand: /gweb unban <spelare>").color(NamedTextColor.RED));
                    return true;
                }
                if (!checkIfPlayerExists(arg2)) {
                    sender.sendMessage(Component.text("Spelaren finns inte.").color(NamedTextColor.RED));
                    return true;
                }
                Player targetUnban = getPlayerByName(arg2);
                unbanPlayerFromWebchat(targetUnban);
                targetUnban.sendMessage(Component.text("Du har blivit avbannlyst fran webchatten.").color(NamedTextColor.GREEN));
                sender.sendMessage(Component.text("Du har avbannlyst " + arg2 + " fran webchatten.").color(NamedTextColor.GREEN));
                return true;
            case INVALID:
            default:
                sender.sendMessage(Component.text("Ogiltigt kommando.").color(NamedTextColor.RED));
                return true;
        }
    }

    /** 
     * Checks if the sender has the specified permission. If the sender does not have the permission, it sends a message to the sender indicating that they do not have permission for the command.
     * @param sender - the CommandSender whose permissions are being checked
     * @param permission - the permission node to check against the sender's permissions
     * @return boolean - true if the sender has the specified permission, false otherwise
     */
    private boolean hasPermission(CommandSender sender, String permission) {
        if (sender.hasPermission(permission)) {
            return true;
        }
        sender.sendMessage(Component.text("Du har inte behorighet for detta kommando.").color(NamedTextColor.RED));
        return false;
    }

    /** 
     * Checks if a player with the given name exists on the server by attempting to retrieve the player object using the server's getPlayerExact method. This method returns true if a player with the exact name exists and is currently online, and false otherwise.
     * @param playerName - the name of the player to check for existence
     * @return boolean - true if a player with the exact name exists and is online, false otherwise
     */
    private boolean checkIfPlayerExists(String playerName) {
        return plugin.getServer().getPlayerExact(playerName) != null;
    }

    /** 
     * Retrieves a Player object by their exact name using the server's getPlayerExact method. This method returns the Player object if a player with the exact name exists and is currently online, or null if no such player is found.
     * @param playerName - the exact name of the player to retrieve
     * @return Player - 
     */
    private Player getPlayerByName(String playerName) {
        return plugin.getServer().getPlayerExact(playerName);
    }

    /** 
     * Adds a player to the list of players for whom webchat is turned off by updating the webchatOFFListFile and the offList. It first checks if the player is already in the list and sends a message if they are. If not, it adds the player's name to the file and the list, and sends a confirmation message to the player.
     * @param player - the Player object representing the player to be added to the turn-off list for webchat
     */
    private void addPlayerToTurnOFFFile(Player player) {
        List<String> players = webchatOFFListFile.getStringList("players");
        if (players.contains(player.getName())) {
            player.sendMessage(Component.text("Du har redan stangt av webchatten.").color(NamedTextColor.YELLOW));
            return;
        }
        webchatOFFListFile.addStringtoArray("players", player.getName());
        if (offList != null && !offList.contains(player.getName())) {
            offList.add(player.getName());
        }
        player.sendMessage(Component.text("Webchatten har stangts av for dig.").color(NamedTextColor.GREEN));
    }

    /** 
     * Removes a player from the list of players for whom webchat is turned off by updating the webchatOFFListFile and the offList. It first checks if the player is in the list and sends a message if they are not. If they are in the list, it removes the player's name from the file and the list, and sends a confirmation message to the player.
     * @param player - the Player object representing the player to be removed from the turn-off list for webchat
     */
    private void removePlayerFromTurnOFFFile(Player player) {
        List<String> players = webchatOFFListFile.getStringList("players");
        if (!players.contains(player.getName())) {
            player.sendMessage(Component.text("Du har inte stangt av webchatten.").color(NamedTextColor.YELLOW));
            return;
        }
        players.remove(player.getName());
        webchatOFFListFile.removeStringFromArray("players", player.getName());
        if (offList != null) {
            offList.remove(player.getName());
        }
        player.sendMessage(Component.text("Webchatten har aktiverats for dig.").color(NamedTextColor.GREEN));
    }

    /** 
     * Checks if there is a pending socket update for the given player and message. If there is a pending update, it applies the socket update using the applySocketUpdate method and returns true. If there is no pending update for the player, it returns false. This method is used to handle the case where a player is expected to provide a new socket address in chat after issuing the modify command.
     * @param player - the Player object representing the player for whom to check for a pending socket update
     * @param message - the message that may contain the new socket address to be applied if there is a pending update for the player
     * @return boolean - true if a pending socket update was found and applied for the player, false if no pending update was found for the player
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
     * Applies a socket update by taking the message provided by the player, trimming it, and if it is not empty, updating the websocket URL in the configuration file. It also sends feedback messages to the player indicating whether the update was successful or if no address was provided. This method is intended to be called when a player has issued the modify command and is expected to provide a new socket address in chat.
     * @param player - the Player object representing the player who provided the new socket address
     * @param message - the message containing the new socket address to be applied
     */
    private void applySocketUpdate(Player player, String message) {
        String trimmed = message == null ? "" : message.trim();
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            if (trimmed.isEmpty()) {
                player.sendMessage(Component.text("Ingen adress angiven. Forsok igen.").color(NamedTextColor.RED));
                return;
            }
            socketFile.setString("websocketurl", trimmed);
            player.sendMessage(
                    Component.text("Socket-adress uppdaterad: ").color(NamedTextColor.GREEN)
                            .append(Component.text(trimmed).color(NamedTextColor.YELLOW)));
        });
    }

    /** 
     * Bans a player from webchat by adding them to the banned list in the bannedFile and ensuring they are also added to the turn-off list for webchat.
     * @param player - the Player object representing the player to be banned from webchat
     */
    private void banPlayerFromWebchat(Player player) {
        addPlayerToTurnOFFFile(player);
        List<String> bannedPlayers = bannedFile.getStringList("bannedPlayers");
        if (!bannedPlayers.contains(player.getName())) {
            bannedFile.addStringtoArray("bannedPlayers", player.getName());
        }
    }

    /** 
     * Unbans a player from webchat by removing them from the banned list in the bannedFile and ensuring they are also removed from the turn-off list for webchat.
     * @param player - the Player object representing the player to be unbanned from webchat
     */
    private void unbanPlayerFromWebchat(Player player) {
        removePlayerFromTurnOFFFile(player);
        List<String> bannedPlayers = bannedFile.getStringList("bannedPlayers");
        if (bannedPlayers.contains(player.getName())) {
            bannedFile.removeStringFromArray("bannedPlayers", player.getName());
        }
    }

    /** 
     * Provides tab completion options for the /gweb command based on the sender's permissions and the current arguments. If the sender is typing the first argument, it suggests subcommands that the sender has permission to use. If the sender is typing the second argument for the "ban" or "unban" subcommands, it allows for player name completion by returning null. For any other cases, it returns an empty list to indicate no suggestions.
     * @param sender - the CommandSender who is requesting tab completion, used to check permissions for suggesting subcommands
     * @param command - the Command object representing the command for which tab completion is being requested
     * @param alias - the alias of the command used, which can be used to determine which command is being completed if multiple aliases exist
     * @param args - the current arguments typed by the sender, where args[0] is used to determine which subcommand is being completed and args[1] is used for player name completion for the "ban" and "unban" subcommands
     * @return List<String> - a list of tab completion suggestions based on the sender's permissions and the current arguments, or null for player name completion, or an empty list if no suggestions are available
     */
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> options = new ArrayList<>();
            if (sender.hasPermission(PERM_MODIFY)) {
                options.add("modify");
            }
            if (sender.hasPermission(PERM_RELOAD)) {
                options.add("reload");
            }
            if (sender.hasPermission(PERM_TURNOFF)) {
                options.add("turnoff");
            }
            if (sender.hasPermission(PERM_TURNON)) {
                options.add("turnon");
            }
            if (sender.hasPermission(PERM_BAN)) {
                options.add("ban");
            }
            if (sender.hasPermission(PERM_UNBAN)) {
                options.add("unban");
            }
            String prefix = args[0].toLowerCase();
            options.removeIf(opt -> !opt.startsWith(prefix));
            return options;
        }
        if (args.length == 2) {
            if ("ban".equalsIgnoreCase(args[0]) && sender.hasPermission(PERM_BAN)) {
                return null;
            }
            if ("unban".equalsIgnoreCase(args[0]) && sender.hasPermission(PERM_UNBAN)) {
                return null;
            }
        }
        return Collections.emptyList();
    }
}
