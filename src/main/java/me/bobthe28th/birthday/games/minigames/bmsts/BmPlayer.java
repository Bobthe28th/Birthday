package me.bobthe28th.birthday.games.minigames.bmsts;

import me.bobthe28th.birthday.Main;
import me.bobthe28th.birthday.games.GamePlayer;
import me.bobthe28th.birthday.games.minigames.MinigamePlayer;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.data.Powerable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class BmPlayer extends MinigamePlayer implements Listener {
    Bmsts bmsts;
    BmTeam team;

    public BmPlayer(GamePlayer player, Main plugin, Bmsts bmsts) {
        super(plugin,player,bmsts);
        this.bmsts = bmsts;
        for (BmTeam b  : bmsts.getTeams().values()) {
            b.getTeam().addPlayer(player.getScoreboardController());
            b.updateDoor(this,false);
        }
        player.getPlayer().teleport(bmsts.playerSpawn);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        player.getPlayer().getInventory().clear();
        player.getPlayer().getInventory().setHeldItemSlot(4);
        player.getPlayer().setGameMode(GameMode.ADVENTURE);
        Main.gameController.giveAdvancement(player.getPlayer(),"bmsts");
    }

    public void setTeam(BmTeam team) {
        if (this.team != null) {
            this.team.dropKeptBy(player.getPlayer());
            this.team.getTeam().removeMemberGlobal(player.getPlayer());
        }
        this.team = team;
        this.team.updateDoor(this,this.team.isReady());
        team.getTeam().addMemberGlobal(player.getPlayer());
        player.getPlayer().teleport(team.getPlayerSpawn(), PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

    public void remove() {
        removeNotMap();
        bmsts.getPlayers().remove(player.getPlayer());
    }

    public void removeNotMap() {
        if (this.team != null) {
            this.team.dropKeptBy(player.getPlayer());
            this.team.getTeam().removeMemberGlobal(player.getPlayer());
        }
        for (BmTeam b  : bmsts.getTeams().values()) {
            b.getTeam().removePlayer(player.getScoreboardController());
            b.updateDoor(this,true);
        }
        HandlerList.unregisterAll(this);
    }

    public BmTeam getTeam() {
        return team;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getPlayer() != player.getPlayer()) return;
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && team != null && event.getClickedBlock() != null) {
            if (event.getClickedBlock().getType() == Material.LEVER && event.getClickedBlock().getLocation().equals(team.getReadySwitch())) {
                if (((Powerable) event.getClickedBlock().getBlockData()).isPowered()) {
                    if (team.isReady()) {
                        event.setCancelled(true);
                    }
                } else {
                    team.setReady(true);
                }
                event.getClickedBlock().getState().update(true, true);
            } else if (event.getClickedBlock().getType() == Material.STONE_BUTTON && event.getClickedBlock().getLocation().equals(team.getDropButton())) {
                team.dropAll();
            }
        }
    }
}
