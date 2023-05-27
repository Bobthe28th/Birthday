package me.bobthe28th.birthday.games.minigames.ghosts;

import me.bobthe28th.birthday.DamageRule;
import me.bobthe28th.birthday.Main;
import me.bobthe28th.birthday.games.GamePlayer;
import me.bobthe28th.birthday.games.minigames.MinigameMap;
import me.bobthe28th.birthday.games.minigames.MinigameStatus;
import me.bobthe28th.birthday.games.minigames.bmsts.BmPlayer;
import me.bobthe28th.birthday.games.minigames.bmsts.BmTeam;
import me.bobthe28th.birthday.games.minigames.bmsts.bonusrounds.BonusRound;
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
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.BoundingBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class Ghosts extends BonusRound {

    HashMap<Player, GhPlayer> players = new HashMap<>();

    ArrayList<Husk> ghosts = new ArrayList<>();

    BukkitTask spawnTask;
    BukkitTask timerTask;

    int time = 90;
    MinigameMap map;
    ScoreboardTeam team;

    public Ghosts(Main plugin) {
        super(plugin);
        status = MinigameStatus.READY;
        team = new ScoreboardTeam("Ghosts",false,true, Team.OptionStatus.NEVER,ChatColor.WHITE);
        World w = plugin.getServer().getWorld("world");
        map = new MinigameMap("temp",w,new BoundingBox(-261, 98, -382,-228, 91, -349),new Location(w,-244.5, 99, -365.5));
    }

    public ScoreboardTeam getTeam() {
        return team;
    }

    @Override
    public void start() {
        status = MinigameStatus.PLAYING;
        Main.musicController.clearAndPlayLoop(Main.musicController.getMusicByName("zombiefun"));
        Main.damageRule = DamageRule.NONPLAYER;
        Main.breakBlocks = false;

        for (GamePlayer p : plugin.getGamePlayers().values()) {
            p.getPlayer().teleport(map.getSpawnLoc(new ArrayList<>(players.values())));
            players.put(p.getPlayer(),new GhPlayer(plugin, p, this));
        }

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

                    Objects.requireNonNull(ghost.getAttribute(Attribute.GENERIC_FOLLOW_RANGE)).setBaseValue(15);
                    Objects.requireNonNull(ghost.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)).setBaseValue(10);
                    ghosts.add(ghost);
                    if (isBonusRound) {
                        for (BmPlayer p : bmsts.getPlayers().values()) {
                            p.getTeam().getTeam().addMember(ghost,p.getGamePlayer().getScoreboardController());
                        }
                    } else {
                        team.addMemberGlobal(ghost);
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
                    for (GhPlayer p : players.values()) {
                        p.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + String.valueOf(time)));
                    }
                    if (time <= 0) {
                        this.cancel();
                        if (isBonusRound) {
                            endBonusRound(true);
                        } else {
                            end(); //todos winners
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
        for (Husk h : ghosts) {
            if (isBonusRound) {
                for (BmTeam bmTeam : bmsts.getTeams().values()) {
                    bmTeam.getTeam().removeMemberGlobal(h);
                }
            }
            h.remove();
        }
        players.clear();
    }

    @Override
    public void onPlayerJoin(GamePlayer player) {
        players.put(player.getPlayer(),new GhPlayer(plugin, player, this));
        players.get(player.getPlayer()).alive = false;
        player.getPlayer().teleport(map.getSpectateLoc());
    }

    @Override
    public void onPlayerLeave(GamePlayer player) {
        players.remove(player.getPlayer());
    }

    @Override
    public void awardPoints() {
        for (GhPlayer p : players.values()) {
            if (p.isAlive()) {
                Main.gameController.giveAdvancement(p.getPlayer(),"ghosts/ghostwin");
                if (bmsts.getPlayers().get(p.getPlayer()).getTeam() != null) {
                    bmsts.getPlayers().get(p.getPlayer()).getTeam().addResearchPoints(5,true);
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
                player.setHealth(20.0);
                boolean allDead = true;
                for (GhPlayer p : players.values()) {
                    if (p.isAlive()) {
                        allDead = false;
                        break;
                    }
                }
                if (allDead) {
                    if (isBonusRound) {
                        endBonusRound(true);
                    } else {
                        end();
                    }
                } else {
                    player.teleport(map.getSpectateLoc());
                }
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityPotionEffect(EntityPotionEffectEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (players.containsKey(player)) {
            if (event.getModifiedType().equals(PotionEffectType.HUNGER) && event.getCause().equals(EntityPotionEffectEvent.Cause.ATTACK)) {
                event.setCancelled(true);
            }
        }
    }
}
