package me.bobthe28th.birthday.games.minigames.bmsts.minions.t4;

import me.bobthe28th.birthday.Main;
import me.bobthe28th.birthday.games.minigames.bmsts.BmTeam;
import me.bobthe28th.birthday.games.minigames.bmsts.Bmsts;
import me.bobthe28th.birthday.games.minigames.bmsts.minions.Minion;
import me.bobthe28th.birthday.games.minigames.bmsts.minions.Rarity;
import me.bobthe28th.birthday.games.minigames.bmsts.minions.entities.t4.EvokerEntity;

public class EvokerMinion extends Minion {
    public EvokerMinion(Main plugin, Bmsts bmsts, BmTeam team, Rarity rarity, Integer strength) {
        super(plugin,bmsts, "Evoker", EvokerEntity.class, team, 4, rarity, strength, 8);
    }
}
