package me.bobthe28th.birthday.games.minigames.tntrun;

import org.bukkit.Material;
import org.bukkit.block.Block;

public class TnLayer {

    int minX;
    int minY;
    int minZ;
    int maxX;
    int maxZ;
    int yOffset;
    int amount;

    public TnLayer(int minX, int minZ, int minY, int maxX, int maxZ, int yOffset, int amount) {
        this.minX = Math.min(minX,maxX);
        this.minZ = Math.min(minZ,maxZ);
        this.minY = minY;
        this.maxX = Math.max(minX,maxX);
        this.maxZ = Math.max(minZ,maxZ);
        this.yOffset = yOffset;
        this.amount = amount;
    }

    public void reset(TnMap map) {
        for (int n = 0; n < amount; n ++) {
            int y = minY + yOffset*n;
            for (int z = minZ; z <= maxZ; z ++) {
                for (int x = minX; x <= maxX; x++) {
                    Block b = map.getWorld().getBlockAt(x,y,z);
                    if (b.getType().isAir() || b.getType().equals(Material.SNOW_BLOCK)) {
                        b.setType(Material.TNT);
                    }
                }
            }
        }
    }
}
