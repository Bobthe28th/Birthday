package me.bobthe28th.birthday.games.minigames.ghosts;

import me.bobthe28th.birthday.Main;
import me.bobthe28th.birthday.games.GamePlayer;
import me.bobthe28th.birthday.games.minigames.MinigamePlayer;

public class GhostPlayer extends MinigamePlayer {
    public GhostPlayer(Main plugin, GamePlayer p, Ghosts ghosts) {
        super(plugin,p,ghosts);
        p.getScoreboardController().addSetObjective(ghosts.getObjective());
        p.getScoreboardController().addTeam(ghosts.getTeam());
        ghosts.getTeam().addMemberGlobal(p.getPlayer());
    }
}
