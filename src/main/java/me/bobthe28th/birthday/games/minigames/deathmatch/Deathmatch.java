package me.bobthe28th.birthday.games.minigames.deathmatch;

import me.bobthe28th.birthday.Main;
import me.bobthe28th.birthday.games.GamePlayer;
import me.bobthe28th.birthday.games.minigames.Minigame;
import me.bobthe28th.birthday.games.minigames.MinigameMap;
import me.bobthe28th.birthday.games.minigames.survive.SuPlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;

import java.util.HashMap;
import java.util.Random;

public class Deathmatch extends Minigame {

    HashMap<Player, SuPlayer> players = new HashMap<>();
    public MinigameMap[] maps;
    MinigameMap currentMap;

    public Deathmatch(Main plugin) {
        super(plugin);
        World w = plugin.getServer().getWorld("world");
        maps = new MinigameMap[]{
                new MinigameMap("rest", w, new BoundingBox(-201, 98, -356,-221, 94, -376))
        };
        currentMap = maps[new Random().nextInt(maps.length)];
    }

    @Override
    public void start() {

    }

    @Override
    public void disable() {

    }

    @Override
    public void onPlayerJoin(GamePlayer player) {

    }

    @Override
    public void onPlayerLeave(GamePlayer player) {

    }
}
