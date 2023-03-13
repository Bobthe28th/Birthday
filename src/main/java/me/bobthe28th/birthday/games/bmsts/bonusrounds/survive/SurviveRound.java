package me.bobthe28th.birthday.games.bmsts.bonusrounds.survive;

import me.bobthe28th.birthday.Main;
import me.bobthe28th.birthday.games.bmsts.BmPlayer;
import me.bobthe28th.birthday.games.bmsts.Bmsts;
import me.bobthe28th.birthday.games.bmsts.bonusrounds.BonusRound;
import me.bobthe28th.birthday.games.bmsts.bonusrounds.BonusRoundMap;
import me.bobthe28th.birthday.games.bmsts.bonusrounds.ghosts.GhostPlayer;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Ravager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class SurviveRound extends BonusRound implements Listener {

    Main plugin;
    HashMap<Player,SurvivePlayer> players = new HashMap<>();
    ArrayList<Ravager> ravagers = new ArrayList<>();
    BukkitTask spawn;
    BukkitTask timer;
    int time = 90;
    public SurviveRound(Main plugin) {
        super(new BonusRoundMap(new Location(plugin.getServer().getWorld("world"),-205, 99, -316))); //todo world?
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void start() {
        World w = plugin.getServer().getWorld("world");

        if (w != null) {
            running = true;
            for (BmPlayer p : Bmsts.BmPlayers.values()) {
                p.getPlayer().teleport(new Location(w, -214, 92, -327));
                players.put(p.getPlayer(),new SurvivePlayer(p));
            }
            spawn = new BukkitRunnable() {
                @Override
                public void run() {
                    if (ravagers.size() < 10 && !this.isCancelled()) {
                        ravagers.add((Ravager) w.spawnEntity(new Location(w, -207, 92, -317), EntityType.RAVAGER)); //TODO add random
                    } else {
                        this.cancel();
                    }
                }
            }.runTaskTimer(plugin, 100, 100);
            timer = new BukkitRunnable() {
                @Override
                public void run() {
                    if (!this.isCancelled()) {
                        for (BmPlayer p : Bmsts.BmPlayers.values()) {
                            p.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.LIGHT_PURPLE + "" + time));
                        }
                        if (time <= 0) {
                            this.cancel();
                            for (SurvivePlayer p : players.values()) {
                                if (p.isAlive()) {
                                    p.getPlayer().getTeam().addResearchPoints(5);
                                }
                            }
                            end(true);
                        }
                        time--;
                    }
                }
            }.runTaskTimer(plugin, 0, 20);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player p) {
            if (p.getHealth() - event.getDamage() <= 0) {
                event.setCancelled(true);
                if (players.containsKey(p)) {
                    players.get(p).setAlive(false);
                }
                p.setHealth(Objects.requireNonNull(p.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getBaseValue());
                p.teleport(getMap().getPlayerRespawn());
                boolean allDead = true;
                for (SurvivePlayer s : players.values()) {
                    if (s.isAlive()) {
                        allDead = false;
                        break;
                    }
                }
                if (allDead) {
                    end(true);
                }
            }
        }
    }

    @Override
    public void endRound() {
        timer.cancel();
        spawn.cancel();
        for (Ravager r : ravagers) {
            r.remove();
        }
        ravagers.clear();
    }
}
