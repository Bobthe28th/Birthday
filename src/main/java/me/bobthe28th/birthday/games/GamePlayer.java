package me.bobthe28th.birthday.games;

import me.bobthe28th.birthday.Main;
import me.bobthe28th.birthday.scoreboard.ScoreboardController;
import org.bukkit.GameMode;
import org.bukkit.entity.GlowItemFrame;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

public class GamePlayer implements Listener {

    Player player;
    Main plugin;
    ScoreboardController scoreboardController;

    public GamePlayer(Main plugin, Player player) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.player = player;
        this.plugin = plugin;
        this.player.setLevel(0);
        this.player.setExp(0.0F);
        this.player.setFoodLevel(20);
        this.player.setSaturation(0F);
        this.player.setGlowing(false);
        this.player.setPlayerListHeaderFooter("Deez","Nuts");
        scoreboardController = new ScoreboardController(this);
//        if (Bukkit.getScoreboardManager() != null) {
//            scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
//            player.setScoreboard(scoreboard);
//        }
    }

    public void remove() {
        removeNotMap();
        plugin.getGamePlayers().remove(player);
    }

    public void removeNotMap() {
        HandlerList.unregisterAll(this);
    }

    public ScoreboardController getScoreboardController() {
        return scoreboardController;
    }

    //    @EventHandler
//    public void onPlacePickupArrow(PlayerPickupArrowEvent event) {
//        if (event.getPlayer() != player) return;
//        if (player.getGameMode() == GameMode.CREATIVE) return;
//        event.setCancelled(true);
//    }

    @EventHandler
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
        if (event.getPlayer() != player) return;
        if (player.getGameMode() == GameMode.CREATIVE) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player pf) {
            if (pf != player) return;
            player.setFoodLevel(20);
            player.setSaturation(0F);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerArmorStandManipulate(PlayerArmorStandManipulateEvent event) {
        if (event.getPlayer() != player) return;
        if (player.getGameMode() == GameMode.CREATIVE) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (event.getPlayer() != player) return;
        if (player.getGameMode() == GameMode.CREATIVE) return;
        if (event.getRightClicked() instanceof ItemFrame || event.getRightClicked() instanceof GlowItemFrame) {
            event.setCancelled(true);
        }
    }

    public Player getPlayer() {
        return player;
    }
}
