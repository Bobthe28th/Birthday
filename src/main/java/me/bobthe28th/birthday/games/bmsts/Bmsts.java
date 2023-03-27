package me.bobthe28th.birthday.games.bmsts;

import me.bobthe28th.birthday.GamePlayer;
import me.bobthe28th.birthday.Main;
import me.bobthe28th.birthday.games.GameStatus;
import me.bobthe28th.birthday.games.Minigame;
import me.bobthe28th.birthday.games.bmsts.bonusrounds.BonusRound;
import me.bobthe28th.birthday.games.bmsts.minions.entities.MinionEntity;
import me.bobthe28th.birthday.games.bmsts.minions.t0.ChickenMinion;
import me.bobthe28th.birthday.games.bmsts.minions.t0.LlamaMinion;
import me.bobthe28th.birthday.games.bmsts.minions.t0.SilverfishMinion;
import me.bobthe28th.birthday.games.bmsts.minions.t1.ZombieMinion;
import me.bobthe28th.birthday.games.bmsts.minions.t2.PillagerMinion;
import me.bobthe28th.birthday.games.bmsts.minions.t3.BlazeMinion;
import me.bobthe28th.birthday.games.bmsts.minions.t3.WitherSkeletonMinion;
import me.bobthe28th.birthday.games.bmsts.minions.t4.EvokerMinion;
import net.minecraft.world.entity.Entity;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftEntity;
import org.bukkit.entity.EvokerFangs;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.HashMap;

public class Bmsts extends Minigame implements Listener {

    public static HashMap<String, BmTeam> BmTeams = new HashMap<>();
    public static HashMap<Player, BmPlayer> BmPlayers = new HashMap<>();
    public static BmMap[] bmMaps;

    public static ChatColor[] strengthColor = new ChatColor[]{ChatColor.WHITE,ChatColor.YELLOW,ChatColor.GOLD};
    public static ChatColor[] techLevelColor = new ChatColor[]{ChatColor.RED,ChatColor.AQUA,ChatColor.GREEN,ChatColor.LIGHT_PURPLE,ChatColor.WHITE,ChatColor.BLACK};

    public static Class<?>[][] minionTypes = new Class<?>[][]{
            {SilverfishMinion.class, LlamaMinion.class, ChickenMinion.class},
            {ZombieMinion.class},{PillagerMinion.class},
            {WitherSkeletonMinion.class, BlazeMinion.class},
            {EvokerMinion.class}
    };

    static int round = 1;
    public static BonusRound currentBonusRound;
    static BmMap currentMap;
    Main plugin;

    public Bmsts(Main plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        for (Team t : Main.board.getTeams()) {
            if (t.getName().startsWith("bday")) {
                t.unregister();
            }
        }

        World w = plugin.getServer().getWorld("world"); //todo different world?

        Location[] l = new Location[4];
        l[0] = new Location(w,-148, 92, -320);
        for (int i = 1; i < l.length; i++) {
            l[i] = l[i-1].clone().add(-6,0,0);
        }
        BmTeams.put("blue",new BmTeam("blue", Color.BLUE,ChatColor.BLUE,ChatColor.DARK_BLUE,plugin,new Location(w,-168, 92, -316,-90,0),new Location(w,-146, 92, -318),new Location(w,-146, 92, -314), Arrays.asList(l),new Location(w,-146, 92, -316),new Location(w,-152, 92, -312),new BoundingBox(-142, 92, -311,-141.5, 94, -313)));
        BmTeams.put("red",BmTeams.get("blue").copy("red",Color.RED,ChatColor.RED,ChatColor.DARK_RED,plugin,new Vector(0,0,-15),new BoundingBox(-142, 92, -316,-141.5, 94, -318)));

        HashMap<BmTeam,Location> minionSpawn = new HashMap<>();
        minionSpawn.put(BmTeams.get("blue"),new Location(w, -193, 92, -317));
        minionSpawn.put(BmTeams.get("red"),new Location(w, -176, 92, -334));

        bmMaps = new BmMap[]{new BmMap("temp",new Location(w,-173, 99, -326),minionSpawn)};
        currentMap = bmMaps[0];
        status = GameStatus.READY;
    }

    public static BmMap getCurrentMap() {
        return currentMap;
    }

    @Override
    public void start() {
        for (GamePlayer player : Main.GamePlayers.values()) {
            BmPlayers.put(player.getPlayer(),new BmPlayer(player,plugin));
        }
        //todo team select
        status = GameStatus.PLAYING;
    }

    @Override
    public void disable() {
        for (BmTeam team : BmTeams.values()) {
            team.removeMinions();
        }
        if (currentBonusRound != null && currentBonusRound.isRunning()) {
            currentBonusRound.end(false);
        }
        for (Team t : Main.board.getTeams()) {
            if (t.getName().startsWith("bdaybmsts")) {
                t.unregister();
            }
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
        if (status == GameStatus.PLAYING) {
            BmPlayers.put(player.getPlayer(), new BmPlayer(player, plugin));

            //todo teleport
            player.getPlayer().getWorld().playSound(player.getPlayer().getLocation(), "playerjoin", SoundCategory.MASTER, 0.2F, 1F);
        }
    }

    @Override
    public void onPlayerLeave(GamePlayer player) {
        if (status == GameStatus.PLAYING) {
            if (BmPlayers.containsKey(player.getPlayer())) {
                BmPlayers.get(player.getPlayer()).remove();
            }
            player.getPlayer().getWorld().playSound(player.getPlayer().getLocation(), "playerleave", SoundCategory.MASTER, 0.2F, 1F);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getTo() != null) { //todo player not on team
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

    public static void setRound(int round) {
        Bmsts.round = round;
    }

    public static int getRound() {
        return round;
    }

    public static String getHealthString(double health, ChatColor fullColor, ChatColor halfColor) {
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

    public static String rainbow(String s) {
        StringBuilder newString = new StringBuilder();
        for (char c : s.toCharArray()) {
            newString.append(randomColor()).append(c);
        }
        return newString.toString();
    }

    static char[] rainbowChars = "c6ea9b5".toCharArray();
    static int rainbowCharPos = 0;
    public static ChatColor randomColor() {
        rainbowCharPos++;
        if (rainbowCharPos >= rainbowChars.length) {
            rainbowCharPos = 0;
        }
        return ChatColor.getByChar(rainbowChars[rainbowCharPos]);
    }
}
