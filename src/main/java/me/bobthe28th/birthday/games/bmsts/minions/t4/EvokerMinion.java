package me.bobthe28th.birthday.games.bmsts.minions.t4;

import me.bobthe28th.birthday.Main;
import me.bobthe28th.birthday.games.bmsts.BmTeam;
import me.bobthe28th.birthday.games.bmsts.minions.Minion;
import me.bobthe28th.birthday.games.bmsts.minions.Rarity;
import me.bobthe28th.birthday.games.bmsts.minions.entities.t4.EvokerEntity;

public class EvokerMinion extends Minion {
    public EvokerMinion(Main plugin, BmTeam team, Rarity rarity, Integer strength) {
        super(plugin, "Evoker", EvokerEntity.class, team, 4, rarity, strength, 8);
    }
}
