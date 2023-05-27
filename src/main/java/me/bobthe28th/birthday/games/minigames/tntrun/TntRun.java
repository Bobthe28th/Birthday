package me.bobthe28th.birthday.games.minigames.tntrun;

import me.bobthe28th.birthday.DamageRule;
import me.bobthe28th.birthday.Main;
import me.bobthe28th.birthday.games.GamePlayer;
import me.bobthe28th.birthday.games.minigames.MinigameStatus;
import me.bobthe28th.birthday.games.minigames.bmsts.BmPlayer;
import me.bobthe28th.birthday.games.minigames.bmsts.BmTeam;
import me.bobthe28th.birthday.games.minigames.bmsts.bonusrounds.BonusRound;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BoundingBox;

import java.util.ArrayList;
import java.util.HashMap;

public class TntRun extends BonusRound {

    BmTeam winningTeam = null;
    HashMap<Player, TnPlayer> players = new HashMap<>();
    TnMap currentMap;
    HashMap<Block, BukkitTask> toRemove = new HashMap<>();

    boolean tntRun = false;

    public TntRun(Main plugin) {
        super(plugin);
        status = MinigameStatus.READY;
        World w = plugin.getServer().getWorld("world");
        currentMap = new TnMap("temp",w,new BoundingBox(-35,128,-292,-29,127,-286),new Location(w,-32, 130 ,-289),new TnLayer(-35, -292, 123, -30, -287, 3, 2),121);
    }

    @Override
    public void awardPoints() {

    }

    @Override
    public void start() {
        Main.damageRule = DamageRule.NONE;
        Main.breakBlocks = false;
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
                    tntRun = true;
                    for (TnPlayer player : players.values()) {
                        player.getPlayer().sendTitle(timeColors[0] + "GO","",0,10,15);
                        checkBlock(player.getPlayer());
                    }
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
        for (BukkitTask b : toRemove.values()) {
            b.cancel();
        }
        toRemove.clear();
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

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!players.containsKey(event.getPlayer())) return;
        if ((event.getTo() == null) || (event.getTo() != null && event.getTo().toVector().equals(event.getFrom().toVector()))) return;
        if (players.get(event.getPlayer()).isAlive() && event.getTo().getY() <= currentMap.yDeath) {
            players.get(event.getPlayer()).death();
            if (isBonusRound) {
                for (BmTeam t : bmsts.getTeams().values()) {
                    for (BmPlayer p : t.getMembers()) {
                        if (players.get(p.getPlayer()).isAlive()) {
                            if (winningTeam == null || winningTeam == t) {
                                winningTeam = t;
                            } else {
                                return;
                            }
                        }
                    }
                }
                endBonusRound(true);
            } else {
                TnPlayer lastAlive = null;
                for (TnPlayer p : players.values()) {
                    if (p.isAlive()) {
                        if (lastAlive == null) {
                            lastAlive = p;
                        } else {
                            return;
                        }
                    }
                }
                end(lastAlive); //todo top 3?
            }
        } else if (tntRun) {
            checkBlock(event.getPlayer());
        }
    }

    public void checkBlock(Player p) {
        Block b = p.getLocation().clone().add(0,-0.1,0).getBlock();
        if (b.getType().equals(Material.TNT)) {
            if (!toRemove.containsKey(b)) {
                toRemove.put(b, new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (!this.isCancelled()) {
                            b.setType(Material.AIR);
                            toRemove.remove(b);
                        }
                    }
                }.runTaskLater(plugin,20));
            }
        }
    }
}