package me.bobthe28th.birthday.games.minigames.prophunt;

import me.bobthe28th.birthday.games.minigames.MinigameMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

public class PrMap extends MinigameMap {

    ArrayList<Material> props = new ArrayList<>();
    Location propSpawn;
    Location hunterSpawn;

    public PrMap(String title, World world, Location propSpawn, Location hunterSpawn) {
        super(title,world);
        this.propSpawn = propSpawn.clone();
        this.hunterSpawn = hunterSpawn.clone();
    }

    public void setProps(Material[] props) {
        this.props.addAll(List.of(props));
    }

    public Location getHunterSpawn() {
        return hunterSpawn;
    }

    public Location getPropSpawn() {
        return propSpawn;
    }
}
