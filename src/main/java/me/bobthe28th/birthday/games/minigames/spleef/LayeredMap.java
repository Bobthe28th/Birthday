package me.bobthe28th.birthday.games.minigames.spleef;

import me.bobthe28th.birthday.games.minigames.MinigameMap;
import org.bukkit.Location;
import org.bukkit.World;

public class LayeredMap extends MinigameMap {

    Layer layers;
    public int yDeath;

    public LayeredMap(String title, World w, Location spawnLoc, Location spectateLoc, Layer layers, int yDeath) {
        super(title,w,spawnLoc,spectateLoc);
        this.layers = layers;
        this.yDeath = yDeath;
        reset();
    }

    public void reset() {
        layers.reset(this);
    }
}
