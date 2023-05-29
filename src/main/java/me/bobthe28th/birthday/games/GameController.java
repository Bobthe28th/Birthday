package me.bobthe28th.birthday.games;

import me.bobthe28th.birthday.Main;
import me.bobthe28th.birthday.games.minigames.Minigame;
import me.bobthe28th.birthday.games.minigames.MinigameStatus;
import me.bobthe28th.birthday.games.minigames.bmsts.Bmsts;
import me.bobthe28th.birthday.games.minigames.ctfsmall.CtfSmall;
import me.bobthe28th.birthday.games.minigames.ghosts.Ghosts;
import me.bobthe28th.birthday.games.minigames.oitc.Oitc;
import me.bobthe28th.birthday.games.minigames.prophunt.PropHunt;
import me.bobthe28th.birthday.games.minigames.spleef.Spleef;
import me.bobthe28th.birthday.games.minigames.survive.Survive;
import me.bobthe28th.birthday.games.minigames.tntrun.TntRun;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
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
        minigames.put("tntrun", TntRun.class);
        minigames.put("ctfsmall", CtfSmall.class);
        minigames.put("survive", Survive.class);
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
        if (minigame != null) {
            try {
                Constructor<?> constructor = minigame.getConstructor(Main.class);
                currentGame = (Minigame) constructor.newInstance(plugin);
                currentGame.start();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            currentGame = null;
        }
    }

    public void giveAdvancement(Player p, String name) {
        Advancement advancement = Bukkit.getAdvancement(new NamespacedKey(plugin,name));
        if (advancement != null) {
            AdvancementProgress progress = p.getAdvancementProgress(advancement);
            for (String criteria : progress.getRemainingCriteria()) progress.awardCriteria(criteria);
        }
    }

    public void playerJoin(GamePlayer player) {
        if (currentGame != null && currentGame.status != MinigameStatus.END) {
            currentGame.onPlayerJoin(player);
        }
    }

    public void playerLeave(GamePlayer player) {
        if (currentGame != null && currentGame.status != MinigameStatus.END) {
            currentGame.onPlayerLeave(player);
        }
    }

    public void disable() {
        if (currentGame != null) {
            currentGame.disable();
        }
    }
}
