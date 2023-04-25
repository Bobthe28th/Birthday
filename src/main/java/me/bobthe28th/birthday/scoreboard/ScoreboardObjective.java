package me.bobthe28th.birthday.scoreboard;

import me.bobthe28th.birthday.games.GamePlayer;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.Objective;

import java.util.HashMap;

public class ScoreboardObjective {

    HashMap<ScoreboardController, Objective> objectives = new HashMap<>();
    String title;
    String displayTitle;

    public ScoreboardObjective(String title, String displayTitle) {
        this.title = title;
        this.displayTitle = displayTitle;
    }

    public void addPlayer(ScoreboardController p) {
        Objective o = p.getScoreboard().registerNewObjective(title, Criteria.DUMMY ,displayTitle);
        objectives.put(p,o);
    }

    public Objective getObjective(GamePlayer p) {
        return objectives.get(p.getScoreboardController());
    }

}
