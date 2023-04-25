package me.bobthe28th.birthday.games.bmsts.bonusrounds;

import org.bukkit.Location;

public class BonusRoundMap {

    Location playerRespawn;

    public BonusRoundMap(Location playerRespawn) {
        this.playerRespawn = playerRespawn;
    }

    public Location getPlayerRespawn() {
        return playerRespawn;
    }
}
