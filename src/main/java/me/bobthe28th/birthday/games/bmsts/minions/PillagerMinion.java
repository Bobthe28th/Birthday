package me.bobthe28th.birthday.games.bmsts.minions;

import me.bobthe28th.birthday.Main;
import me.bobthe28th.birthday.games.bmsts.BmTeam;
import me.bobthe28th.birthday.games.bmsts.minions.entities.PillagerEntity;

public class PillagerMinion extends Minion {
    public PillagerMinion(Main plugin, BmTeam team, Rarity rarity, Integer strength) {
        super(plugin, "Pillager", PillagerEntity.class, team, 2, rarity, strength, 3);
    }
}
