package me.bobthe28th.birthday.games.oitc;

import me.bobthe28th.birthday.GamePlayer;
import me.bobthe28th.birthday.Main;
import me.bobthe28th.birthday.games.bmsts.Bmsts;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.CrossbowMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.*;

public class OiPlayer implements Listener { //todo remove listener?

    GamePlayer player;
    boolean alive;
    boolean king = false;

    Main plugin;
    ArrayList<Material> blackListedSpawnBlocks = new ArrayList<>();
    Scoreboard scoreboard;
    Objective scores;
    int points = 0;
    int kills = 0;
    int deaths = 0;

    public OiPlayer(GamePlayer player, Main plugin) {
        this.player = player;
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        alive = true;
        blackListedSpawnBlocks.add(Material.OAK_STAIRS);
        if (Bukkit.getScoreboardManager() != null) {
            scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
            scores = scoreboard.registerNewObjective("score", Criteria.DUMMY,"One in the Chamber");
            scores.setDisplaySlot(DisplaySlot.SIDEBAR);
            scores.getScore(ChatColor.GOLD + "" + ChatColor.BOLD + "Your Stats:").setScore(4);
            scores.getScore("Points: " + points).setScore(3);
            scores.getScore("Kills: " + kills).setScore(2);
            scores.getScore("Deaths: " + deaths).setScore(1);
            Team t = scoreboard.registerNewTeam("bdayoitc");
            t.setDisplayName("One in the Chamber");
            t.setAllowFriendlyFire(true);
            t.setCanSeeFriendlyInvisibles(false);
            t.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
            t.addEntry(player.getPlayer().getName());

            Team tBad = scoreboard.registerNewTeam("bdayoitcbad");
            tBad.setDisplayName("KING");
            tBad.setColor(ChatColor.RED);
            tBad.setAllowFriendlyFire(true);
            tBad.setCanSeeFriendlyInvisibles(false);
            tBad.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);

            player.getPlayer().setScoreboard(scoreboard);
        }
    }

    public void updateScoreboard(OiScoreboardRow row) {
        if (scoreboard != null) {
            if (scores != null) {
                for (String e : scoreboard.getEntries()) {
                    if (scores.getScore(e).getScore() != 0) {
                        if (scores.getScore(e).getScore() == row.getRow()) {
                            scoreboard.resetScores(e);
                            break;
                        }
                    }
                }
                switch (row) {
                    case POINTS -> scores.getScore("Points: " + points).setScore(row.getRow());
                    case KILLS -> scores.getScore("Kills: " + kills).setScore(row.getRow());
                    case DEATHS -> scores.getScore("Deaths: " + deaths).setScore(row.getRow());
                }
            }
        }
    }

    public void respawn() {
        Objects.requireNonNull(player.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(1.0);
        player.getPlayer().setHealth(0.1);
        giveItems();
        alive = true;

        World w = plugin.getServer().getWorld("world");
        BoundingBox spawnZone = Oitc.currentMap.spawnArea;
        int maxAttempts = 5;

        Random random = new Random();
        for (int i = 0; i < maxAttempts; i++) {
            Location l = getPossibleSpawnLoc(w, spawnZone,true,random);
            if (l != null) {
                player.getPlayer().teleport(l);
                player.getPlayer().setGameMode(GameMode.ADVENTURE);
                return;
            }
        }
        player.getPlayer().teleport(getPossibleSpawnLoc(w,spawnZone,false,random));
        player.getPlayer().setGameMode(GameMode.ADVENTURE);
    }

    public Location getPossibleSpawnLoc(World w, BoundingBox spawnZone, boolean checkPlayerDist, Random random) {
        Vector start = new Vector(random.nextInt((int) spawnZone.getWidthX()) + spawnZone.getMinX(), spawnZone.getMaxY(), random.nextInt((int) spawnZone.getWidthZ()) + spawnZone.getMinZ());
        BlockIterator blockIterator = new BlockIterator(w, start, new Vector(0, -1, 0), 0, (int) spawnZone.getHeight());
        while (blockIterator.hasNext()) {
            Block b = blockIterator.next();
            if (b.isEmpty() && b.getRelative(BlockFace.DOWN).getType().isSolid() && !blackListedSpawnBlocks.contains(b.getRelative(BlockFace.DOWN).getType())) {
                double distToPlayerSqared = Integer.MAX_VALUE;
                for (OiPlayer p : Oitc.OiPlayers.values()) {
                    if (p.isAlive()) {
                        double pDist = b.getLocation().distanceSquared(p.getPlayer().getLocation());
                        if (distToPlayerSqared > pDist) {
                            distToPlayerSqared = pDist;
                        }
                    }
                }
                if (!checkPlayerDist || distToPlayerSqared >= 25) { //todo longer range
                    return b.getLocation().add(0.5,0,0.5);
                } else {
                    break;
                }
            }
        }
        return null;
    }

    public GamePlayer getGamePlayer() {
        return player;
    }

    public Player getPlayer() {
        return player.getPlayer();
    }

    public boolean isAlive() {
        return alive;
    }

    public void remove() {
        removeNotMap();
        Bmsts.BmPlayers.remove(player.getPlayer());
    }

    public void removeNotMap() {
        Objects.requireNonNull(player.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(20.0);
        player.getPlayer().setHealth(20.0);
        scores.unregister();
        scoreboard.clearSlot(DisplaySlot.SIDEBAR);
        HandlerList.unregisterAll(this);
    }
    
    public void giveItems() {
        PlayerInventory inventory = player.getPlayer().getInventory();
        inventory.clear();
        if (Oitc.cross) {
            ItemStack crossbow = new ItemStack(Material.CROSSBOW);
            CrossbowMeta meta = (CrossbowMeta) crossbow.getItemMeta();
            if (meta != null) {
                meta.setUnbreakable(true);
//                meta.addEnchant(Enchantment.QUICK_CHARGE,3,true);
                meta.addChargedProjectile(new ItemStack(Material.ARROW));
                meta.setLore(List.of("Reloads an arrow on kill"));

            }
            crossbow.setItemMeta(meta);
            inventory.setItem(3,crossbow);
        } else {
            ItemStack bow = new ItemStack(Material.BOW);
            ItemMeta meta = bow.getItemMeta();
            if (meta != null) {
                meta.setUnbreakable(true);
                meta.setLore(List.of("Gains an arrow on kill"));
            }
            bow.setItemMeta(meta);
            inventory.setItem(3,bow);
            inventory.setItem(5,new ItemStack(Material.ARROW));
        }
        player.getPlayer().getInventory().setHeldItemSlot(3);
        player.getPlayer().updateInventory();
    }
    
    public void giveKingItem() {
        PlayerInventory inventory = player.getPlayer().getInventory();
        if (Oitc.cross) {
            ItemStack crossbow = inventory.getItem(3);
            if (crossbow != null && crossbow.getType() == Material.CROSSBOW) {
                CrossbowMeta meta = (CrossbowMeta) crossbow.getItemMeta();
                if (meta != null) {
                    meta.addEnchant(Enchantment.MULTISHOT, 3, true);
                    meta.setChargedProjectiles(Arrays.asList(Oitc.firework.clone(),Oitc.firework.clone(),Oitc.firework.clone()));
                    meta.setLore(List.of("Reloads an arrow on kill"));

                }
                crossbow.setItemMeta(meta);
                inventory.setItem(3, crossbow);
                player.getPlayer().getInventory().setHeldItemSlot(3);
            }
        } else {
            //todo bow king
        }
        
        player.getPlayer().updateInventory();
    }

    public void giveArrow(boolean pickup, int amount) {
        PlayerInventory inventory = player.getPlayer().getInventory();
        ItemStack arrows = inventory.getItem(5);
        if (arrows == null || arrows.getType() != Material.ARROW) {
            inventory.setItem(5,new ItemStack(Material.ARROW));
        } else {
            arrows.setAmount(arrows.getAmount() + amount);
        }
        player.getPlayer().updateInventory();
        if (pickup) {
            player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, SoundCategory.MASTER, 1.0f, 1.0f); //todo better
        }
    }

    public void giveFirework(int amount) {
        PlayerInventory inventory = player.getPlayer().getInventory();
        ItemStack fireworks = inventory.getItemInOffHand();
        if (fireworks.getType() != Material.FIREWORK_ROCKET) {
            inventory.setItem(5,Oitc.firework);
        } else {
            fireworks.setAmount(fireworks.getAmount() + amount);
        }
        player.getPlayer().updateInventory();
    }

    public void kill(Player killed) { //todo top player fun mode
        if (Oitc.cross) {
            PlayerInventory inventory = player.getPlayer().getInventory();
            ItemStack crossbow = inventory.getItem(3);
            if (crossbow != null && crossbow.getType() == Material.CROSSBOW) {
                CrossbowMeta meta = (CrossbowMeta) crossbow.getItemMeta();
                if (meta != null) {
                    if (meta.hasChargedProjectiles()) {
                        if (king) {
                            giveFirework(1);
                        }
                        giveArrow(false,1);
                    } else {
                        if (king) {
                            meta.setChargedProjectiles(Arrays.asList(Oitc.firework.clone(),Oitc.firework.clone(),Oitc.firework.clone()));
                        } else {
                            meta.addChargedProjectile(new ItemStack(Material.ARROW));
                        }
                    }
                }
                crossbow.setItemMeta(meta);
            }
        } else {
            giveArrow(false,1);
        }
        player.getPlayer().sendTitle("",ChatColor.RED + "☠",0,10,10);
        kills ++;
        points ++;
        updateScoreboard(OiScoreboardRow.KILLS);
        updateScoreboard(OiScoreboardRow.POINTS);

        if (points >= Oitc.maxKills && !king) {
            Oitc.setKing(this);
        }
        if (points >= Oitc.maxKills + Oitc.killsPostMax) {
            //todo win
        }
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    public void death(Player killer) {
        alive = false;
        deaths ++;
        updateScoreboard(OiScoreboardRow.DEATHS);
        player.getPlayer().setGameMode(GameMode.SPECTATOR);
        player.getPlayer().getInventory().clear();
        if (king) {
            Oitc.kingDeath(this);
        }
        new BukkitRunnable() {
            int time = 3;
            final ChatColor[] timeColors = new ChatColor[]{ChatColor.GREEN,ChatColor.YELLOW,ChatColor.RED};
            @Override
            public void run() {
                if (time <= 0) {
                    player.getPlayer().sendTitle("",ChatColor.YELLOW + "Respawned!",0,5,5);
                    respawn();
                    this.cancel();
                }
                if (!this.isCancelled()) {
                    player.getPlayer().sendTitle( ChatColor.GRAY + "Respawning in: " + timeColors[time-1] + time, (killer != null ? ChatColor.DARK_GRAY + "Killed by " + ChatColor.RED + killer.getDisplayName() : ""), 0,25,3);
                    time --;
                }
            }
        }.runTaskTimer(plugin,0,20L);
    }

}
