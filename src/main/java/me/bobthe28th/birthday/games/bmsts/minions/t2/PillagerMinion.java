package me.bobthe28th.birthday.games.bmsts.minions.t2;

import me.bobthe28th.birthday.Main;
import me.bobthe28th.birthday.games.bmsts.BmTeam;
import me.bobthe28th.birthday.games.bmsts.minions.Minion;
import me.bobthe28th.birthday.games.bmsts.minions.Rarity;
import me.bobthe28th.birthday.games.bmsts.minions.entities.t2.PillagerEntity;

public class PillagerMinion extends Minion {
    public PillagerMinion(Main plugin, BmTeam team, Rarity rarity, Integer strength) {
        super(plugin, "Pillager", PillagerEntity.class, team, 2, rarity, strength, 3);
    }
}
