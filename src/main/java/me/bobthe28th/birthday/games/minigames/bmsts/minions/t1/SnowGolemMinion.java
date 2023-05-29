package me.bobthe28th.birthday.games.minigames.bmsts.minions.t1;

import me.bobthe28th.birthday.Main;
import me.bobthe28th.birthday.games.minigames.bmsts.BmTeam;
import me.bobthe28th.birthday.games.minigames.bmsts.Bmsts;
import me.bobthe28th.birthday.games.minigames.bmsts.minions.Minion;
import me.bobthe28th.birthday.games.minigames.bmsts.minions.Rarity;
import me.bobthe28th.birthday.games.minigames.bmsts.minions.entities.t1.SnowGolemEntity;

public class SnowGolemMinion extends Minion {

    public SnowGolemMinion(Main plugin, Bmsts bmsts, BmTeam team, Rarity rarity, Integer strength) {
        super(plugin,bmsts, "Snow Golem", SnowGolemEntity.class, team, 1, rarity, strength, 11);
    }
}
