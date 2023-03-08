package me.bobthe28th.birthday.games.bmsts.minions.t0;

import me.bobthe28th.birthday.Main;
import me.bobthe28th.birthday.games.bmsts.BmTeam;
import me.bobthe28th.birthday.games.bmsts.minions.Minion;
import me.bobthe28th.birthday.games.bmsts.minions.Rarity;
import me.bobthe28th.birthday.games.bmsts.minions.entities.t0.SilverfishEntity;

public class SilverfishMinion extends Minion {

    public SilverfishMinion(Main plugin, BmTeam team, Rarity rarity, Integer strength) {
        super(plugin, "Silverfish", SilverfishEntity.class, team, 0, rarity, strength, 2);
    }
}
