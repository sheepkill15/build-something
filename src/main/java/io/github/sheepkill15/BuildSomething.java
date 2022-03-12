package io.github.sheepkill15;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class BuildSomething extends JavaPlugin implements Listener {

    public final static String CommandChatPrefix = ChatColor.BOLD + "" + ChatColor.YELLOW + "[" + ChatColor.RED + "BS" + ChatColor.YELLOW + "] " + ChatColor.RESET;
    public Location buildStartLocation;
    public Location spectateStartLocation;

    private GameManager manager;

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        LeavePlayer(player);
    }

    @Override
    public void onEnable() {
        this.saveDefaultConfig();

        FileConfiguration config = getConfig();
        String wordsTxt = config.getString("words");
        ArrayList<String> words = new ArrayList<>();
        if(wordsTxt == null || wordsTxt.isEmpty()) {
            words = null;
        }
        else {
            File wordsFile = new File(wordsTxt);
            try {
                Scanner wordsScanner = new Scanner(wordsFile);
                while(wordsScanner.hasNextLine()) {
                    String word = wordsScanner.nextLine();
                    words.add(word);
                }

            } catch (FileNotFoundException e) {
                getLogger().info("File '" + wordsTxt + "' not found!");
                words = null;
            }
        }

        manager = new GameManager(this, words, config.getInt("required_players"), config.getInt("start"), config.getInt("inbetween"), config.getInt("round_duration"));
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("Build Something is enabled!");
    }

    @Override
    public void onDisable() {
        PlayerQuitEvent.getHandlerList().unregister((Listener) this);
        getLogger().info("Build Something is disabled!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage("Only players can run this command!");
            return true;
        }

        Player player = (Player) sender;
        if(args.length == 0) {
            SendCommandUsage(player);
        }
        else {
            switch (args[0].toLowerCase()) {
                case "join":
                    JoinPlayer(player);
                    break;
                case "leave":
                    LeavePlayer(player);
                    break;
                case "bset":
                    SetBuildStart(player);
                    break;
                case "sset":
                    SetSpectateStart(player);
                    break;
                default:
                    NoSuchCommand(player);
                    return false;
            }
        }
        return true;
    }

    private void LeavePlayer(Player player) {
        manager.LeavePlayer(player);
        player.sendMessage(CommandChatPrefix + "Successfully left the game!");
    }

    private void JoinPlayer(Player player)
    {
        manager.JoinPlayer(player);
        player.sendMessage(CommandChatPrefix + "Successfully joined the game!");
    }

    private void SetBuildStart(Player player)
    {
        buildStartLocation = player.getLocation();
        player.sendMessage(CommandChatPrefix + "Builder starting point set!");
    }

    private void SetSpectateStart(Player player)
    {
        spectateStartLocation = player.getLocation();
        player.sendMessage(CommandChatPrefix + "Spectator starting point set!");
    }

    private void NoSuchCommand(Player player) {
        player.sendMessage(CommandChatPrefix + "No such command exists!");
    }

    private void SendCommandUsage(CommandSender sender) {
        sender.sendMessage(CommandChatPrefix + "Available commands:");
        if(sender.hasPermission("com.sanddunes.buildsomething.join")) {
            sender.sendMessage("/bs join: Join a game of BuildSomething!");
        }
        if(sender.hasPermission("com.sanddunes.buildsomething.leave")) {
            sender.sendMessage("/bs leave: Leave the game");
        }
        if(sender.hasPermission("com.sanddunes.buildsomething.bset"))
        {
            sender.sendMessage(ChatColor.STRIKETHROUGH + "/bs bset: Set the starting point for builders (doesn't work yet)");
        }
        if(sender.hasPermission("com.sanddunes.buildsomething.sset"))
        {
            sender.sendMessage(ChatColor.STRIKETHROUGH + "/bs sset: Set the starting point for spectators (doesn't work yet)");
        }
    }
}
