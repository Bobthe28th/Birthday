package me.bobthe28th.birthday.scoreboard;

import org.bukkit.scoreboard.Objective;

public class ScoreboardGlobalRow implements ScoreboardRow {

    String data;
    int value;
    ScoreboardObjective objective;

    public ScoreboardGlobalRow(int value, String data, ScoreboardObjective objective) {
        this.value = value;
        this.data = data;
        this.objective = objective;
    }

    @Override
    public void addPlayer(ScoreboardController p) {
        objective.getObjective(p).getScore(data).setScore(value);
    }

    public void update(String data) {
        String oldData = this.data;
        this.data = data;
        for (ScoreboardController p : objective.getObjectives().keySet()) {
            Objective o = objective.getObjectives().get(p);
            p.getScoreboard().resetScores(oldData);
            o.getScore(data).setScore(value);
        }
    }
}
