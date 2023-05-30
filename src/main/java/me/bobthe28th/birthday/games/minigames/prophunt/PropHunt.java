package me.bobthe28th.birthday.games.minigames.prophunt;

import me.bobthe28th.birthday.Main;
import me.bobthe28th.birthday.games.GamePlayer;
import me.bobthe28th.birthday.games.minigames.MinigameStatus;
import me.bobthe28th.birthday.games.minigames.bmsts.Bmsts;
import me.bobthe28th.birthday.games.minigames.bmsts.bonusrounds.BonusRound;
import me.bobthe28th.birthday.scoreboard.ScoreboardObjective;
import me.bobthe28th.birthday.scoreboard.ScoreboardTeam;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class PropHunt extends BonusRound {

    Bmsts bmsts;
    HashMap<Player, PrPlayer> players = new HashMap<>();
//    HashMap<Player, PrHunterPlayer> hunters = new HashMap<>();
//    HashMap<Player, PrPropPlayer> props = new HashMap<>();

    PrMap currentMap;

    ScoreboardObjective objective;
    ScoreboardTeam propTeam;
    ScoreboardTeam hunterTeam;

    public PropHunt(Main plugin) {
        super(plugin);
        status = MinigameStatus.READY;
        World w = plugin.getServer().getWorld("world");
        currentMap = new PrMap("temp",w, new Location(w,287, 64, -519),new Location(w,287, 64, -519)); //todos locations
        currentMap.setProps(new Material[]{Material.ICE,Material.MELON,Material.PURPUR_BLOCK});
        objective = new ScoreboardObjective("prophunt","Prop Hunt");
        objective.addRow(2,"Props Left:",true);
        objective.addRow(1,"0",true);

        propTeam = new ScoreboardTeam("Props",false,false, Team.OptionStatus.FOR_OWN_TEAM, ChatColor.BLUE);
        hunterTeam = new ScoreboardTeam("Hunters",false,true, Team.OptionStatus.FOR_OWN_TEAM, ChatColor.RED);
    }

    public ScoreboardObjective getObjective() {
        return objective;
    }

    public ScoreboardTeam getPropTeam() {
        return propTeam;
    }

    public ScoreboardTeam getHunterTeam() {
        return hunterTeam;
    }

    public void updatePropsLeft() {
        int left = 0;
        for (PrPlayer player : players.values()) {
            if (player.isProp() && player.isAlive()) {
                left ++;
            }
        }
        objective.updateRow(1, String.valueOf(left));
    }

    @Override
    public void start() {
        int hunterAmount = (int) Math.ceil(plugin.getGamePlayers().size() / 3f);

        List<Player> randomPlayers = new ArrayList<>(plugin.getGamePlayers().keySet());
        Collections.shuffle(randomPlayers);

        for (Player player : randomPlayers) {
            GamePlayer p = plugin.getGamePlayers().get(player);
            if (hunterAmount > 0) {
                players.put(player, new PrHunterPlayer(plugin, p, this));
                hunterAmount --;
                players.get(player).spawn(currentMap.getHunterSpawn());
            } else {
                players.put(player, new PrPropPlayer(plugin, p, this));
                players.get(player).spawn(currentMap.getPropSpawn());
            }
        }
        status = MinigameStatus.PLAYING;
        updatePropsLeft();
    }

    @Override
    public void disable() {
        HandlerList.unregisterAll(this);
        if (players != null) {
            for (PrPlayer player : players.values()) {
                player.remove();
            }
            players.clear();
        }
        objective.remove();
        propTeam.remove();
        hunterTeam.remove();
    }

    @Override
    public void onPlayerJoin(GamePlayer player) {
        players.put(player.getPlayer(),new PrPlayer(plugin, player, this));
        players.get(player.getPlayer()).alive = false;
    }

    @Override
    public void onPlayerLeave(GamePlayer player) {
        if (players.containsKey(player.getPlayer())) {
            players.get(player.getPlayer()).remove();
            players.remove(player.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (players.containsKey(event.getPlayer())) event.setCancelled(true);
    }

//    @EventHandler
//    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
//        if (players.containsKey(event.getPlayer())) event.setCancelled(true);
//    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player player && event.getClickedInventory() != null) {
            if (!players.containsKey(player)) return;
            event.setCancelled(true);
        }
    }

    @Override
    public void awardPoints() {

    }

    public void hideProp(PrPropPlayer prop) {
        for (PrPlayer p : players.values()) {
            p.addProp(prop);
        }
    }

    public void showProp(PrPropPlayer prop) {
        for (PrPlayer p : players.values()) {
            p.removeProp(prop);
        }
    }
}
