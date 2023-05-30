package me.bobthe28th.birthday.games.minigames.bmsts.bonusrounds;

import me.bobthe28th.birthday.Main;
import me.bobthe28th.birthday.games.minigames.Minigame;
import me.bobthe28th.birthday.games.minigames.MinigameStatus;
import me.bobthe28th.birthday.games.minigames.bmsts.BmStatus;
import me.bobthe28th.birthday.games.minigames.bmsts.Bmsts;

public abstract class BonusRound extends Minigame {

    public Bmsts bmsts;

    public BonusRound(Main plugin) {
        super(plugin);
    }

    public void startBonusRound(Bmsts bmsts) {
        bmsts.bmStatus = BmStatus.BONUSROUND;
        this.bmsts = bmsts;
        this.isBonusRound = true;
        start();
    }

    public abstract void awardPoints();

    public void endBonusRound(boolean points) {
        status = MinigameStatus.END;
        if (points) {
            awardPoints();
        }
        disable();
        bmsts.endBonusRound();
    }

}
