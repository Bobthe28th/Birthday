package me.bobthe28th.birthday.games.minigames.bmsts.minions.t5;

import me.bobthe28th.birthday.Main;
import me.bobthe28th.birthday.games.minigames.bmsts.BmTeam;
import me.bobthe28th.birthday.games.minigames.bmsts.Bmsts;
import me.bobthe28th.birthday.games.minigames.bmsts.minions.Minion;
import me.bobthe28th.birthday.games.minigames.bmsts.minions.Rarity;
import me.bobthe28th.birthday.games.minigames.bmsts.minions.entities.t5.IronGolemEntity;

public class IronGolemMinion extends Minion {

    public IronGolemMinion(Main plugin, Bmsts bmsts, BmTeam team, Rarity rarity, Integer strength) {
        super(plugin,bmsts, "Iron Golem", IronGolemEntity.class, team, 5, rarity, strength, 19);
    }
}
