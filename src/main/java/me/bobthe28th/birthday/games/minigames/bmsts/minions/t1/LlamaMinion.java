package me.bobthe28th.birthday.games.minigames.bmsts.minions.t1;

import me.bobthe28th.birthday.Main;
import me.bobthe28th.birthday.games.minigames.bmsts.BmTeam;
import me.bobthe28th.birthday.games.minigames.bmsts.Bmsts;
import me.bobthe28th.birthday.games.minigames.bmsts.minions.Minion;
import me.bobthe28th.birthday.games.minigames.bmsts.minions.Rarity;
import me.bobthe28th.birthday.games.minigames.bmsts.minions.entities.t1.LlamaEntity;

public class LlamaMinion extends Minion {
    public LlamaMinion(Main plugin, Bmsts bmsts, BmTeam team, Rarity rarity, Integer strength) {
        super(plugin, bmsts, "Llama", LlamaEntity.class, team, 1, rarity, strength, 4);
    }
}
