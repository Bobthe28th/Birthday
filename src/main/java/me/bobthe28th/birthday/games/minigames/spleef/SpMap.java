package me.bobthe28th.birthday.games.minigames.spleef;

import me.bobthe28th.birthday.games.minigames.MinigameMap;
import org.bukkit.World;
import org.bukkit.util.BoundingBox;

public class SpMap extends MinigameMap {

    SpLayers snowLayers;
    int yDeath;

    public SpMap(String title, World w, BoundingBox spawnArea, SpLayers snowLayers, int yDeath) {
        super(title,w,spawnArea);
        this.snowLayers = snowLayers;
        this.yDeath = yDeath;
        reset();
    }

    public void reset() {
        snowLayers.reset(this);
    }

}
