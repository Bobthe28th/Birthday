package me.bobthe28th.birthday.games.bmsts.bonusrounds.survive;

import me.bobthe28th.birthday.games.bmsts.BmPlayer;

public class SurvivePlayer {

    BmPlayer player;

    boolean alive = true;

    public SurvivePlayer(BmPlayer player) {
        this.player = player;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }
}
