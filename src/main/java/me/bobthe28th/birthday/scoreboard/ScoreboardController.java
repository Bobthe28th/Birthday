package me.bobthe28th.birthday.scoreboard;

import me.bobthe28th.birthday.games.GamePlayer;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;

public class ScoreboardController {

    GamePlayer player;
    Scoreboard scoreboard;
    ArrayList<ScoreboardObjective> objectives = new ArrayList<>();

    public ScoreboardController(GamePlayer player) {
        this.player = player;
        if (Bukkit.getScoreboardManager() != null) {
            scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
            player.getPlayer().setScoreboard(scoreboard);
        }
    }

    public void addObjective(ScoreboardObjective o) {
        o.addPlayer(this);
        objectives.add(o);
    }

    public void addSetObjective(ScoreboardObjective o) {
        addObjective(o);
        setObjective(o);
    }

    public void setObjective(ScoreboardObjective o) {
        o.getObjective(player).setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    public void removeObjective(ScoreboardObjective o) {
        objectives.remove(o);
        o.getObjective(this).unregister();
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }
}
