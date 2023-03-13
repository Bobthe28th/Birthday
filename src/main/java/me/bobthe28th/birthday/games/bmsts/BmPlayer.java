package me.bobthe28th.birthday.games.bmsts;

import me.bobthe28th.birthday.GamePlayer;
import me.bobthe28th.birthday.Main;
import org.bukkit.Material;
import org.bukkit.block.data.Powerable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class BmPlayer implements Listener {
    GamePlayer player;
    BmTeam team;

    public BmPlayer(GamePlayer player, Main plugin) {
        this.player = player;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void setTeam(BmTeam team) {
        if (this.team != null) {
            this.team.dropKeptBy(player.getPlayer());
            this.team.getTeam().removeEntry(player.getPlayer().getName());
        }
        this.team = team;
        team.getTeam().addEntry(player.getPlayer().getName());
        player.getPlayer().teleport(team.getPlayerSpawn(), PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

    public void remove() {
        removeNotMap();
        Bmsts.BmPlayers.remove(player.getPlayer());
    }

    public void removeNotMap() {
        if (this.team != null) {
            this.team.dropKeptBy(player.getPlayer());
//            this.team.getTeam().removeEntry(player.getPlayer().getName()); //todo ?
        }
        HandlerList.unregisterAll(this);
    }

    public Player getPlayer() {
        return player.getPlayer();
    }

    public BmTeam getTeam() {
        return team;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getPlayer() != player.getPlayer()) return;
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && team != null && event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.LEVER && event.getClickedBlock().getLocation().equals(team.getReadySwitch())) {
            if (((Powerable) event.getClickedBlock().getBlockData()).isPowered()) {
                if (team.isReady()) {
                    event.setCancelled(true);
                }
            } else {
                team.setReady(true);
            }
            event.getClickedBlock().getState().update(true, true);
        }
    }
}
