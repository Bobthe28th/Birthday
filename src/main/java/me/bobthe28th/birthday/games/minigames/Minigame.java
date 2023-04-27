package me.bobthe28th.birthday.games.minigames;

import me.bobthe28th.birthday.Main;
import me.bobthe28th.birthday.games.GamePlayer;

public abstract class Minigame {

    public MinigameStatus status = MinigameStatus.WAITING;
    public boolean isBonusRound = false;
    public Main plugin;

    public Minigame(Main plugin) {
        this.plugin = plugin;
    }

    public abstract void start();

    public void end() {
        //todo
    }

    public void end(GamePlayer winner) {
        //todo
    }

    public void end(GamePlayer[] winners) {
        //todo
    }

    public abstract void disable();

    public abstract void onPlayerJoin(GamePlayer player);

    public abstract void onPlayerLeave(GamePlayer player);

}
