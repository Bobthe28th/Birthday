package me.bobthe28th.birthday.games.oitc;

import me.bobthe28th.birthday.GamePlayer;
import me.bobthe28th.birthday.Main;
import me.bobthe28th.birthday.games.bmsts.Bmsts;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Random;

public class OiPlayer implements Listener {

    GamePlayer player;
    boolean alive;

    Main plugin;
    ArrayList<Material> blackListedSpawnBlocks = new ArrayList<>();

    public OiPlayer(GamePlayer player, Main plugin) {
        this.player = player;
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        alive = true;
        blackListedSpawnBlocks.add(Material.OAK_STAIRS);
    }

    public void respawn() {
        World w = plugin.getServer().getWorld("world");
        BoundingBox spawnZone = Oitc.currentMap.spawnArea;
        int maxAttempts = 5;

        Random random = new Random();
        for (int i = 0; i < maxAttempts; i++) {
            Location l = getPossibleSpawnLoc(w, spawnZone,true,random);
            if (l != null) {
                player.getPlayer().teleport(l);
                return;
            }
        }
        player.getPlayer().teleport(getPossibleSpawnLoc(w,spawnZone,false,random));
    }

    public Location getPossibleSpawnLoc(World w, BoundingBox spawnZone, boolean checkPlayerDist, Random random) {
        Vector start = new Vector(random.nextInt((int) spawnZone.getWidthX()) + spawnZone.getMinX(), spawnZone.getMaxY(), random.nextInt((int) spawnZone.getWidthZ()) + spawnZone.getMinZ());
        BlockIterator blockIterator = new BlockIterator(w, start, new Vector(0, -1, 0), 0, (int) spawnZone.getHeight());
        while (blockIterator.hasNext()) {
            Block b = blockIterator.next();
            if (b.isEmpty() && !b.getRelative(BlockFace.DOWN).isEmpty() && !blackListedSpawnBlocks.contains(b.getType())) {
                double distToPlayerSqared = Integer.MAX_VALUE;
                for (OiPlayer p : Oitc.OiPlayers.values()) {
                    if (p.isAlive()) {
                        double pDist = b.getLocation().distanceSquared(p.getPlayer().getPlayer().getLocation());
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

    public GamePlayer getPlayer() {
        return player;
    }

    public boolean isAlive() {
        return alive;
    }

    public void remove() {
        removeNotMap();
        Bmsts.BmPlayers.remove(player.getPlayer());
    }

    public void removeNotMap() {
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (event.getPlayer() != player.getPlayer()) return;
        event.setCancelled(true);
    }

}
