package me.bobthe28th.birthday.games.minigames.bmsts.minions.t0;

import me.bobthe28th.birthday.Main;
import me.bobthe28th.birthday.games.minigames.bmsts.BmTeam;
import me.bobthe28th.birthday.games.minigames.bmsts.Bmsts;
import me.bobthe28th.birthday.games.minigames.bmsts.minions.Minion;
import me.bobthe28th.birthday.games.minigames.bmsts.minions.Rarity;
import me.bobthe28th.birthday.games.minigames.bmsts.minions.entities.t0.EndermiteEntity;

public class EndermiteMinion extends Minion {

    public EndermiteMinion(Main plugin, Bmsts bmsts, BmTeam team, Rarity rarity, Integer strength) {
        super(plugin,bmsts, "Endermite", EndermiteEntity.class, team, 0, rarity, strength, 12);
    }
}
