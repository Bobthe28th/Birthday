package me.bobthe28th.birthday.games.minigames.spleef;

import me.bobthe28th.birthday.games.minigames.MinigameMap;
import org.bukkit.World;
import org.bukkit.util.BoundingBox;

public class SpMap extends MinigameMap {

    SpLayers snowLayers;

    public SpMap(String title, World w, BoundingBox spawnArea, SpLayers snowLayers) {
        super(title,w,spawnArea);
        this.snowLayers = snowLayers;
        reset();
    }

    public void reset() {
        snowLayers.reset(this);
    }

}
