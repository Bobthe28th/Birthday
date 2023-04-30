package me.bobthe28th.birthday.games;

import me.bobthe28th.birthday.Main;
import me.bobthe28th.birthday.games.minigames.bmsts.Bmsts;
import me.bobthe28th.birthday.games.minigames.ghosts.Ghosts;
import me.bobthe28th.birthday.games.minigames.Minigame;
import me.bobthe28th.birthday.games.minigames.oitc.Oitc;
import me.bobthe28th.birthday.games.minigames.prophunt.PropHunt;
import me.bobthe28th.birthday.games.minigames.spleef.Spleef;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.util.HashMap;

public class GameController {

    Main plugin;
    Minigame currentGame;

    public HashMap<Player,GamePlayer> GamePlayers = new HashMap<>();

    public HashMap<String,Class<? extends Minigame>> minigames = new HashMap<>();

    public GameController(Main plugin) {
        this.plugin = plugin;
        minigames.put("bm_sts", Bmsts.class);
        minigames.put("oitc", Oitc.class);
        minigames.put("ghosts", Ghosts.class);
        minigames.put("spleef", Spleef.class);
        minigames.put("prophunt", PropHunt.class);
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
