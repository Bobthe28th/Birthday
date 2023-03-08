package me.bobthe28th.birthday.games.bmsts.minions.t3;

import me.bobthe28th.birthday.Main;
import me.bobthe28th.birthday.games.bmsts.BmTeam;
import me.bobthe28th.birthday.games.bmsts.minions.Minion;
import me.bobthe28th.birthday.games.bmsts.minions.Rarity;
import me.bobthe28th.birthday.games.bmsts.minions.entities.t3.BlazeEntity;

public class BlazeMinion extends Minion {
    public BlazeMinion(Main plugin, BmTeam team, Rarity rarity, Integer strength) {
        super(plugin, "Blaze", BlazeEntity.class, team, 3, rarity, strength, 3); //todo model
    }
}
