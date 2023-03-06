package me.bobthe28th.birthday.games.bmsts.minions;

import me.bobthe28th.birthday.Main;
import me.bobthe28th.birthday.games.bmsts.BmTeam;
import me.bobthe28th.birthday.games.bmsts.minions.entities.ChickenEntity;

public class ChickenMinion extends Minion {

    public ChickenMinion(Main plugin, BmTeam team, Rarity rarity, Integer strength) {
        super(plugin, "Chicken", ChickenEntity.class, team, 0, rarity, strength, 5);
    }
}