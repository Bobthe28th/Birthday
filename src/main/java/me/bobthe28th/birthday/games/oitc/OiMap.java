package me.bobthe28th.birthday.games.oitc;

import org.bukkit.util.BoundingBox;

public class OiMap {

    String name;
    BoundingBox spawnArea;

    public OiMap(String name, BoundingBox spawnArea) {
        this.name = name;
        this.spawnArea = spawnArea.clone();
    }

}
