package me.bobthe28th.birthday.games;

import me.bobthe28th.birthday.Main;
import me.bobthe28th.birthday.games.bmsts.Bmsts;
import me.bobthe28th.birthday.games.test.test;

import java.lang.reflect.Constructor;
import java.util.HashMap;

public class GameController { //todo move gameplayers here?

    Main plugin;
    static Minigame currentGame;

    public static HashMap<String,Class<? extends Minigame>> minigames = new HashMap<>();

    public GameController(Main plugin) {
        this.plugin = plugin;
        minigames.put("bm_sts",Bmsts.class);
        minigames.put("test",test.class);
    }

    public static void setMinigame(Class<? extends Minigame> minigame,Main plugin) {
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

    public void disable() {
        if (currentGame != null) {
            currentGame.disable();
        }
    }
}
