package me.bobthe28th.birthday.games.minigames.bmsts.minions.t3;

import me.bobthe28th.birthday.Main;
import me.bobthe28th.birthday.games.minigames.bmsts.BmTeam;
import me.bobthe28th.birthday.games.minigames.bmsts.Bmsts;
import me.bobthe28th.birthday.games.minigames.bmsts.minions.Minion;
import me.bobthe28th.birthday.games.minigames.bmsts.minions.Rarity;
import me.bobthe28th.birthday.games.minigames.bmsts.minions.entities.t3.WitherSkeletonEntity;

public class WitherSkeletonMinion extends Minion {
    public WitherSkeletonMinion(Main plugin, Bmsts bmsts, BmTeam team, Rarity rarity, Integer strength) {
        super(plugin,bmsts, "Wither Skeleton", WitherSkeletonEntity.class, team, 3, rarity, strength, 7);
    }
}