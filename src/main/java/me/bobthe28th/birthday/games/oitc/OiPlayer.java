package me.bobthe28th.birthday.games.oitc;

import me.bobthe28th.birthday.Main;
import me.bobthe28th.birthday.games.GamePlayer;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.CrossbowMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.*;

public class OiPlayer {

    GamePlayer player;
    boolean alive;
    boolean king = false;

    Main plugin;
    Oitc oitc;
    ArrayList<Material> blackListedSpawnBlocks = new ArrayList<>();
    ArrayList<Material> blackListedSpawnInBlocks = new ArrayList<>();
    int points = 0;
    int kills = 0;
    int deaths = 0;

    public OiPlayer(GamePlayer player, Main plugin, Oitc oitc) {
        this.player = player;
        this.plugin = plugin;
        this.oitc = oitc;
        alive = true;
        blackListedSpawnBlocks.add(Material.OAK_STAIRS);
        blackListedSpawnBlocks.add(Material.CACTUS);
        blackListedSpawnBlocks.add(Material.WHITE_WOOL);
        blackListedSpawnBlocks.add(Material.RED_WOOL);
        blackListedSpawnInBlocks.add(Material.LAVA);
        blackListedSpawnInBlocks.add(Material.FIRE);
        blackListedSpawnBlocks.add(Material.SPRUCE_FENCE);
        player.getScoreboardController().addSetObjective(oitc.getObjective());
        player.getScoreboardController().addTeam(oitc.getGTeam());
        player.getScoreboardController().addTeam(oitc.getKTeam());
        oitc.getGTeam().addMemberGlobal(player.getPlayer());
        oitc.updateTopPoints(this);
//        Team t = player.getScoreboardController().addTeam("oitc");
//        t.setDisplayName("One in the Chamber");
//        t.setAllowFriendlyFire(true);
//        t.setCanSeeFriendlyInvisibles(true);
//        t.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
//        t.addEntry(player.getPlayer().getName());
//
//        Team tKing = player.getScoreboardController().addTeam("oitcking");
//        tKing.setDisplayName("KING");
//        tKing.setColor(ChatColor.RED);
//        tKing.setAllowFriendlyFire(true);
//        tKing.setCanSeeFriendlyInvisibles(false);
//        tKing.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
    }

    public void respawn() {
        Objects.requireNonNull(player.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(1.0);
        player.getPlayer().setHealth(0.1);
        giveItems();
        alive = true;

        World w = plugin.getServer().getWorld("world");
        BoundingBox spawnZone = oitc.currentMap.spawnArea;
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
            if (b.isEmpty() && b.getRelative(BlockFace.DOWN).getType().isSolid() && !blackListedSpawnInBlocks.contains(b.getType()) && !blackListedSpawnBlocks.contains(b.getRelative(BlockFace.DOWN).getType())) {
                double distToPlayerSqared = Integer.MAX_VALUE;
                for (OiPlayer p : oitc.OiPlayers.values()) {
                    if (p.isAlive()) {
                        double pDist = b.getLocation().distanceSquared(p.getPlayer().getLocation());
                        if (distToPlayerSqared > pDist) {
                            distToPlayerSqared = pDist;
                        }
                    }
                }
                if (!checkPlayerDist || distToPlayerSqared >= 100) {
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
    }

    public void removeNotMap() {
        Objects.requireNonNull(player.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(20.0);
        player.getPlayer().setHealth(20.0);
        player.getScoreboardController().removeObjective(oitc.getObjective());
        player.getScoreboardController().removeTeam(oitc.getGTeam());
        player.getScoreboardController().removeTeam(oitc.getKTeam());
//        player.getScoreboardController().removeTeam("oitc");
//        player.getScoreboardController().removeTeam("oitcking");
    }
    
    public void giveItems() {
        PlayerInventory inventory = player.getPlayer().getInventory();
        inventory.clear();
        if (oitc.cross) {
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
        if (oitc.cross) {
            ItemStack crossbow = inventory.getItem(3);
            if (crossbow != null && crossbow.getType() == Material.CROSSBOW) {
                CrossbowMeta meta = (CrossbowMeta) crossbow.getItemMeta();
                if (meta != null) {
                    meta.addEnchant(Enchantment.MULTISHOT, 3, true);
                    meta.setChargedProjectiles(Arrays.asList(oitc.firework.clone(),oitc.firework.clone(),oitc.firework.clone()));
                    meta.setLore(List.of("Reloads an arrow on kill"));

                }
                crossbow.setItemMeta(meta);
                inventory.setItem(3, crossbow);
                player.getPlayer().getInventory().setHeldItemSlot(3);
            }
        } else {
            ItemStack crossbow = new ItemStack(Material.CROSSBOW);
            if (crossbow.getType() == Material.CROSSBOW) {
                CrossbowMeta meta = (CrossbowMeta) crossbow.getItemMeta();
                if (meta != null) {
                    meta.addEnchant(Enchantment.MULTISHOT, 3, true);
                    meta.setChargedProjectiles(Arrays.asList(oitc.firework.clone(),oitc.firework.clone(),oitc.firework.clone()));
                    meta.setLore(List.of("Reloads an arrow on kill"));

                }
                crossbow.setItemMeta(meta);
                inventory.setItem(4, crossbow);
                player.getPlayer().getInventory().setHeldItemSlot(4);
            }
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
            player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, SoundCategory.MASTER, 1.0f, 1.0f);
        }
    }

    public void giveFirework(int amount) {
        PlayerInventory inventory = player.getPlayer().getInventory();
        ItemStack fireworks = inventory.getItemInOffHand();
        if (fireworks.getType() != Material.FIREWORK_ROCKET) {
            inventory.setItem(5,oitc.firework);
        } else {
            fireworks.setAmount(fireworks.getAmount() + amount);
        }
        player.getPlayer().updateInventory();
    }

    public void kill(Player killed) {
        if (oitc.cross) {
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
                            meta.setChargedProjectiles(Arrays.asList(oitc.firework.clone(),oitc.firework.clone(),oitc.firework.clone()));
                        } else {
                            meta.addChargedProjectile(new ItemStack(Material.ARROW));
                        }
                    }
                }
                crossbow.setItemMeta(meta);
            }
        } else {
            if (king) {
                giveFirework(1);
            }
            giveArrow(false,1);
        }
        player.getPlayer().sendTitle("",ChatColor.RED + "☠",0,10,10);
        kills ++;
        points ++;
        oitc.updateTopPoints(this);

        oitc.getObjective().updateRow(2,"Kills: " + kills, player);
        oitc.getObjective().updateRow(3, "Points: " + points, player);

        if (points >= oitc.maxKills && !king) {
            oitc.setKing(this);
        }
        if (points >= oitc.maxKills + oitc.killsPostMax) {
            Bukkit.broadcastMessage(player.getPlayer().getDisplayName() + " wins.");
            //todo win
        }
    }

    public void death(Player killer) {
        alive = false;
        deaths ++;
        oitc.getObjective().updateRow(1,"Deaths: " + deaths, player);
        player.getPlayer().setGameMode(GameMode.SPECTATOR);
        player.getPlayer().getInventory().clear();
        if (king) {
            oitc.kingDeath(this);
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
