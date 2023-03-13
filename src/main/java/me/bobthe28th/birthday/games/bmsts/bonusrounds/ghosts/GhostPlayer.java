package me.bobthe28th.birthday.games.bmsts.bonusrounds.ghosts;

import me.bobthe28th.birthday.games.bmsts.BmPlayer;

public class GhostPlayer {

    BmPlayer player;

    boolean alive = true;

    public GhostPlayer(BmPlayer player) {
        this.player = player;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public BmPlayer getPlayer() {
        return player;
    }
}
