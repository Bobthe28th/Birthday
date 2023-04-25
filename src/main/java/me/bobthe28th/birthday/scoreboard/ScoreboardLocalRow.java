package me.bobthe28th.birthday.scoreboard;

import org.bukkit.scoreboard.Objective;

import java.util.HashMap;

public class ScoreboardLocalRow implements ScoreboardRow {

    HashMap<ScoreboardController, String> data = new HashMap<>();
    int value;
    String defaultData;
    ScoreboardObjective objective;

    public ScoreboardLocalRow(int value, String data, ScoreboardObjective objective) {
        this.value = value;
        this.defaultData = data;
        this.objective = objective;
    }

    @Override
    public void addPlayer(ScoreboardController p) {
        objective.getObjective(p).getScore(defaultData).setScore(value);
        data.put(p,defaultData);
    }

    public void update(String data, ScoreboardController p) {
        String oldData = this.data.get(p);
        this.data.replace(p,data);
        Objective o = objective.getObjectives().get(p);
        p.getScoreboard().resetScores(oldData);
        o.getScore(data).setScore(value);
    }
}
