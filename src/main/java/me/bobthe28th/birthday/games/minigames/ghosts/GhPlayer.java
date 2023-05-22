package me.bobthe28th.birthday.games.minigames.ghosts;

import me.bobthe28th.birthday.Main;
import me.bobthe28th.birthday.games.GamePlayer;
import me.bobthe28th.birthday.games.minigames.MinigamePlayer;

public class GhPlayer extends MinigamePlayer {
    public GhPlayer(Main plugin, GamePlayer p, Ghosts ghosts) {
        super(plugin,p,ghosts);
        if (!ghosts.isBonusRound) {
            p.getScoreboardController().addTeam(ghosts.getTeam());
            ghosts.getTeam().addMemberGlobal(p.getPlayer());
        }
        Main.gameController.giveAdvancement(player.getPlayer(),"ghosts");
    }
}
