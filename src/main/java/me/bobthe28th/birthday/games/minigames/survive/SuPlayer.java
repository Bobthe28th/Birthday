package me.bobthe28th.birthday.games.minigames.survive;

import me.bobthe28th.birthday.Main;
import me.bobthe28th.birthday.games.GamePlayer;
import me.bobthe28th.birthday.games.minigames.Minigame;
import me.bobthe28th.birthday.games.minigames.MinigamePlayer;
import org.bukkit.GameMode;

public class SuPlayer extends MinigamePlayer {
    public SuPlayer(Main plugin, GamePlayer player, Minigame game) {
        super(plugin, player, game);
        player.getPlayer().setHealth(20.0);
        player.getPlayer().setGameMode(GameMode.ADVENTURE);
    }
}
