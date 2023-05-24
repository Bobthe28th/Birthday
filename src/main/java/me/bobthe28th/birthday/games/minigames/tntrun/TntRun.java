package me.bobthe28th.birthday.games.minigames.tntrun;

import me.bobthe28th.birthday.DamageRule;
import me.bobthe28th.birthday.Main;
import me.bobthe28th.birthday.games.GamePlayer;
import me.bobthe28th.birthday.games.minigames.Minigame;
import me.bobthe28th.birthday.games.minigames.MinigameStatus;
import me.bobthe28th.birthday.games.minigames.bmsts.BmTeam;
import me.bobthe28th.birthday.games.minigames.bmsts.Bmsts;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;

public class TntRun extends Minigame {

    Bmsts bmsts;
    BmTeam winningTeam = null;
    HashMap<Player, TnPlayer> players = new HashMap<>();
    TnMap currentMap;

    boolean tntRun = false;

    public TntRun(Main plugin) {
        super(plugin);
        status = MinigameStatus.READY;
        World w = plugin.getServer().getWorld("world");
//        currentMap =
    }

    @Override
    public void start() {
        Main.damageRule = DamageRule.NONE;
        for (GamePlayer player : plugin.getGamePlayers().values()) {
            players.put(player.getPlayer(),new TnPlayer(plugin,player,this));
        }
        status = MinigameStatus.PLAYING;
        for (TnPlayer player : players.values()) {
            player.getPlayer().teleport(currentMap.getSpawnLoc(new ArrayList<>(players.values())));
        }
        new BukkitRunnable() {
            int time = 5;
            final ChatColor[] timeColors = new ChatColor[]{ChatColor.GREEN, ChatColor.DARK_GREEN, ChatColor.YELLOW, ChatColor.GOLD, ChatColor.RED};

            @Override
            public void run() {
                if (time <= 0) {
                    for (TnPlayer player : players.values()) {
                        player.getPlayer().sendTitle(timeColors[0] + "GO","",0,10,15);
                    }
                    tntRun = true;
                    this.cancel();
                }
                if (!this.isCancelled()) {
                    for (TnPlayer player : players.values()) {
                        player.getPlayer().sendTitle(timeColors[time - 1] + String.valueOf(time),"",0,25,0);
                    }
                    time -= 1;
                }
            }
        }.runTaskTimer(plugin,20,20);
    }

    @Override
    public void disable() {
        currentMap.reset();
        HandlerList.unregisterAll(this);
        if (players != null) {
            for (TnPlayer tnPlayer : players.values()) {
                tnPlayer.remove();
            }
            players.clear();
        }
    }

    @Override
    public void onPlayerJoin(GamePlayer player) {
        players.put(player.getPlayer(),new TnPlayer(plugin, player, this));
        players.get(player.getPlayer()).alive = false;
        player.getPlayer().teleport(currentMap.getSpectateLoc());
    }

    @Override
    public void onPlayerLeave(GamePlayer player) {
        if (players.containsKey(player.getPlayer())) {
            players.get(player.getPlayer()).remove();
            players.remove(player.getPlayer());
        }
    }
}