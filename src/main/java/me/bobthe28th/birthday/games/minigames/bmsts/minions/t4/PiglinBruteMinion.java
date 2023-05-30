package me.bobthe28th.birthday.games.minigames.bmsts.minions.t4;

import me.bobthe28th.birthday.Main;
import me.bobthe28th.birthday.games.minigames.bmsts.BmTeam;
import me.bobthe28th.birthday.games.minigames.bmsts.Bmsts;
import me.bobthe28th.birthday.games.minigames.bmsts.minions.Minion;
import me.bobthe28th.birthday.games.minigames.bmsts.minions.Rarity;
import me.bobthe28th.birthday.games.minigames.bmsts.minions.entities.t4.PiglinBruteEntity;

public class PiglinBruteMinion extends Minion {

    public PiglinBruteMinion(Main plugin, Bmsts bmsts, BmTeam team, Rarity rarity, Integer strength) {
        super(plugin,bmsts, "Piglin Brute", PiglinBruteEntity.class, team, 4, rarity, strength, 16);
    }
}
