package me.bobthe28th.birthday.games.minigames.ctfsmall;

import me.bobthe28th.birthday.Main;
import me.bobthe28th.birthday.games.GamePlayer;
import me.bobthe28th.birthday.games.minigames.MinigamePlayer;
import me.bobthe28th.birthday.scoreboard.ScoreboardTeam;

public class CtPlayer extends MinigamePlayer {

    CtfSmall ctf;

    public CtPlayer(Main plugin, GamePlayer player, CtfSmall ctf) {
        super(plugin, player, ctf);
        this.ctf = ctf;
    }

    public void remove() {
        if (!ctf.isBonusRound) {
            ctf.getTeams()[ctf.getTeam(this)].removeMemberGlobal(player.getPlayer());
            for (ScoreboardTeam t : ctf.getTeams()) {
                t.removePlayer(player.getScoreboardController());
            }
        }
    }
}
