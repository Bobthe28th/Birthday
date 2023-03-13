package me.bobthe28th.birthday.games.bmsts.bonusrounds;

import me.bobthe28th.birthday.games.bmsts.BmPlayer;
import me.bobthe28th.birthday.games.bmsts.Bmsts;

public abstract class BonusRound {

    BonusRoundMap map;
    public boolean running;

    public BonusRound(BonusRoundMap map) {
        this.map = map;
    }

    public abstract void start();

    public abstract void endRound();

    public void end(boolean teleport) {
        running = false;
        if (teleport) {
            for (BmPlayer p : Bmsts.BmPlayers.values()) {
                p.getPlayer().teleport(p.getTeam().getPlayerSpawn().clone().add(0.5, 0, 0.5));
            }
        }
        endRound();
    }

    public BonusRoundMap getMap() {
        return map;
    }

    public boolean isRunning() {
        return running;
    }
}
