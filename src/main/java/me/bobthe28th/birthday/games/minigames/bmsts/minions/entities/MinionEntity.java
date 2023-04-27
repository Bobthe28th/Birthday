package me.bobthe28th.birthday.games.minigames.bmsts.minions.entities;

import me.bobthe28th.birthday.games.minigames.bmsts.BmTeam;

public interface MinionEntity {
    BmTeam getGameTeam();
    boolean isPreview();
}
