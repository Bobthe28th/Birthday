package me.bobthe28th.birthday;

import me.bobthe28th.birthday.commands.BCommands;
import me.bobthe28th.birthday.commands.BTabCompleter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public class Main extends JavaPlugin implements Listener {

    public static HashMap<Player,GamePlayer> GamePlayers;
    public static boolean pvp = false; //todo breakblocks

    @Override
    public void onEnable() {
        Bukkit.broadcastMessage("Man");
        getServer().getPluginManager().registerEvents(this, this);

        String[] commandNames = new String[]{"test","pvp"};
        BCommands commands = new BCommands(this);
        BTabCompleter tabCompleter = new BTabCompleter();
        for (String commandName : commandNames) {
            PluginCommand command = getCommand(commandName);
            if (command != null) {
                command.setExecutor(commands);
                command.setTabCompleter(tabCompleter);
            }
        }

        GamePlayers = new HashMap<>();

        for(Player player : Bukkit.getOnlinePlayers()) {
            GamePlayers.put(player,new GamePlayer(this,player));
        }
    }

    @Override
    public void onDisable() {
        if (GamePlayers != null) {
            for (GamePlayer gamePlayer : GamePlayers.values()) {
                gamePlayer.removeNotMap();
            }
            GamePlayers.clear();
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.setJoinMessage(ChatColor.GRAY + "[" + ChatColor.GREEN + "+" + ChatColor.GRAY + "] " + ChatColor.YELLOW + event.getPlayer().getDisplayName() + " joined");
        GamePlayers.put(event.getPlayer(),new GamePlayer(this,event.getPlayer()));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(ChatColor.GRAY + "[" + ChatColor.RED + "-" + ChatColor.GRAY + "] " + ChatColor.YELLOW + event.getPlayer().getDisplayName() + " left");
        if (GamePlayers.get(event.getPlayer()) != null) {
            GamePlayers.remove(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        event.setFormat(event.getPlayer().getDisplayName() + ": " + event.getMessage());
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!pvp && event.getCause() != EntityDamageEvent.DamageCause.VOID) {
            event.setCancelled(true);
        }
    }

}
