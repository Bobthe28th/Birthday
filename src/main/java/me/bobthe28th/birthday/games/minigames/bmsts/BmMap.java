package me.bobthe28th.birthday.games.minigames.bmsts;

import org.bukkit.Location;

import java.util.HashMap;

public class BmMap {

    String name;
    Location playerSpawn;
    HashMap<BmTeam, Location> minionSpawn = new HashMap<>();

    public BmMap(String name, Location playerSpawn, HashMap<BmTeam, Location> minionSpawn) {
        this.name = name;
        this.playerSpawn = playerSpawn.clone();
        this.minionSpawn.putAll(minionSpawn);
    }

    public Location getPlayerSpawn() {
        return playerSpawn;
    }

    public HashMap<BmTeam, Location> getMinionSpawn() {
        return minionSpawn;
    }

}
