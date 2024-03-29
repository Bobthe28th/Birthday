package me.bobthe28th.birthday;

import me.bobthe28th.birthday.commands.BCommands;
import me.bobthe28th.birthday.commands.BTabCompleter;
import me.bobthe28th.birthday.games.GameController;
import me.bobthe28th.birthday.games.GamePlayer;
import me.bobthe28th.birthday.music.MusicController;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.advancement.Advancement;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public class Main extends JavaPlugin implements Listener {

    public static DamageRule damageRule = DamageRule.NONE;
    public static boolean breakBlocks = false;

    public static GameController gameController;
    public static MusicController musicController;

    static char[] rainbowChars = "c6ea9b5".toCharArray();
    static int rainbowCharPos = 0;
    public static ChatColor randomColor() {
        rainbowCharPos++;
        if (rainbowCharPos >= rainbowChars.length) {
            rainbowCharPos = 0;
        }
        return ChatColor.getByChar(rainbowChars[rainbowCharPos]);
    }

    public static String rainbow(String s) {
        StringBuilder newString = new StringBuilder();
        for (char c : s.toCharArray()) {
            newString.append(randomColor()).append(c);
        }
        return newString.toString();
    }

    @Override
    public void onEnable() {
        Bukkit.broadcastMessage("Man");

        saveResource("config.yml",true);

        saveDefaultConfig();

        getServer().getPluginManager().registerEvents(this, this);

        String[] commandNames = new String[]{"test","pvp","start","music","join"};
        BCommands commands = new BCommands(this);
        BTabCompleter tabCompleter = new BTabCompleter();
        for (String commandName : commandNames) {
            PluginCommand command = getCommand(commandName);
            if (command != null) {
                command.setExecutor(commands);
                command.setTabCompleter(tabCompleter);
            }
        }
        gameController = new GameController(this);
        musicController = new MusicController(this);

        for(Player player : Bukkit.getOnlinePlayers()) {
            gameController.addNewPlayer(player);
        }
    }

    @Override
    public void onDisable() {
        gameController.disable();
        musicController.disable();
        if (getGamePlayers() != null) {
            for (GamePlayer gamePlayer : getGamePlayers().values()) {
                gamePlayer.removeNotMap();
            }
            getGamePlayers().clear();
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.setJoinMessage(ChatColor.GRAY + "[" + ChatColor.GREEN + "+" + ChatColor.GRAY + "] " + ChatColor.YELLOW + event.getPlayer().getDisplayName() + " joined");
        getGamePlayers().put(event.getPlayer(),new GamePlayer(this,event.getPlayer()));
        gameController.playerJoin(getGamePlayers().get(event.getPlayer()));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(ChatColor.GRAY + "[" + ChatColor.RED + "-" + ChatColor.GRAY + "] " + ChatColor.YELLOW + event.getPlayer().getDisplayName() + " left");
        if (getGamePlayers().get(event.getPlayer()) != null) {
            gameController.playerLeave(getGamePlayers().get(event.getPlayer()));
            getGamePlayers().remove(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (event.getPlayer().getName().equals("Bobthe29th")) {
            event.setFormat(rainbow(event.getPlayer().getDisplayName()) + ChatColor.RESET + ": " + event.getMessage().replace("%","%%"));
        } else {
            event.setFormat(event.getPlayer().getDisplayName() + ": " + event.getMessage().replace("%","%%"));
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (damageRule == DamageRule.NONE && event.getCause() != EntityDamageEvent.DamageCause.VOID && event.getEntity() instanceof Player) {
            event.setCancelled(true);
        } else if (damageRule == DamageRule.NONPLAYER) {
            if (event instanceof EntityDamageByEntityEvent byEntityEvent && byEntityEvent.getDamager() instanceof Player) {
                event.setCancelled(true);
            }
        }
    }

    public HashMap<Player, GamePlayer> getGamePlayers() {
        return gameController.getGamePlayers();
    }

    @EventHandler
    public void onPlayerAdvancementDone(PlayerAdvancementDoneEvent event) {
        Player player = event.getPlayer();
        Advancement advancement = event.getAdvancement();
        if (advancement.getDisplay() != null && advancement.getDisplay().getDescription().startsWith("\ue240")) return;
        for(String criteria: advancement.getCriteria()) {
            player.getAdvancementProgress(advancement).revokeCriteria(criteria);
        }
    }

    @EventHandler
    public void onBreakBlock(BlockBreakEvent event) {
        if (breakBlocks) return;
        if (event.getPlayer().getGameMode() == GameMode.SURVIVAL) {
            event.setCancelled(true);
        }
    }

}
