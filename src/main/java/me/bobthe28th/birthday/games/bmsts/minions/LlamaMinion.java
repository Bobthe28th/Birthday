package me.bobthe28th.birthday.games.bmsts.minions;

import me.bobthe28th.birthday.Main;
import me.bobthe28th.birthday.games.bmsts.BmTeam;
import me.bobthe28th.birthday.games.bmsts.minions.entities.LlamaEntity;

public class LlamaMinion extends Minion {
    public LlamaMinion(Main plugin, BmTeam team, Rarity rarity, Integer strength) {
        super(plugin, "Llama", LlamaEntity.class, team, 0, rarity, strength, 4);
    }
}
