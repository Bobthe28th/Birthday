package me.bobthe28th.birthday.games;

public abstract class Minigame {

    public GameStatus status = GameStatus.WAITING;

    public abstract void start();
    public abstract void disable(); //todo

}
