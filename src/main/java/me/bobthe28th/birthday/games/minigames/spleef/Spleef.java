package me.bobthe28th.birthday.games.minigames.spleef;

import me.bobthe28th.birthday.Main;
import me.bobthe28th.birthday.games.GamePlayer;
import me.bobthe28th.birthday.games.minigames.Minigame;
import me.bobthe28th.birthday.games.minigames.MinigameStatus;
import me.bobthe28th.birthday.games.minigames.bmsts.Bmsts;
import me.bobthe28th.birthday.games.minigames.bmsts.bonusrounds.BonusRound;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.BoundingBox;

import java.util.ArrayList;
import java.util.HashMap;

public class Spleef extends Minigame implements BonusRound {

    Bmsts bmsts;
    HashMap<Player, SpPlayer> players = new HashMap<>();
    SpMap currentMap;
    public Spleef(Main plugin) {
        super(plugin);
        status = MinigameStatus.READY;
        World w = plugin.getServer().getWorld("world");
        currentMap = new SpMap("temp",w,new BoundingBox(-35,128,-292,-29,127,-286),new SpLayers(-35, -292, 123, -30, -287, 3, 2),121);
    }

    @Override
    public void start() {
        Main.pvp = false;
        for (GamePlayer player : plugin.getGamePlayers().values()) {
            players.put(player.getPlayer(),new SpPlayer(plugin,player,this));
        }
        status = MinigameStatus.PLAYING;
        for (SpPlayer player : players.values()) {
            player.getPlayer().teleport(currentMap.getSpawnLoc(new ArrayList<>(players.values())));
            player.giveItems();
        }
    }

    @Override
    public void disable() {
        currentMap.reset();
    }

    @Override
    public void onPlayerJoin(GamePlayer player) {
        players.put(player.getPlayer(),new SpPlayer(plugin, player, this));
        players.get(player.getPlayer()).alive = false;
        player.getPlayer().teleport(currentMap.getSpectateLoc());
    }

    @Override
    public void onPlayerLeave(GamePlayer player) {
        players.remove(player.getPlayer());
    }

    @Override
    public void startBonusRound(Bmsts bmsts) {
        this.bmsts = bmsts;
        this.isBonusRound = true;
    }

    @Override
    public void endBonusRound(boolean points) {

    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (!(event.getEntity().getShooter() instanceof Player player)) return;
        if (!(event.getEntity() instanceof Snowball snowball)) return;
        if (players.containsKey(player)) {
            if (event.getHitBlock() != null && event.getHitBlock().getType() == Material.SNOW_BLOCK) {
                players.get(player).breakBlock(true);
                snowball.remove();
                event.getHitBlock().setType(Material.AIR);
            }
        }
    }

    @EventHandler
    public void onBreakBlock(BlockBreakEvent event) {
        if (players.containsKey(event.getPlayer())) {
            if (event.getBlock().getType() == Material.SNOW_BLOCK) {
                players.get(event.getPlayer()).breakBlock(false);
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!players.containsKey(event.getPlayer())) return;
        if (event.getTo() != null && event.getTo().getY() <= currentMap.yDeath) {
            players.get(event.getPlayer()).death();
            SpPlayer lastAlive = null;
            for (SpPlayer p : players.values()) {
                if (p.isAlive()) {
                    if (lastAlive == null) {
                        lastAlive = p;
                    } else {
                        return;
                    }
                }
            }
            if (isBonusRound) {
                endBonusRound(true);
            } else {
                end(lastAlive); //todo top 3?
            }
        }
    }

}
