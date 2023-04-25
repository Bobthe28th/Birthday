package me.bobthe28th.birthday.games;

import me.bobthe28th.birthday.Main;
import me.bobthe28th.birthday.games.bmsts.Bmsts;
import me.bobthe28th.birthday.games.oitc.Oitc;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.util.HashMap;

public class GameController { //todo move gameplayers here?

    Main plugin;
    Minigame currentGame;

    public HashMap<Player,GamePlayer> GamePlayers;

    public HashMap<String,Class<? extends Minigame>> minigames = new HashMap<>();

    public GameController(Main plugin) {
        this.plugin = plugin;
        minigames.put("bm_sts", Bmsts.class);
        minigames.put("oitc", Oitc.class);
    }

    public void addNewPlayer(Player p) {
        GamePlayers.put(p,new GamePlayer(plugin,p));
    }

    public HashMap<Player, GamePlayer> getGamePlayers() {
        return GamePlayers;
    }

    public void setMinigame(Class<? extends Minigame> minigame, Main plugin) {
        if (currentGame != null) {
            currentGame.disable();
        }
        try {
            Constructor<?> constructor = minigame.getConstructor(Main.class);
            currentGame = (Minigame) constructor.newInstance(plugin);
            currentGame.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void playerJoin(GamePlayer player) {
        if (currentGame != null) {
            currentGame.onPlayerJoin(player);
        }
    }

    public void playerLeave(GamePlayer player) {
        if (currentGame != null) {
            currentGame.onPlayerLeave(player);
        }
    }

    public void disable() {
        if (currentGame != null) {
            currentGame.disable();
        }
    }
}
