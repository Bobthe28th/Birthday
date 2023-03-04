package me.bobthe28th.birthday;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public class GamePlayer implements Listener {

    Player player;

    public GamePlayer(Main plugin, Player player) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.player = player;
        this.player.setLevel(0);
        this.player.setExp(0.0F);
        this.player.setFoodLevel(20);
        this.player.setSaturation(0F);
        this.player.setGlowing(false);
    }

    public void remove() {
        removeNotMap();
        Main.GamePlayers.remove(player);
    }

    public void removeNotMap() {
        HandlerList.unregisterAll(this);
    }



}
