package me.bobthe28th.birthday.games.ghosts;

import me.bobthe28th.birthday.games.GamePlayer;
import me.bobthe28th.birthday.scoreboard.ScoreboardObjective;
import org.bukkit.entity.Player;

public class GhostPlayer {

    GamePlayer player;
    boolean alive = true;
    public GhostPlayer(GamePlayer p, ScoreboardObjective o) {
        this.player = p;
        p.getScoreboardController().addSetObjective(o);
    }

    public boolean isAlive() {
        return alive;
    }

    public Player getPlayer() {
        return player.getPlayer();
    }

    public GamePlayer getGamePlayer() {
        return player;
    }
}
