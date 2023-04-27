package me.bobthe28th.birthday.games.minigames;

import me.bobthe28th.birthday.Main;
import me.bobthe28th.birthday.games.GamePlayer;
import org.bukkit.entity.Player;

public abstract class MinigamePlayer {

    public Main plugin;
    public GamePlayer player;
    public boolean alive = true;
    public Minigame game;

    public MinigamePlayer(Main plugin, GamePlayer player, Minigame game) {
        this.plugin = plugin;
        this.player = player;
        this.game = game;
    }

    public GamePlayer getGamePlayer() {
        return player;
    }

    public Player getPlayer() {
        return player.getPlayer();
    }

    public boolean isAlive() {
        return alive;
    }
}
