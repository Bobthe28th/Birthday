package me.bobthe28th.birthday.games.bmsts.minions;

import me.bobthe28th.birthday.Main;
import me.bobthe28th.birthday.games.bmsts.BmTeam;
import me.bobthe28th.birthday.games.bmsts.minions.entities.ZombieEntity;

public class ZombieMinion extends Minion {

    public ZombieMinion(Main plugin, BmTeam team, Rarity rarity, Integer strength) {
        super(plugin, "Zombie", ZombieEntity.class, team, 1, rarity, strength, 1);
    }
}
