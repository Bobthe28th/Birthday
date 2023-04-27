package me.bobthe28th.birthday.games.minigames.oitc;

import me.bobthe28th.birthday.games.minigames.MinigameMap;
import org.bukkit.World;
import org.bukkit.util.BoundingBox;

public class OiMap extends MinigameMap {

    BoundingBox spawnArea;

    public OiMap(String title, World w, BoundingBox spawnArea) {
        super(title,w,spawnArea);
        this.spawnArea = spawnArea.clone();
    }

}
