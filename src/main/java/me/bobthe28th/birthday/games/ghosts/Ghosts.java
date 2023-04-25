package me.bobthe28th.birthday.games.ghosts;

import me.bobthe28th.birthday.games.GamePlayer;
import me.bobthe28th.birthday.Main;
import me.bobthe28th.birthday.games.GameStatus;
import me.bobthe28th.birthday.games.Minigame;
import me.bobthe28th.birthday.games.bmsts.BmPlayer;
import me.bobthe28th.birthday.games.bmsts.Bmsts;
import me.bobthe28th.birthday.games.bmsts.bonusrounds.BonusRound;
import me.bobthe28th.birthday.scoreboard.ScoreboardObjective;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Husk;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

public class Ghosts extends Minigame implements BonusRound {

    Main plugin;
    Bmsts bmsts;

    HashMap<Player,GhostPlayer> players = new HashMap<>();

    ArrayList<Husk> ghosts = new ArrayList<>();

    BukkitTask spawnTask;
    BukkitTask timerTask;

    int time = 90;
    BoundingBox spawnZone;
    ScoreboardObjective objective;

    public Ghosts(Main plugin) {
        this.plugin = plugin;
        spawnZone = new BoundingBox(-261, 98, -382,-228, 91, -349);
        status = GameStatus.READY;
        objective = new ScoreboardObjective("test","TEST");
        objective.addRow(1,"Time: " + time,true);
        objective.addRow(0,"TestLocal",false);
    }

    public Location getSpawnLoc(World w) {
        int maxAttempts = 20;

        Random random = new Random();
        for (int i = 0; i < maxAttempts; i++) {
            Location l = getPossibleSpawnLoc(w,true,random);
            if (l != null) {
                return l;
            }
        }
        return getPossibleSpawnLoc(w,false,random);
    }

    public Location getPossibleSpawnLoc(World w, boolean checkPlayerDist, Random random) { //todo move to util class

        Vector start = new Vector(random.nextInt((int) spawnZone.getWidthX()) + spawnZone.getMinX(), spawnZone.getMaxY(), random.nextInt((int) spawnZone.getWidthZ()) + spawnZone.getMinZ());

        BlockIterator blockIterator = new BlockIterator(w, start, new Vector(0, -1, 0), 0, (int) spawnZone.getHeight());
        while (blockIterator.hasNext()) {
            Block b = blockIterator.next();
            if (b.isEmpty() && !b.getRelative(BlockFace.DOWN).isEmpty()) {
                double distToPlayerSqared = Integer.MAX_VALUE;
                for (GhostPlayer p : players.values()) {
                    if (p.isAlive()) {
                        double pDist = b.getLocation().distanceSquared(p.getPlayer().getLocation());
                        if (distToPlayerSqared > pDist) {
                            distToPlayerSqared = pDist;
                        }
                    }
                }
                if (!checkPlayerDist || distToPlayerSqared >= 25) {
                    return b.getLocation().add(0.5,0,0.5);
                } else {
                    break;
                }
            }
        }
        return null;
    }

    @Override
    public void start() {
        World w = plugin.getServer().getWorld("world");
        if (w != null) {
            status = GameStatus.PLAYING;

            if (isBonusRound) {
                for (BmPlayer p : bmsts.getPlayers().values()) {
                    p.getPlayer().teleport(getSpawnLoc(w));
                    players.put(p.getPlayer(),new GhostPlayer(p.getGamePlayer(),objective));
                }
            } else {
                for (GamePlayer p : plugin.getGamePlayers().values()) {
                    p.getPlayer().teleport(getSpawnLoc(w));
                    players.put(p.getPlayer(),new GhostPlayer(p,objective));
                }
            }

            spawnTask = new BukkitRunnable() {
                @Override
                public void run() {
                    if (ghosts.size() < 30 && !this.isCancelled()) {
                        Husk ghost = w.spawn(getSpawnLoc(w), Husk.class);
                        ghost.setInvulnerable(true);
                        ghost.setInvisible(true);
                        ghost.setAdult();
                        ghost.setSilent(true);
                        if (ghost.getEquipment() != null) ghost.getEquipment().clear();
                        if (ghost.getVehicle() != null) ghost.getVehicle().remove();

                        Objects.requireNonNull(ghost.getAttribute(Attribute.GENERIC_FOLLOW_RANGE)).setBaseValue(5);
                        Objects.requireNonNull(ghost.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)).setBaseValue(10);
                        ghosts.add(ghost);
                        //updateTeam(ghost);
                        //todos update team
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
        } else {
            status = GameStatus.END;
        }
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

    }

    @Override
    public void onPlayerLeave(GamePlayer player) {

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
}
