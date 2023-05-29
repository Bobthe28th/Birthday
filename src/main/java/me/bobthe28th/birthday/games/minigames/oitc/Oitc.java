package me.bobthe28th.birthday.games.minigames.oitc;

import me.bobthe28th.birthday.DamageRule;
import me.bobthe28th.birthday.Main;
import me.bobthe28th.birthday.games.GamePlayer;
import me.bobthe28th.birthday.games.minigames.MinigameStatus;
import me.bobthe28th.birthday.games.minigames.bmsts.bonusrounds.BonusRound;
import me.bobthe28th.birthday.scoreboard.ScoreboardObjective;
import me.bobthe28th.birthday.scoreboard.ScoreboardTeam;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupArrowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.BoundingBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Oitc extends BonusRound {

    public HashMap<Player, OiPlayer> OiPlayers = new HashMap<>();
    ArrayList<OiPlayer> topPoints = new ArrayList<>();
    public OiMap[] oiMaps;
    public OiMap currentMap;

    public int maxKills = 10;
    public int killsPostMax = 5;

    public boolean cross = true;
    public boolean pickup = false;

    public ItemStack firework;

    ScoreboardObjective objective;
    ScoreboardTeam gTeam;
    ScoreboardTeam kTeam;

    public Oitc(Main plugin) { //todos add timer for bmsts
        super(plugin);

        World w = plugin.getServer().getWorld("world");
        oiMaps = new OiMap[]{
                new OiMap("temp",w,new BoundingBox(-15, 109, -328, 26, 131, -377)),
                new OiMap("temp2",w,new BoundingBox(28, 104, -328, 69, 131, -377))
        };
        oiMaps[1].addBlackListedSpawnOnBlocks(new Material[]{Material.WHITE_WOOL,Material.RED_WOOL,Material.CACTUS,Material.SPRUCE_FENCE,Material.OAK_STAIRS});
        oiMaps[1].addBlackListedSpawnInBlocks(new Material[]{Material.REDSTONE_TORCH,Material.FIRE,Material.LAVA,Material.COBWEB});
        currentMap = oiMaps[1];
        status = MinigameStatus.READY;
        removeArrows();

        firework = new ItemStack(Material.FIREWORK_ROCKET);
        FireworkMeta meta = (FireworkMeta) firework.getItemMeta();
        if (meta != null) {
            meta.setPower(2);
            meta.addEffect(FireworkEffect.builder().with(FireworkEffect.Type.BURST).withColor(Color.RED,Color.ORANGE,Color.YELLOW,Color.GREEN,Color.BLUE,Color.PURPLE).withTrail().build());
        }
        firework.setItemMeta(meta);
        objective = new ScoreboardObjective("otic","One in the Chamber");
        objective.addRow(9, ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "Top Points:",true);
        objective.addRow(5, "",true);
        objective.addRow(4, ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "Your Stats:",true);
        objective.addRow(3,"Points: 0",false);
        objective.addRow(2,"Kills: 0",false);
        objective.addRow(1,"Deaths: 0",false);

        gTeam = new ScoreboardTeam("oitc",true,false, Team.OptionStatus.NEVER,ChatColor.WHITE);
        kTeam = new ScoreboardTeam("oitcking",true,false, Team.OptionStatus.NEVER,ChatColor.RED);
    }

    public void updateTopPoints(OiPlayer p) {
        topPoints.remove(p);
        for (OiPlayer t : topPoints) {
            if (p.points > t.points) {
                topPoints.add(topPoints.indexOf(t),p);
                break;
            }
        }
        if (!topPoints.contains(p)) {
            topPoints.add(p);
        }

        for (int i = 0; i < Math.min(topPoints.size(),3); i++) {
            String data;
            if (isBonusRound) {
                data = (topPoints.get(i).king ? ChatColor.GOLD : ChatColor.WHITE) + String.valueOf(i + 1) + ". " + bmsts.getTeamColor(topPoints.get(i).getPlayer(),ChatColor.WHITE) + topPoints.get(i).getPlayer().getDisplayName() + ChatColor.WHITE + ": " + topPoints.get(i).points;
            } else {
                data = (topPoints.get(i).king ? ChatColor.GOLD : ChatColor.WHITE) + String.valueOf(i + 1) + ". " + ChatColor.WHITE + topPoints.get(i).getPlayer().getDisplayName() + ": " + topPoints.get(i).points;
            }
            if (objective.hasRow(8 - i)) {
                objective.updateRow(8 - i,data);
            } else {
                objective.addRow(8 - i, data,true);
            }
        }
    }

    void removeArrows() {
        World w = plugin.getServer().getWorld("world");
        if (w != null) {
            List<Entity> inSpawn = (List<Entity>) w.getNearbyEntities(currentMap.getSpawnArea());
            for (Entity e : inSpawn) {
                if (e.getType() == EntityType.ARROW) {
                    e.remove();
                }
            }
        }
    }

    public void setKing(OiPlayer king) { //todol sound
        if (king.alive) {
            if (!isBonusRound) {
                gTeam.removeMemberGlobal(king.getPlayer());
                kTeam.addMemberGlobal(king.getPlayer());
            }
            for (OiPlayer p : OiPlayers.values()) {
                if (p != king) {
                    if (isBonusRound) {
                        p.getPlayer().sendTitle(ChatColor.RED + "KILL " + bmsts.getTeamColor(king.getPlayer(),ChatColor.RED) + king.getPlayer().getDisplayName(), ChatColor.YELLOW + "Don't let them win!", 10, 20, 10);
                    } else {
                        p.getPlayer().sendTitle(ChatColor.RED + "KILL " + king.getPlayer().getDisplayName(), ChatColor.YELLOW + "Don't let them win!", 10, 20, 10);
                    }
                }
            }
            king.king = true;
            updateTopPoints(king);
            king.getPlayer().sendTitle(ChatColor.RED + "KILL THEM ALL", ChatColor.YELLOW + "Get " + killsPostMax + " kills to win!", 10, 20, 10);
            king.getPlayer().setGlowing(true);
            king.giveKingItem();
            if (isBonusRound) {
                Bukkit.broadcastMessage(bmsts.getTeamColor(king.getPlayer(),ChatColor.BLUE) + king.getPlayer().getDisplayName() + ChatColor.BLUE + " is the king!");
            } else {
                Bukkit.broadcastMessage(ChatColor.BLUE + king.getPlayer().getDisplayName() + " is the king!");
            }
        } else {
            if (isBonusRound) {
                Bukkit.broadcastMessage(bmsts.getTeamColor(king.getPlayer(),ChatColor.BLUE) + king.getPlayer().getDisplayName() + ChatColor.BLUE + " was the king but died as they got it lol");
            } else {
                king.points -= killsPostMax;
                objective.updateRow(3,"Points: " + king.points, king.getGamePlayer());
                updateTopPoints(king);
                Bukkit.broadcastMessage(ChatColor.BLUE + king.getPlayer().getDisplayName() + " was the king but died as they got it lol");
            }
        }
    }

    public void kingDeath(OiPlayer king) { //todol sound
        if (!isBonusRound) {
            kTeam.removeMemberGlobal(king.getPlayer());
            gTeam.addMemberGlobal(king.getPlayer());
        }
        for (OiPlayer p : OiPlayers.values()) {
            if (p != king) {
                p.getPlayer().sendTitle("",ChatColor.BLUE + king.getPlayer().getDisplayName() + " was killed!",10,20,10);
            }
        }
        king.king = false;
        king.getPlayer().setGlowing(false);
        king.points -= killsPostMax;
        objective.updateRow(3,"Points: " + king.points, king.getGamePlayer());
        updateTopPoints(king);
        Bukkit.broadcastMessage(ChatColor.BLUE + king.getPlayer().getDisplayName() + " is no longer the king!");
    }

    public ScoreboardObjective getObjective() {
        return objective;
    }

    public ScoreboardTeam getGTeam() {
        return gTeam;
    }

    public ScoreboardTeam getKTeam() {
        return kTeam;
    }

    public OiMap getCurrentMap() {
        return currentMap;
    }

    public HashMap<Player, OiPlayer> getOiPlayers() {
        return OiPlayers;
    }

    @Override
    public void start() {
        for (GamePlayer player : plugin.getGamePlayers().values()) {
            OiPlayers.put(player.getPlayer(),new OiPlayer(player,plugin,this));
        }
        status = MinigameStatus.PLAYING;
        for (OiPlayer player : OiPlayers.values()) {
            player.respawn();
        }
        Main.damageRule = DamageRule.ALL;
        Main.breakBlocks = false;
        if (isBonusRound) {
            Main.musicController.clearAndPlayLoop(Main.musicController.getMusicByName("bonusround" + (new Random().nextInt(bmsts.bonusroundMusicAmount) + 1)));
        }
    }

    @Override
    public void disable() {
        HandlerList.unregisterAll(this);
        removeArrows();
        if (OiPlayers != null) {
            for (OiPlayer oiPlayer : OiPlayers.values()) {
                oiPlayer.remove();
            }
            OiPlayers.clear();
        }
        objective.remove();
        gTeam.remove();
        kTeam.remove();
    }

    @Override
    public void onPlayerJoin(GamePlayer player) {
        if (status == MinigameStatus.PLAYING) {
            OiPlayers.put(player.getPlayer(), new OiPlayer(player, plugin,this));
            OiPlayers.get(player.getPlayer()).respawn();
        }
    }

    @Override
    public void onPlayerLeave(GamePlayer player) {
        if (OiPlayers.containsKey(player.getPlayer())) {
            OiPlayers.get(player.getPlayer()).remove();
            OiPlayers.remove(player.getPlayer());
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

//    @EventHandler
//    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
//        if (OiPlayers.containsKey(event.getPlayer())) event.setCancelled(true);
//    }

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
                OiPlayers.get(event.getPlayer()).death(null,true);
                event.getPlayer().teleport(new Location(plugin.getServer().getWorld("world"), currentMap.getSpawnArea().getCenterX(),currentMap.getSpawnArea().getCenterY(),currentMap.getSpawnArea().getCenterZ()));
            }
        }
        if (event.getPlayer().getGameMode() == GameMode.SPECTATOR && !currentMap.getSpawnArea().contains(event.getTo().toVector()) && event.getFrom().getY() > 0) {
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
                player.teleport(new Location(plugin.getServer().getWorld("world"), currentMap.getSpawnArea().getCenterX(),currentMap.getSpawnArea().getCenterY(),currentMap.getSpawnArea().getCenterZ()));
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
                    if (OiPlayers.containsKey(damager)) {
                        OiPlayers.get(damager).kill(player);
                    }
                    if (OiPlayers.containsKey(player)) {
                        OiPlayers.get(player).death(damager);
                    }
                } else {
                    OiPlayers.get(player).death(null);
                }
            } else {
                OiPlayers.get(player).death(null);
            }
            event.setCancelled(true);
        }
    }

    @Override
    public void awardPoints() {
        for (int i = 0; i < Math.min(3,topPoints.size()); i++) {
            Main.gameController.giveAdvancement(topPoints.get(i).getPlayer(),"oitc/oitctop3");
            if (i == 0) {
                Main.gameController.giveAdvancement(topPoints.get(i).getPlayer(),"oitc/oitcwin");
            }
            if (bmsts.getPlayers().get(topPoints.get(i).getPlayer()).getTeam() != null) {
                bmsts.getPlayers().get(topPoints.get(i).getPlayer()).getTeam().addResearchPoints(20 - (i * 5) - (i > 0 ? 10 : 0),true);
            }
        }
    }
}
