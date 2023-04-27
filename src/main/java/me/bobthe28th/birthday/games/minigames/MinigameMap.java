package me.bobthe28th.birthday.games.minigames;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MinigameMap {

    String title;
    World world;

    BoundingBox spawnArea;
    Location spectateLoc;

    ArrayList<Material> blackListedSpawnOnBlocks = new ArrayList<>();
    ArrayList<Material> blackListedSpawnInBlocks = new ArrayList<>();

    public MinigameMap(String title) {
        this.title = title;
    }

    public MinigameMap(String title, World world) {
        this.title = title;
        this.world = world;
    }
    public MinigameMap(String title, World world, BoundingBox spawnArea) {
        this.title = title;
        this.world = world;
        this.spawnArea = spawnArea.clone();
    }

    public MinigameMap(String title, World world, BoundingBox spawnArea, Location spectateLoc) {
        this.title = title;
        this.world = world;
        this.spawnArea = spawnArea.clone();
        this.spectateLoc = spectateLoc.clone();
    }

    public void setBlackListedSpawnOnBlocks(Material[] blocks) {
        blackListedSpawnOnBlocks.addAll(List.of(blocks));
    }

    public void setBlackListedSpawnInBlocks(Material[] blocks) {
        blackListedSpawnInBlocks.addAll(List.of(blocks));
    }

    public String getTitle() {
        return title;
    }

    public World getWorld() {
        return world;
    }

    public Location getSpectateLoc() {
        return spectateLoc;
    }

    public Location getSpawnLoc(List<MinigamePlayer> players) {
        int maxAttempts = 10;

        Random random = new Random();
        for (int i = 0; i < maxAttempts; i++) {
            Location l = getPossibleSpawnLoc(players,random);
            if (l != null) {
                return l;
            }
        }
        return getPossibleSpawnLoc(random);
    }

    public Location getPossibleSpawnLoc(Random random) {
        Vector start = new Vector(random.nextInt((int) spawnArea.getWidthX()) + spawnArea.getMinX(), spawnArea.getMaxY(), random.nextInt((int) spawnArea.getWidthZ()) + spawnArea.getMinZ());
        BlockIterator blockIterator = new BlockIterator(world, start, new Vector(0, -1, 0), 0, (int) spawnArea.getHeight());
        while (blockIterator.hasNext()) {
            Block b = blockIterator.next();
            if (b.isEmpty() && b.getRelative(BlockFace.DOWN).getType().isSolid() && !blackListedSpawnInBlocks.contains(b.getType()) && !blackListedSpawnOnBlocks.contains(b.getRelative(BlockFace.DOWN).getType())) {
                return b.getLocation().add(0.5,0,0.5);
            }
        }
        return null;
    }

    public Location getPossibleSpawnLoc(List<MinigamePlayer> players,  Random random) {
        Vector start = new Vector(random.nextInt((int) spawnArea.getWidthX()) + spawnArea.getMinX(), spawnArea.getMaxY(), random.nextInt((int) spawnArea.getWidthZ()) + spawnArea.getMinZ());
        BlockIterator blockIterator = new BlockIterator(world, start, new Vector(0, -1, 0), 0, (int) spawnArea.getHeight());
        while (blockIterator.hasNext()) {
            Block b = blockIterator.next();
            if (b.isEmpty() && b.getRelative(BlockFace.DOWN).getType().isSolid() && !blackListedSpawnInBlocks.contains(b.getType()) && !blackListedSpawnOnBlocks.contains(b.getRelative(BlockFace.DOWN).getType())) {
                double distToPlayerSqared = Integer.MAX_VALUE;
                for (MinigamePlayer p : players) {
                    if (p.isAlive()) {
                        double pDist = b.getLocation().distanceSquared(p.getPlayer().getLocation());
                        if (distToPlayerSqared > pDist) {
                            distToPlayerSqared = pDist;
                        }
                    }
                }
                if (distToPlayerSqared >= 100) {
                    return b.getLocation().add(0.5,0,0.5);
                } else {
                    break;
                }
            }
        }
        return null;
    }

}
