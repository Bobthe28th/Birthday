package me.bobthe28th.birthday.games;

public abstract class Minigame {

    public GameStatus status = GameStatus.WAITING;
    public boolean isBonusRound = false;

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
