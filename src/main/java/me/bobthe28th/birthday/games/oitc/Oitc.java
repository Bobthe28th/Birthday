package me.bobthe28th.birthday.games.oitc;

import me.bobthe28th.birthday.games.GamePlayer;
import me.bobthe28th.birthday.Main;
import me.bobthe28th.birthday.games.GameStatus;
import me.bobthe28th.birthday.games.Minigame;
import me.bobthe28th.birthday.games.bmsts.Bmsts;
import me.bobthe28th.birthday.games.bmsts.bonusrounds.BonusRound;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupArrowEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.BoundingBox;

import java.util.HashMap;
import java.util.List;

public class Oitc extends Minigame implements Listener, BonusRound {

    public static HashMap<Player, OiPlayer> OiPlayers = new HashMap<>();
    public static OiMap[] oiMaps;
    public static OiMap currentMap;

    public static int maxKills = 10;
    public static int killsPostMax = 5;
    public static int kingDeathKills = 5;

    public static boolean cross = true;
    public static boolean pickup = false;

    public static ItemStack firework;

    Main plugin;
    Bmsts bmsts;

    public Oitc(Main plugin) {
        this.plugin = plugin;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        oiMaps = new OiMap[]{
                new OiMap("temp",new BoundingBox(-15, 109, -328, 26, 131, -377)),
                new OiMap("temp2",new BoundingBox(28, 104, -328, 69, 131, -377))
        };
        currentMap = oiMaps[1];
        status = GameStatus.READY;
        removeArrows();

        firework = new ItemStack(Material.FIREWORK_ROCKET);
        FireworkMeta meta = (FireworkMeta) firework.getItemMeta();
        if (meta != null) {
            meta.setPower(2);
            meta.addEffect(FireworkEffect.builder().with(FireworkEffect.Type.BURST).withColor(Color.RED,Color.ORANGE,Color.YELLOW,Color.GREEN,Color.BLUE,Color.PURPLE).withTrail().build());
        }
        firework.setItemMeta(meta);
    }

    void removeArrows() {
        World w = plugin.getServer().getWorld("world");
        if (w != null) {
            List<Entity> inSpawn = (List<Entity>) w.getNearbyEntities(currentMap.spawnArea);
            for (Entity e : inSpawn) {
                if (e.getType() == EntityType.ARROW) {
                    e.remove();
                }
            }
        }
    }

    public static void setKing(OiPlayer king) { //todo soundl
        for (OiPlayer p : OiPlayers.values()) {
            Team t = p.scoreboard.getTeam("bdayoitcbad");
            if (t != null) {
                t.addEntry(king.getPlayer().getName());
            }
            if (p != king) {
                p.getPlayer().sendTitle(ChatColor.RED + "KILL " + king.getPlayer().getDisplayName(),ChatColor.YELLOW + "Don't let them win!",10,20,10);
            }
        }
        king.king = true;
        king.getPlayer().sendTitle(ChatColor.RED + "KILL THEM ALL",ChatColor.YELLOW + "Get " + killsPostMax + " kills to win!",10,20,10);
        king.getPlayer().setGlowing(true);
        king.giveKingItem();
        Bukkit.broadcastMessage(ChatColor.BLUE + king.getPlayer().getDisplayName() + " is the king!");
    }

    public static void kingDeath(OiPlayer king) { //todo soundl
        for (OiPlayer p : OiPlayers.values()) {
            Team t = p.scoreboard.getTeam("bdayoitc");
            if (t != null) {
                t.addEntry(king.getPlayer().getName());
            }
            if (p != king) {
                p.getPlayer().sendTitle("",ChatColor.BLUE + king.getPlayer().getDisplayName() + " was killed!",10,20,10);
            }
        }
        king.king = false;
        king.getPlayer().setGlowing(false);
        king.points = kingDeathKills;
        king.updateScoreboard(OiScoreboardRow.POINTS);
        Bukkit.broadcastMessage(ChatColor.BLUE + king.getPlayer().getDisplayName() + " is no longer the king!");
    }

    public static void addToTeams(OiPlayer toAdd) {
        for (OiPlayer p : Oitc.OiPlayers.values()) {
            if (toAdd != p) {
                Team ot = p.scoreboard.getTeam("bdayoitc");
                if (ot != null) {
                    ot.addEntry(toAdd.getPlayer().getName());
                }
            }
        }
    }

    public static void setTeams() {
        for (OiPlayer p : Oitc.OiPlayers.values()) {
            addToTeams(p);
        }
    }

    @Override
    public void start() {
        for (GamePlayer player : plugin.getGamePlayers().values()) {
            OiPlayers.put(player.getPlayer(),new OiPlayer(player,plugin));
        }
        setTeams();
        status = GameStatus.PLAYING;
        for (OiPlayer player : OiPlayers.values()) {
            player.respawn();
        }
    }

    @Override
    public void disable() {
        HandlerList.unregisterAll(this);
        removeArrows();
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

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (OiPlayers.containsKey(event.getPlayer())) event.setCancelled(true);
    }

    @EventHandler
    public void onProjectileHitEvent(ProjectileHitEvent event) {
        if (cross && event.getEntity().getType() == EntityType.ARROW) {
            event.getEntity().remove();
        }
    }

    @EventHandler
    public void onPlayerPickupArrow(PlayerPickupArrowEvent event) {
        if (!OiPlayers.containsKey(event.getPlayer())) return;
        event.getItem().remove();
        if (pickup) {
            OiPlayers.get(event.getPlayer()).giveArrow(true,1);
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
        if (OiPlayers.containsKey(event.getPlayer())) event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player player && event.getClickedInventory() != null) {
            if (!OiPlayers.containsKey(player)) return;
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getTo() == null) return;
        if (event.getTo().getBlockY() <= -60) {
            if (OiPlayers.containsKey(event.getPlayer())) {
                Bukkit.broadcastMessage(ChatColor.GRAY + "[" + ChatColor.RED + "☠" + ChatColor.GRAY + "] " + ChatColor.RED + event.getPlayer().getDisplayName() + ChatColor.GRAY + " fell down the idiot hole");
                OiPlayers.get(event.getPlayer()).death(null);
                event.getPlayer().teleport(new Location(plugin.getServer().getWorld("world"), currentMap.spawnArea.getCenterX(),currentMap.spawnArea.getCenterY(),currentMap.spawnArea.getCenterZ()));
            }
        }
        if (event.getPlayer().getGameMode() == GameMode.SPECTATOR && !currentMap.spawnArea.contains(event.getTo().toVector())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (OiPlayers.containsKey(player)) {
            if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                event.setCancelled(true);
                return;
            }
            if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
                player.teleport(new Location(plugin.getServer().getWorld("world"), currentMap.spawnArea.getCenterX(),currentMap.spawnArea.getCenterY(),currentMap.spawnArea.getCenterZ()));
            }
            if (event instanceof EntityDamageByEntityEvent ebeEvent) {
                Player damager = null;
                if (ebeEvent.getDamager() instanceof Player d) {
                    damager = d;
                } else if (ebeEvent.getDamager() instanceof Arrow a && a.getShooter() instanceof Player d) {
                    a.remove();
                    damager = d;
                } else if (ebeEvent.getDamager() instanceof Firework a && a.getShooter() instanceof Player d) {
                    a.remove();
                    damager = d;
                }

                if (damager == player) {
                    event.setCancelled(true);
                    return;
                }

                if (damager != null) {
                    Bukkit.broadcastMessage(ChatColor.GRAY + "[" + ChatColor.RED + "☠" + ChatColor.GRAY + "] " + ChatColor.RED + player.getDisplayName() + ChatColor.GRAY + " was killed by " + ChatColor.RED + damager.getDisplayName());
                    if (OiPlayers.containsKey(damager)) {
                        OiPlayers.get(damager).kill(player);
                    }
                    OiPlayers.get(player).death(damager);
                } else {
                    Bukkit.broadcastMessage(ChatColor.GRAY + "[" + ChatColor.RED + "☠" + ChatColor.GRAY + "] " + ChatColor.RED + player.getDisplayName() + ChatColor.GRAY + " died");
                    OiPlayers.get(player).death(null);
                }
            } else {
                Bukkit.broadcastMessage(ChatColor.GRAY + "[" + ChatColor.RED + "☠" + ChatColor.GRAY + "] " + ChatColor.RED + player.getDisplayName() + ChatColor.GRAY + " died");
                OiPlayers.get(player).death(null);
            }
            event.setCancelled(true);
        }
    }

    @Override
    public void startBonusRound(Bmsts bmsts) {
        this.bmsts = bmsts;
        this.isBonusRound = true;
        start();
    }

    @Override
    public void endBonusRound(boolean points) {

    }
}
