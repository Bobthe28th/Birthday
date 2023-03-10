package me.bobthe28th.birthday;

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
import org.bukkit.event.player.PlayerPickupArrowEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

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
        this.player.setPlayerListHeaderFooter("test","test");
    }

    public void remove() {
        removeNotMap();
        Main.GamePlayers.remove(player);
    }

    public void removeNotMap() {
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    public void onPlacePickupArrow(PlayerPickupArrowEvent event) {
        if (event.getPlayer() != player) return;
        if (player.getGameMode() == GameMode.CREATIVE) return;
        event.setCancelled(true);
    }

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

}
