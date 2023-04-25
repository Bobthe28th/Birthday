package me.bobthe28th.birthday.games.bmsts.minions.t1;

import me.bobthe28th.birthday.Main;
import me.bobthe28th.birthday.games.bmsts.BmTeam;
import me.bobthe28th.birthday.games.bmsts.Bmsts;
import me.bobthe28th.birthday.games.bmsts.minions.Minion;
import me.bobthe28th.birthday.games.bmsts.minions.Rarity;
import me.bobthe28th.birthday.games.bmsts.minions.entities.t1.ZombieEntity;

public class ZombieMinion extends Minion {

    public ZombieMinion(Main plugin, Bmsts bmsts, BmTeam team, Rarity rarity, Integer strength) {
        super(plugin,bmsts, "Zombie", ZombieEntity.class, team, 1, rarity, strength, 1);
    }
}
