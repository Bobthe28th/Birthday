package me.bobthe28th.birthday.games;

public abstract class Minigame {

    GameStatus status = GameStatus.WAITING;

    public abstract void init();

    public abstract void start();

}
