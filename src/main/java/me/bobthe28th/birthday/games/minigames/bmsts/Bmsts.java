package me.bobthe28th.birthday.games.minigames.bmsts;

import me.bobthe28th.birthday.Main;
import me.bobthe28th.birthday.games.GamePlayer;
import me.bobthe28th.birthday.games.minigames.Minigame;
import me.bobthe28th.birthday.games.minigames.MinigameStatus;
import me.bobthe28th.birthday.games.minigames.bmsts.bonusrounds.BonusRound;
import me.bobthe28th.birthday.games.minigames.bmsts.minions.entities.MinionEntity;
import me.bobthe28th.birthday.games.minigames.bmsts.minions.t0.ChickenMinion;
import me.bobthe28th.birthday.games.minigames.bmsts.minions.t0.LlamaMinion;
import me.bobthe28th.birthday.games.minigames.bmsts.minions.t0.SilverfishMinion;
import me.bobthe28th.birthday.games.minigames.bmsts.minions.t1.ZombieMinion;
import me.bobthe28th.birthday.games.minigames.bmsts.minions.t2.PillagerMinion;
import me.bobthe28th.birthday.games.minigames.bmsts.minions.t3.BlazeMinion;
import me.bobthe28th.birthday.games.minigames.bmsts.minions.t3.WitherSkeletonMinion;
import me.bobthe28th.birthday.games.minigames.bmsts.minions.t4.EvokerMinion;
import net.minecraft.world.entity.Entity;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftEntity;
import org.bukkit.entity.EvokerFangs;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.HashMap;

public class Bmsts extends Minigame {

    public HashMap<String, BmTeam> BmTeams = new HashMap<>();
    public HashMap<Player, BmPlayer> BmPlayers = new HashMap<>();
    public BmMap[] bmMaps;

    public ChatColor[] strengthColor = new ChatColor[]{ChatColor.WHITE,ChatColor.YELLOW,ChatColor.GOLD};
    public ChatColor[] techLevelColor = new ChatColor[]{ChatColor.RED,ChatColor.AQUA,ChatColor.GREEN,ChatColor.LIGHT_PURPLE,ChatColor.WHITE,ChatColor.BLACK};

    public Class<?>[][] minionTypes = new Class<?>[][]{
            {SilverfishMinion.class, LlamaMinion.class, ChickenMinion.class},
            {ZombieMinion.class},{PillagerMinion.class},
            {WitherSkeletonMinion.class, BlazeMinion.class},
            {EvokerMinion.class}
    };

    int round = 1;
    BonusRound currentBonusRound;
    BmMap currentMap;

    public Bmsts(Main plugin) {
        super(plugin);

        World w = plugin.getServer().getWorld("world");

        Location[] l = new Location[4];
        l[0] = new Location(w,-226, 120, -298);
        for (int i = 1; i < l.length; i++) {
            l[i] = l[i-1].clone().add(-6,0,0);
        }
        Location[] door = new Location[9];
        for (int x = 0; x < 3; x ++) {
            for (int y = 0; y < 3; y++) {
                door[x+y*3] = new Location(w,-248, 120 + y, -294 - x);
            }
        }
        BmTeams.put("yellow",new BmTeam(this,"yellow", Color.YELLOW,ChatColor.YELLOW,ChatColor.GOLD,plugin,new Location(w,-245.5, 120, -294.5,-90,0),new Location(w,-224, 121, -291),new Location(w,-228, 121, -291), Arrays.asList(l),new Location(w,-226, 121, -291),new Location(w,-231, 120, -290), Arrays.asList(door),new BoundingBox(-235, 130, -300,-234.5, 127, -298)));
        BmTeams.put("green",BmTeams.get("yellow").copy("green",Color.GREEN,ChatColor.GREEN,ChatColor.DARK_GREEN,plugin,new Vector(0,0,15),new BoundingBox(-235, 130, -295,-234.5, 127, -293)));
        BmTeams.put("red",BmTeams.get("green").copy("red",Color.RED,ChatColor.RED,ChatColor.DARK_RED,plugin,new Vector(0,0,15),new BoundingBox(-235, 130, -290,-234.5, 127, -288)));
        BmTeams.put("blue",BmTeams.get("red").copy("blue",Color.BLUE,ChatColor.BLUE,ChatColor.DARK_BLUE,plugin,new Vector(0,0,15),new BoundingBox(-235, 130, -285,-234.5, 127, -283)));

        HashMap<BmTeam,Location> minionSpawn = new HashMap<>();
        minionSpawn.put(BmTeams.get("blue"),new Location(w, -193, 92, -317));
        minionSpawn.put(BmTeams.get("red"),new Location(w, -176, 92, -334));

        bmMaps = new BmMap[]{new BmMap("temp",new Location(w,-173, 99, -326),minionSpawn)};
        currentMap = bmMaps[0];
        status = MinigameStatus.READY;
    }

    public BmMap getCurrentMap() {
        return currentMap;
    }

    @Override
    public void start() {
        for (GamePlayer player : plugin.getGamePlayers().values()) {
            BmPlayers.put(player.getPlayer(),new BmPlayer(player,plugin,this));
        }
        //todol team select
        status = MinigameStatus.PLAYING;
    }

    @Override
    public void disable() {
        for (BmTeam team : BmTeams.values()) {
            team.remove();
        }
        if (currentBonusRound != null) {
            currentBonusRound.endBonusRound(false);
        }
        HandlerList.unregisterAll(this);
        if (BmPlayers != null) {
            for (BmPlayer bmPlayer : BmPlayers.values()) {
                bmPlayer.removeNotMap();
            }
            BmPlayers.clear();
        }

    }

    @Override
    public void onPlayerJoin(GamePlayer player) {
        if (status == MinigameStatus.PLAYING) {
            BmPlayers.put(player.getPlayer(), new BmPlayer(player, plugin, this));

            //todol teleport
            player.getPlayer().getWorld().playSound(player.getPlayer().getLocation(), "playerjoin", SoundCategory.MASTER, 0.2F, 1F);
        }
    }

    @Override
    public void onPlayerLeave(GamePlayer player) {
        if (status == MinigameStatus.PLAYING) {
            if (BmPlayers.containsKey(player.getPlayer())) {
                BmPlayers.get(player.getPlayer()).remove();
            }
            player.getPlayer().getWorld().playSound(player.getPlayer().getLocation(), "playerleave", SoundCategory.MASTER, 0.2F, 1F);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getTo() != null) { //todol player not on team
            if (BmPlayers.containsKey(event.getPlayer())) {
                for (BmTeam team : BmTeams.values()) {
                    if (team.getJoinPortal().contains(event.getTo().toVector())) {
                        BmPlayers.get(event.getPlayer()).setTeam(team);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (event.getHitEntity() != null) {
            if (event.getEntity().getShooter() instanceof org.bukkit.entity.Entity pe) {
                if (((CraftEntity)pe).getHandle() instanceof MinionEntity minion && ((CraftEntity)event.getHitEntity()).getHandle() instanceof MinionEntity hitMinion) {
                    if (minion.getGameTeam() == hitMinion.getGameTeam()) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity damager;
        if (event.getDamager() instanceof Projectile p && p.getShooter() instanceof org.bukkit.entity.Entity pe) {
            damager = ((CraftEntity)pe).getHandle();
        } else {
            if (event.getDamager() instanceof EvokerFangs fangs && fangs.getOwner() != null) {
                damager = ((CraftEntity)fangs.getOwner()).getHandle();
            } else {
                damager = ((CraftEntity) event.getDamager()).getHandle();
            }
        }

        if (((CraftEntity)event.getEntity()).getHandle() instanceof MinionEntity e && damager instanceof MinionEntity d) {
            if (e.getGameTeam() == d.getGameTeam()) {
                event.setCancelled(true);
            }
        }
    }

    public void setRound(int round) {
        this.round = round;
    }

    public int getRound() {
        return round;
    }

    public HashMap<Player, BmPlayer> getPlayers() {
        return BmPlayers;
    }

    public HashMap<String, BmTeam> getTeams() {
        return BmTeams;
    }

    public ChatColor[] getStrengthColor() {
        return strengthColor;
    }

    public ChatColor[] getTechLevelColor() {
        return techLevelColor;
    }

    public Class<?>[][] getMinionTypes() {
        return minionTypes;
    }

    public void setBonusRound(BonusRound currentBonusRound) {
        this.currentBonusRound = currentBonusRound;
    }

    public BonusRound getBonusRound() {
        return currentBonusRound;
    }

    public String getHealthString(double health, ChatColor fullColor, ChatColor halfColor) {
        if (health <= 0) {
            return ChatColor.WHITE + "☠";
        }
        int fullHearts = (int) Math.floor(health / 2);
        boolean halfHeart = health - (fullHearts * 2) >= 1;
        StringBuilder healthString = new StringBuilder();
        for (int i = 0; i < fullHearts; i++) {
            healthString.append(fullColor).append("♥");
        }
        if (halfHeart) {
            healthString.append(halfColor).append("♥");
        }
        return healthString.toString();
    }
}
