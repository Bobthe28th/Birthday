package me.bobthe28th.birthday.games.minigames.ghosts;

import me.bobthe28th.birthday.Main;
import me.bobthe28th.birthday.games.GamePlayer;
import me.bobthe28th.birthday.games.minigames.Minigame;
import me.bobthe28th.birthday.games.minigames.MinigameMap;
import me.bobthe28th.birthday.games.minigames.MinigameStatus;
import me.bobthe28th.birthday.games.minigames.bmsts.Bmsts;
import me.bobthe28th.birthday.games.minigames.bmsts.bonusrounds.BonusRound;
import me.bobthe28th.birthday.scoreboard.ScoreboardObjective;
import me.bobthe28th.birthday.scoreboard.ScoreboardTeam;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Husk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.BoundingBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class Ghosts extends Minigame implements Listener,BonusRound {

    Main plugin;
    Bmsts bmsts;

    HashMap<Player,GhostPlayer> players = new HashMap<>();

    ArrayList<Husk> ghosts = new ArrayList<>();

    BukkitTask spawnTask;
    BukkitTask timerTask;

    int time = 90;
    MinigameMap map;
    ScoreboardObjective objective;
    ScoreboardTeam team;

    public Ghosts(Main plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        status = MinigameStatus.READY;
        objective = new ScoreboardObjective("test","TEST");
        objective.addRow(1,"Time: " + time,true);
        objective.addRow(0,"TestLocal",false);
        team = new ScoreboardTeam("Ghosts",false,true, Team.OptionStatus.NEVER,ChatColor.WHITE);
        World w = plugin.getServer().getWorld("world");
        map = new MinigameMap("temp",w,new BoundingBox(-261, 98, -382,-228, 91, -349),new Location(w,-245.5, 99, -366.5));
        Main.musicController.getQueue().clearQueue();
        Main.musicController.getQueue().addLoopQueue(Main.musicController.getMusicByName("zombiefun"));
        Main.musicController.start();
    }

    public ScoreboardObjective getObjective() {
        return objective;
    }

    public ScoreboardTeam getTeam() {
        return team;
    }

    @Override
    public void start() {
        status = MinigameStatus.PLAYING;

//            if (isBonusRound) {
//                for (BmPlayer p : bmsts.getPlayers().values()) {
//                    p.getPlayer().teleport(getSpawnLoc(w));
//                    players.put(p.getPlayer(),new GhostPlayer(plugin, p.getGamePlayer(), this));
//                }
//            } else {
        for (GamePlayer p : plugin.getGamePlayers().values()) {
            p.getPlayer().teleport(map.getSpawnLoc(new ArrayList<>(players.values())));
            players.put(p.getPlayer(),new GhostPlayer(plugin, p, this));
        }
//            }

        spawnTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (ghosts.size() < 30 && !this.isCancelled()) {
                    Husk ghost = map.getWorld().spawn(map.getSpawnLoc(new ArrayList<>(players.values())), Husk.class);
                    ghost.setInvulnerable(true);
                    ghost.setInvisible(true);
                    ghost.setAdult();
                    ghost.setSilent(true);
                    if (ghost.getEquipment() != null) ghost.getEquipment().clear();
                    if (ghost.getVehicle() != null) ghost.getVehicle().remove();

                    Objects.requireNonNull(ghost.getAttribute(Attribute.GENERIC_FOLLOW_RANGE)).setBaseValue(5);
                    Objects.requireNonNull(ghost.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)).setBaseValue(10);
                    ghosts.add(ghost);
                    for (GhostPlayer p : players.values()) {
                        team.addMember(ghost,p.getGamePlayer().getScoreboardController());
                    }
                } else {
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin,20,40);

        timerTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (!this.isCancelled()) {
                    for (GhostPlayer p : players.values()) {
                        p.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + String.valueOf(time)));
                        objective.updateRow(1,"Time: " + time);
                        objective.updateRow(0,p.getPlayer().getDisplayName(),p.getGamePlayer());
                    }
                    if (time <= 0) {
                        this.cancel();
                        if (isBonusRound) {
                            endBonusRound(true);
                        }
                        end();
                    }
                    time--;
                }
            }
        }.runTaskTimer(plugin, 0, 20);
    }

    @Override
    public void disable() {
        spawnTask.cancel();
        timerTask.cancel();
        objective.remove();
        for (Husk h : ghosts) {
            h.remove();
        }
    }

    @Override
    public void onPlayerJoin(GamePlayer player) {
        players.put(player.getPlayer(),new GhostPlayer(plugin, player, this));
        players.get(player.getPlayer()).alive = false;
        player.getPlayer().teleport(map.getSpectateLoc());
    }

    @Override
    public void onPlayerLeave(GamePlayer player) {
        players.remove(player.getPlayer());
    }

    @Override
    public void startBonusRound(Bmsts bmsts) {
        this.bmsts = bmsts;
        this.isBonusRound = true;
        start();
    }

    @Override
    public void endBonusRound(boolean points) {
        if (points) {
            for (GhostPlayer p : players.values()) {
                if (p.isAlive()) {
                    bmsts.getPlayers().get(p.getPlayer()).getTeam().addResearchPoints(5);
                }
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (players.containsKey(player)) {
            if (player.getHealth() - event.getFinalDamage() <= 0) {
                players.get(player).alive = false;
                player.teleport(map.getSpectateLoc());
                player.setHealth(20.0);
                event.setCancelled(true);
            }
        }
    }
}
