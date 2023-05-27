package me.bobthe28th.birthday.games.minigames.spleef;

import me.bobthe28th.birthday.DamageRule;
import me.bobthe28th.birthday.Main;
import me.bobthe28th.birthday.games.GamePlayer;
import me.bobthe28th.birthday.games.minigames.MinigameStatus;
import me.bobthe28th.birthday.games.minigames.bmsts.BmPlayer;
import me.bobthe28th.birthday.games.minigames.bmsts.BmTeam;
import me.bobthe28th.birthday.games.minigames.bmsts.bonusrounds.BonusRound;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;

import java.util.ArrayList;
import java.util.HashMap;

public class Spleef extends BonusRound {
    BmTeam winningTeam = null;
    HashMap<Player, SpPlayer> players = new HashMap<>();
    SpMap currentMap;
    boolean canBreak = false;
    public Spleef(Main plugin) {
        super(plugin);
        status = MinigameStatus.READY;
        World w = plugin.getServer().getWorld("world");
        currentMap = new SpMap("temp",w,new BoundingBox(-35,128,-292,-29,127,-286),new Location(w,-32, 130 ,-289),new SpLayer(-35, -292, 123, -30, -287, 3, 2),121);
    }

    @Override
    public void start() {
        Main.damageRule = DamageRule.NONE;
        Main.breakBlocks = true;
        for (GamePlayer player : plugin.getGamePlayers().values()) {
            players.put(player.getPlayer(),new SpPlayer(plugin,player,this));
        }
        status = MinigameStatus.PLAYING;
        for (SpPlayer player : players.values()) {
            player.getPlayer().teleport(currentMap.getSpawnLoc(new ArrayList<>(players.values())));
            player.giveItems();
        }
        new BukkitRunnable() {
            int time = 5;
            final ChatColor[] timeColors = new ChatColor[]{ChatColor.GREEN, ChatColor.DARK_GREEN, ChatColor.YELLOW, ChatColor.GOLD, ChatColor.RED};

            @Override
            public void run() {
                if (time <= 0) {
                    for (SpPlayer player : players.values()) {
                        player.getPlayer().sendTitle(timeColors[0] + "GO","",0,10,15);
                    }
                    canBreak = true;
                    this.cancel();
                }
                if (!this.isCancelled()) {
                    for (SpPlayer player : players.values()) {
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
            for (SpPlayer spPlayer : players.values()) {
                spPlayer.remove();
            }
            players.clear();
        }
    }

    @Override
    public void onPlayerJoin(GamePlayer player) {
        players.put(player.getPlayer(),new SpPlayer(plugin, player, this));
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

    @Override
    public void awardPoints() {
        if (winningTeam != null) {
            for (BmPlayer p : winningTeam.getMembers()) {
                if (players.get(p.getPlayer()).isAlive()) {
                    Main.gameController.giveAdvancement(p.getPlayer(),"spleef/spleefwin");
                    winningTeam.addResearchPoints(5,true);
                }
            }
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (!(event.getEntity().getShooter() instanceof Player player)) return;
        if (!(event.getEntity() instanceof Snowball snowball)) return;
        if (players.containsKey(player)) {
            if (event.getHitBlock() != null && event.getHitBlock().getType() == Material.SNOW_BLOCK) {
                players.get(player).breakBlock(true);
                snowball.remove();
                event.getHitBlock().getWorld().spawnParticle(Particle.BLOCK_DUST, event.getHitBlock().getLocation().add(0.5,0.5,0.5), 50, 0.35, 0.35, 0.35, event.getHitBlock().getBlockData());
                event.getHitBlock().setType(Material.AIR);
            }
        }
    }

    @EventHandler
    public void onBreakBlock(BlockBreakEvent event) {
        if (players.containsKey(event.getPlayer())) {
            if (!canBreak) {
                event.setCancelled(true);
                return;
            }
            if (event.getBlock().getType() == Material.SNOW_BLOCK) {
                players.get(event.getPlayer()).breakBlock(false);
            }
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (players.containsKey(event.getPlayer())) event.setCancelled(true);
    }

//    @EventHandler
//    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
//        if (players.containsKey(event.getPlayer())) event.setCancelled(true);
//    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player player && event.getClickedInventory() != null) {
            if (!players.containsKey(player)) return;
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!players.containsKey(event.getPlayer())) return;
        if (players.get(event.getPlayer()).isAlive() && event.getTo() != null && event.getTo().getY() <= currentMap.yDeath) {
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
                SpPlayer lastAlive = null;
                for (SpPlayer p : players.values()) {
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
        }
    }

}
