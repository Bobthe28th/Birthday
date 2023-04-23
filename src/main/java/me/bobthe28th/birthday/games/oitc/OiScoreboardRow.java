package me.bobthe28th.birthday.games.oitc;

public enum OiScoreboardRow {
    POINTS(3),
    KILLS(2),
    DEATHS(1);

    final int row;
    OiScoreboardRow(int i) {
        row = i;
    }

    public int getRow() {
        return row;
    }
}