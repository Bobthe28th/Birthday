package me.bobthe28th.birthday.games.minigames.bmsts.minions.t2;

import me.bobthe28th.birthday.Main;
import me.bobthe28th.birthday.games.minigames.bmsts.BmTeam;
import me.bobthe28th.birthday.games.minigames.bmsts.Bmsts;
import me.bobthe28th.birthday.games.minigames.bmsts.minions.Minion;
import me.bobthe28th.birthday.games.minigames.bmsts.minions.Rarity;
import me.bobthe28th.birthday.games.minigames.bmsts.minions.entities.t2.SkeletonEntity;

public class SkeletonMinion extends Minion {

    public SkeletonMinion(Main plugin, Bmsts bmsts, BmTeam team, Rarity rarity, Integer strength) {
        super(plugin,bmsts, "Skeleton", SkeletonEntity.class, team, 2, rarity, strength, 9);
    }
}
