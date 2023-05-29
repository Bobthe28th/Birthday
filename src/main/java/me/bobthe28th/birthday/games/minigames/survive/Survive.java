package me.bobthe28th.birthday.games.minigames.survive;

import me.bobthe28th.birthday.DamageRule;
import me.bobthe28th.birthday.Main;
import me.bobthe28th.birthday.games.GamePlayer;
import me.bobthe28th.birthday.games.minigames.MinigameMap;
import me.bobthe28th.birthday.games.minigames.MinigameStatus;
import me.bobthe28th.birthday.games.minigames.bmsts.bonusrounds.BonusRound;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Ravager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BoundingBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Survive extends BonusRound {

    HashMap<Player, SuPlayer> players = new HashMap<>();
    ArrayList<Ravager> ravagers = new ArrayList<>();
    BukkitTask spawnTask;
    BukkitTask timerTask;

    int time = 90;
    MinigameMap map;

    public Survive(Main plugin) {
        super(plugin);
        World w = plugin.getServer().getWorld("world");
        map = new MinigameMap("garg",w,new BoundingBox(-182, 106, -306,-215, 98, -339),new Location(w,-199, 116, -322));
        map.addBlackListedSpawnOnBlocks(new Material[]{Material.BARRIER,Material.DEEPSLATE});
    }

    @Override
    public void start() {
        status = MinigameStatus.PLAYING;
        Main.damageRule = DamageRule.NONPLAYER;
        Main.breakBlocks = false;
        for (GamePlayer p : plugin.getGamePlayers().values()) {
            p.getPlayer().teleport(map.getSpawnLoc(new ArrayList<>(players.values())));
            players.put(p.getPlayer(),new SuPlayer(plugin, p, this));
        }
        spawnTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (ravagers.size() < 10 && !this.isCancelled()) {
                    ravagers.add((Ravager) map.getWorld().spawnEntity(new Location(map.getWorld(), -199, 95, -322), EntityType.RAVAGER));
                } else {
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin,80,80);
        timerTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (!this.isCancelled()) {
                    for (SuPlayer p : players.values()) {
                        p.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.LIGHT_PURPLE + "" + time));
                    }
                    if (time <= 0) {
                        this.cancel();
                        if (isBonusRound) {
                            endBonusRound(true);
                        } else {
                            List<GamePlayer> living = new ArrayList<>();
                            for (SuPlayer p : players.values()) {
                                if (p.isAlive()) {
                                    living.add(p.getGamePlayer());
                                }
                            }
                            end(living);
                        }
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
        for (Ravager h : ravagers) {
            h.remove();
        }
        players.clear();
    }

    @Override
    public void onPlayerJoin(GamePlayer player) {
        players.put(player.getPlayer(),new SuPlayer(plugin, player, this));
        players.get(player.getPlayer()).alive = false;
        player.getPlayer().teleport(map.getSpectateLoc());
    }

    @Override
    public void onPlayerLeave(GamePlayer player) {
        players.remove(player.getPlayer());
    }

    @Override
    public void awardPoints() {
        for (SuPlayer p : players.values()) {
            if (p.isAlive()) {
                bmsts.getPlayers().get(p.getPlayer()).getTeam().addResearchPoints(5,true);
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (players.containsKey(player)) {
            if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                event.setCancelled(true);
            } else if (player.getHealth() - event.getFinalDamage() <= 0) {
                players.get(player).alive = false;
                player.setHealth(20.0);
                org.bukkit.ChatColor teamColor = org.bukkit.ChatColor.RED;
                if (isBonusRound) {
                    teamColor = bmsts.getTeamColor(player, org.bukkit.ChatColor.RED);
                }
                Bukkit.broadcastMessage(org.bukkit.ChatColor.GRAY + "[" + teamColor + "☠" + org.bukkit.ChatColor.GRAY + "] " + teamColor + player.getDisplayName() + org.bukkit.ChatColor.GRAY + " died");
                boolean allDead = true;
                for (SuPlayer p : players.values()) {
                    if (p.isAlive()) {
                        allDead = false;
                        break;
                    }
                }
                if (allDead) {
                    if (isBonusRound) {
                        endBonusRound(true);
                    } else {
                        List<GamePlayer> living = new ArrayList<>();
                        for (SuPlayer p : players.values()) {
                            if (p.isAlive()) {
                                living.add(p.getGamePlayer());
                            }
                        }
                        end(living);
                    }
                } else {
                    player.setGameMode(GameMode.SPECTATOR);
                    player.teleport(map.getSpectateLoc());
                }
                event.setCancelled(true);
            }
        }
    }
}
