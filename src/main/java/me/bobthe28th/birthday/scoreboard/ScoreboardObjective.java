package me.bobthe28th.birthday.scoreboard;

import me.bobthe28th.birthday.games.GamePlayer;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.Objective;

import java.util.HashMap;

public class ScoreboardObjective {

    HashMap<ScoreboardController, Objective> objectives = new HashMap<>();
    HashMap<Integer, ScoreboardRow> rows = new HashMap<>();
    String title;
    String displayTitle;

    public ScoreboardObjective(String title, String displayTitle) {
        this.title = title;
        this.displayTitle = displayTitle;
    }

    public void addRow(int value, String data, boolean global) {
        //todos override
        if (global) {
            rows.put(value,new ScoreboardGlobalRow(value,data,this));
        } else {
            rows.put(value,new ScoreboardLocalRow(value,data,this));
        }
    }

    public void addPlayer(ScoreboardController p) {
        Objective o = p.getScoreboard().registerNewObjective(title, Criteria.DUMMY ,displayTitle);
        objectives.put(p,o);
        for (ScoreboardRow r : rows.values()) {
            r.addPlayer(p);
        }
    }

    public Objective getObjective(GamePlayer p) {
        return objectives.get(p.getScoreboardController());
    }

    public Objective getObjective(ScoreboardController p) {
        return objectives.get(p);
    }

    public void updateRow(int value, String data) {
        if (rows.containsKey(value)) {
            if (rows.get(value) instanceof ScoreboardGlobalRow g) {
                g.update(data);
            }
        }
    }

    public void updateRow(int value, String data, ScoreboardController p) {
        if (rows.containsKey(value)) {
            if (rows.get(value) instanceof ScoreboardLocalRow l) {
                l.update(data,p);
            }
        }
    }

    public void updateRow(int value, String data, GamePlayer p) {
        if (rows.containsKey(value)) {
            if (rows.get(value) instanceof ScoreboardLocalRow l) {
                l.update(data,p.getScoreboardController());
            }
        }
    }

    public void remove() {
        for (ScoreboardController p : objectives.keySet()) {
            p.removeObjective(this);
        }
    }

    public HashMap<ScoreboardController, Objective> getObjectives() {
        return objectives;
    }
}
