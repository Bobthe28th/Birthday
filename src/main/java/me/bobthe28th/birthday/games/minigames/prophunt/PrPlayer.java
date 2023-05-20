package me.bobthe28th.birthday.games.minigames.prophunt;

import me.bobthe28th.birthday.Main;
import me.bobthe28th.birthday.games.GamePlayer;
import me.bobthe28th.birthday.games.minigames.MinigamePlayer;
import org.bukkit.Location;
import org.bukkit.event.Listener;

public class PrPlayer extends MinigamePlayer implements Listener {

    PropHunt propHunt;
    PrPlayerType playerType;

    public PrPlayer(Main plugin, GamePlayer player, PropHunt propHunt, PrPlayerType playerType) {
        super(plugin, player, propHunt);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.propHunt = propHunt;
        this.playerType = playerType;
        player.getScoreboardController().addSetObjective(propHunt.getObjective());
        player.getScoreboardController().addTeam(propHunt.getPropTeam());
        player.getScoreboardController().addTeam(propHunt.getHunterTeam());
        if (playerType == PrPlayerType.PROP) {
            propHunt.getPropTeam().addMemberGlobal(player.getPlayer());
        } else if (playerType == PrPlayerType.HUNTER){
            propHunt.getHunterTeam().addMemberGlobal(player.getPlayer());
        }
        for (PrPlayer p : propHunt.players.values()) {
            if (p.isProp() && p instanceof PrPropPlayer prop) {
                if (prop.hide) {
                    addProp(prop);
                }
            }
        }
    }

    public PrPlayer(Main plugin, GamePlayer player, PropHunt propHunt) {
        this(plugin,player,propHunt,PrPlayerType.SPECTATOR);
    }

    public void spawn(Location l) {}
    public void giveItems() {}

    public boolean isProp() {
        return playerType == PrPlayerType.PROP;
    }

    @SuppressWarnings("UnstableApiUsage")
    public void addProp(PrPropPlayer prop) {
        if (prop.getGamePlayer() != player) {
            player.getPlayer().hidePlayer(plugin, prop.getPlayer());
            player.getPlayer().hideEntity(plugin, prop.localPropEntity);
        }
    }

    public void removeProp(PrPropPlayer prop) {
        player.getPlayer().showPlayer(plugin,prop.getPlayer());
    }

    public void remove() {
        removePr();
        player.getPlayer().setInvisible(false);
        player.getPlayer().getInventory().clear();
        player.getScoreboardController().removeObjective(propHunt.getObjective());
        player.getScoreboardController().removeTeam(propHunt.getPropTeam());
        player.getScoreboardController().removeTeam(propHunt.getHunterTeam());
        propHunt.getPropTeam().removeMemberGlobal(player.getPlayer());
        propHunt.getHunterTeam().removeMemberGlobal(player.getPlayer());
    }

    public void removePr() {}
}
