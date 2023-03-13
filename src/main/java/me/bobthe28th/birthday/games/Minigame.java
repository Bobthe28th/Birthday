package me.bobthe28th.birthday.games;

import me.bobthe28th.birthday.GamePlayer;

public abstract class Minigame {

    public GameStatus status = GameStatus.WAITING;

    public abstract void start();
    public abstract void disable();

    public abstract void onPlayerJoin(GamePlayer player);

    public abstract void onPlayerLeave(GamePlayer player);

}
