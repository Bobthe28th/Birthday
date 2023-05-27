package me.bobthe28th.birthday.games.minigames.tntrun;

import me.bobthe28th.birthday.games.minigames.MinigameMap;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.BoundingBox;

public class TnMap extends MinigameMap {

    TnLayer tntLayers;
    int yDeath;

    public TnMap(String title, World world, BoundingBox spawnArea, Location spectateLoc, TnLayer tntLayers, int yDeath) {
        super(title, world, spawnArea, spectateLoc);
        this.tntLayers = tntLayers;
        this.yDeath = yDeath;
        reset();
    }

    public void reset() {
        tntLayers.reset(this);
    }
}
