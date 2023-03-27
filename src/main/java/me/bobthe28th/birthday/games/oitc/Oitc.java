package me.bobthe28th.birthday.games.oitc;

import me.bobthe28th.birthday.GamePlayer;
import me.bobthe28th.birthday.Main;
import me.bobthe28th.birthday.games.GameStatus;
import me.bobthe28th.birthday.games.Minigame;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.util.BoundingBox;

import java.util.HashMap;

public class Oitc extends Minigame implements Listener {

    public static HashMap<Player, OiPlayer> OiPlayers = new HashMap<>();
    public static OiMap[] oiMaps;
    public static OiMap currentMap;

    Main plugin;

    public Oitc(Main plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        oiMaps = new OiMap[]{new OiMap("temp",new BoundingBox(-15, 109, -329, 25, 127, -377))};
        currentMap = oiMaps[0];
        status = GameStatus.READY;
    }

    @Override
    public void start() {
        for (GamePlayer player : Main.GamePlayers.values()) {
            OiPlayers.put(player.getPlayer(),new OiPlayer(player,plugin));
        }
        status = GameStatus.PLAYING;
        for (OiPlayer player : OiPlayers.values()) {
            player.respawn();
        }
    }

    @Override
    public void disable() {
        HandlerList.unregisterAll(this);
        if (OiPlayers != null) {
            for (OiPlayer oiPlayer : OiPlayers.values()) {
                oiPlayer.removeNotMap();
            }
            OiPlayers.clear();
        }
    }

    @Override
    public void onPlayerJoin(GamePlayer player) {
        if (status == GameStatus.PLAYING) {
            OiPlayers.put(player.getPlayer(), new OiPlayer(player, plugin));
            OiPlayers.get(player.getPlayer()).respawn();
        }
    }

    @Override
    public void onPlayerLeave(GamePlayer player) {
        if (OiPlayers.containsKey(player.getPlayer())) {
            OiPlayers.get(player.getPlayer()).remove();
        }
    }
}
