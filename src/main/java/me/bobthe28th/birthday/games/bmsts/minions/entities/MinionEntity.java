package me.bobthe28th.birthday.games.bmsts.minions.entities;

import me.bobthe28th.birthday.games.bmsts.BmTeam;

public interface MinionEntity {
    BmTeam getGameTeam();
    boolean isPreview();
}