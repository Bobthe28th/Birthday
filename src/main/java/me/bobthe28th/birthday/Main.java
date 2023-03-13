package me.bobthe28th.birthday;

import me.bobthe28th.birthday.commands.BCommands;
import me.bobthe28th.birthday.commands.BTabCompleter;
import me.bobthe28th.birthday.games.GameController;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;

public class Main extends JavaPlugin implements Listener {

    public static Scoreboard board;
    public static HashMap<Player,GamePlayer> GamePlayers;
    public static boolean pvp = true; //todo breakblocks

    public static GameController gameController;

    @Override
    public void onEnable() {
        Bukkit.broadcastMessage("Man");
        getServer().getPluginManager().registerEvents(this, this);

        String[] commandNames = new String[]{"test","pvp","start"};
        BCommands commands = new BCommands(this);
        BTabCompleter tabCompleter = new BTabCompleter();
        for (String commandName : commandNames) {
            PluginCommand command = getCommand(commandName);
            if (command != null) {
                command.setExecutor(commands);
                command.setTabCompleter(tabCompleter);
            }
        }

        if (Bukkit.getScoreboardManager() != null) {
            board = Bukkit.getScoreboardManager().getMainScoreboard();
        }

        for (Team t : board.getTeams()) {
            if (t.getName().startsWith("bday")) {
                t.unregister();
            }
        }

        gameController = new GameController(this);

        GamePlayers = new HashMap<>();

        for(Player player : Bukkit.getOnlinePlayers()) {
            GamePlayers.put(player,new GamePlayer(this,player));
        }
    }

    @Override
    public void onDisable() {
        gameController.disable();
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
        GameController.playerJoin(GamePlayers.get(event.getPlayer()));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(ChatColor.GRAY + "[" + ChatColor.RED + "-" + ChatColor.GRAY + "] " + ChatColor.YELLOW + event.getPlayer().getDisplayName() + " left");
        if (GamePlayers.get(event.getPlayer()) != null) {
            GameController.playerLeave(GamePlayers.get(event.getPlayer()));
            GamePlayers.remove(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        event.setFormat(event.getPlayer().getDisplayName() + ": " + event.getMessage());
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!pvp && event.getCause() != EntityDamageEvent.DamageCause.VOID && event.getEntity() instanceof Player) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onSpawn(SpawnerSpawnEvent event) {
        if (event.getEntityType().equals(EntityType.CHICKEN)) {
            if (event.getEntity().getPassengers().size() > 0) {
                event.setCancelled(true);
            }
        }
    }

//    @EventHandler
//    public void onPlayerAdvancementDone(PlayerAdvancementDoneEvent event) {
//        Player player = event.getPlayer();
//        Advancement advancement = event.getAdvancement();
//        for(String criteria: advancement.getCriteria()) {
//            player.getAdvancementProgress(advancement).revokeCriteria(criteria);
//        }
//    }

}
