package me.bobthe28th.birthday.games.minigames.spleef;

import me.bobthe28th.birthday.games.minigames.MinigameMap;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.BoundingBox;

public class SpMap extends MinigameMap {

    SpLayer snowLayers;
    int yDeath;

    public SpMap(String title, World w, BoundingBox spawnArea, Location spectateLoc, SpLayer snowLayers, int yDeath) {
        super(title,w,spawnArea,spectateLoc);
        this.snowLayers = snowLayers;
        this.yDeath = yDeath;
        reset();
    }

    public void reset() {
        snowLayers.reset(this);
    }

}
