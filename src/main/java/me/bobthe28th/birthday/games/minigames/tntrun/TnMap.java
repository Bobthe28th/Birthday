package me.bobthe28th.birthday.games.minigames.tntrun;

import me.bobthe28th.birthday.games.minigames.MinigameMap;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.BoundingBox;

public class TnMap extends MinigameMap {
    public TnMap(String title, World world, BoundingBox spawnArea, Location spectateLoc) {
        super(title, world, spawnArea, spectateLoc);
    }

    public void reset() {
    }
}
