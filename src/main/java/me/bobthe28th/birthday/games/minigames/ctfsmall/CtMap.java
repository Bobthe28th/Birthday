package me.bobthe28th.birthday.games.minigames.ctfsmall;

import me.bobthe28th.birthday.games.minigames.MinigameMap;
import org.bukkit.Location;
import org.bukkit.World;

public class CtMap extends MinigameMap {

    Location[] teamSpawns;

    public CtMap(String title, World world, Location spectateLoc, Location[] teamSpawns) {
        super(title, world);
        this.spectateLoc = spectateLoc.clone();
        this.teamSpawns = teamSpawns;
    }

    public Location getTeamSpawnLoc(int t) {
        return teamSpawns[t];
    }
}
