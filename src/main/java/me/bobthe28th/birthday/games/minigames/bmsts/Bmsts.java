package me.bobthe28th.birthday.games.minigames.bmsts;

import me.bobthe28th.birthday.DamageRule;
import me.bobthe28th.birthday.Main;
import me.bobthe28th.birthday.games.GamePlayer;
import me.bobthe28th.birthday.games.minigames.Minigame;
import me.bobthe28th.birthday.games.minigames.MinigameStatus;
import me.bobthe28th.birthday.games.minigames.bmsts.bonusrounds.BonusRound;
import me.bobthe28th.birthday.games.minigames.bmsts.minions.entities.MinionEntity;
import me.bobthe28th.birthday.games.minigames.bmsts.minions.t0.ChickenMinion;
import me.bobthe28th.birthday.games.minigames.bmsts.minions.t0.EndermiteMinion;
import me.bobthe28th.birthday.games.minigames.bmsts.minions.t0.SilverfishMinion;
import me.bobthe28th.birthday.games.minigames.bmsts.minions.t1.LlamaMinion;
import me.bobthe28th.birthday.games.minigames.bmsts.minions.t1.SnowGolemMinion;
import me.bobthe28th.birthday.games.minigames.bmsts.minions.t1.WolfMinion;
import me.bobthe28th.birthday.games.minigames.bmsts.minions.t1.ZombieMinion;
import me.bobthe28th.birthday.games.minigames.bmsts.minions.t2.GoatMinion;
import me.bobthe28th.birthday.games.minigames.bmsts.minions.t2.PiglinMinion;
import me.bobthe28th.birthday.games.minigames.bmsts.minions.t2.PillagerMinion;
import me.bobthe28th.birthday.games.minigames.bmsts.minions.t2.SkeletonMinion;
import me.bobthe28th.birthday.games.minigames.bmsts.minions.t3.BlazeMinion;
import me.bobthe28th.birthday.games.minigames.bmsts.minions.t3.PolarBearMinion;
import me.bobthe28th.birthday.games.minigames.bmsts.minions.t3.WitherSkeletonMinion;
import me.bobthe28th.birthday.games.minigames.bmsts.minions.t4.EvokerMinion;
import me.bobthe28th.birthday.games.minigames.bmsts.minions.t4.HoglinMinion;
import me.bobthe28th.birthday.games.minigames.bmsts.minions.t4.PiglinBruteMinion;
import me.bobthe28th.birthday.games.minigames.bmsts.minions.t5.IronGolemMinion;
import me.bobthe28th.birthday.games.minigames.bmsts.minions.t5.RavagerMinion;
import me.bobthe28th.birthday.games.minigames.ghosts.Ghosts;
import me.bobthe28th.birthday.games.minigames.oitc.Oitc;
import me.bobthe28th.birthday.games.minigames.spleef.Spleef;
import me.bobthe28th.birthday.games.minigames.survive.Survive;
import me.bobthe28th.birthday.games.minigames.tntrun.TntRun;
import net.minecraft.world.entity.Entity;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftEntity;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EvokerFangs;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.lang.reflect.Constructor;
import java.util.*;

public class Bmsts extends Minigame {

    public HashMap<String, BmTeam> BmTeams = new HashMap<>();
    public HashMap<Player, BmPlayer> BmPlayers = new HashMap<>();
    public BmMap[] bmMaps;
    public int battleMusicAmount = 10;
    public int bonusroundMusicAmount = 6;

    Location playerSpawn;

    public ChatColor[] strengthColor = new ChatColor[]{ChatColor.WHITE,ChatColor.YELLOW,ChatColor.GOLD,ChatColor.BLACK};
    public ChatColor[] techLevelColor = new ChatColor[]{ChatColor.RED,ChatColor.AQUA,ChatColor.GREEN,ChatColor.LIGHT_PURPLE,ChatColor.WHITE,ChatColor.BLACK};

    List<Class<? extends BonusRound>> bonusRounds = List.of(Survive.class, Spleef.class, TntRun.class, Ghosts.class, Oitc.class);

    public BmStatus bmStatus = BmStatus.MINIONS;
    FileConfiguration config;


    public Class<?>[][] minionTypes = new Class<?>[][]{
            {SilverfishMinion.class, ChickenMinion.class, EndermiteMinion.class},
            {ZombieMinion.class, LlamaMinion.class, WolfMinion.class, SnowGolemMinion.class},
            {PillagerMinion.class, SkeletonMinion.class, GoatMinion.class, PiglinMinion.class},
            {WitherSkeletonMinion.class, BlazeMinion.class, PolarBearMinion.class},
            {EvokerMinion.class, HoglinMinion.class, PiglinBruteMinion.class},
            {RavagerMinion.class, IronGolemMinion.class}
    };

    int round = 1;
    BonusRound currentBonusRound;
    BmMap currentMap;

    World w;

    public Bmsts(Main plugin) {
        super(plugin);
        config = plugin.getConfig();
        Main.damageRule = DamageRule.NONE;
        Main.breakBlocks = false;

        w = plugin.getServer().getWorld("world");

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
        BmTeams.put("yellow",new BmTeam(this,"yellow",3, Color.YELLOW,ChatColor.YELLOW,ChatColor.GOLD,plugin,new Location(w,-245.5, 120, -294.5,-90,0),new Location(w,-224, 121, -291),new Location(w,-228, 121, -291), Arrays.asList(l),new Location(w,-226, 121, -291), new Location(w,-234, 121, -290),new Location(w,-231, 120, -290), Arrays.asList(door),new Location(w,-225.5, 121.5, -290),new BoundingBox(-295, 101, -410,-294.5, 103, -409)));
        BmTeams.put("green",BmTeams.get("yellow").copy("green",2,Color.GREEN,ChatColor.GREEN,ChatColor.DARK_GREEN,plugin,new Vector(0,0,15),new BoundingBox(-292, 101, -413,-291, 103, -412.5)));
        BmTeams.put("red",BmTeams.get("green").copy("red",1,Color.RED,ChatColor.RED,ChatColor.DARK_RED,plugin,new Vector(0,0,15),new BoundingBox(-281, 101, -413,-280, 103, -412.5)));
        BmTeams.put("blue",BmTeams.get("red").copy("blue",0,Color.BLUE,ChatColor.BLUE,ChatColor.DARK_BLUE,plugin,new Vector(0,0,15),new BoundingBox(-277.5, 101, -410,-277, 103, -409)));

        playerSpawn = new Location(w,-273, 104, -432);

        bmMaps = new BmMap[]{
                new BmMap("yellow",new Location(w,-244.5, 100, -365.5),new HashMap<>(Map.of(
                        BmTeams.get("blue"),new Location(w, -233.5, 92, -376.5),
                        BmTeams.get("green"),new Location(w, -255.5, 92, -376.5),
                        BmTeams.get("red"),new Location(w, -255.5, 92, -354.5),
                        BmTeams.get("yellow"),new Location(w, -233.5, 92, -354.5)
                ))),
                new BmMap("rest",new Location(w,-210.5, 100, -365.5), new HashMap<>(Map.of(
                        BmTeams.get("blue"), new Location(w, -202.5, 95, -373.5),
                        BmTeams.get("green"), new Location(w, -218.5, 95, -373.5),
                        BmTeams.get("red"), new Location(w, -218.5, 95, -357.5),
                        BmTeams.get("yellow"), new Location(w, -202.5, 95, -357.5)
                ))),
                new BmMap("ghost",new Location(w,-235, 105, -409), new HashMap<>(Map.of(
                        BmTeams.get("blue"), new Location(w, -215.5, 94, -428.5),
                        BmTeams.get("green"), new Location(w, -254.5, 94, -428.5),
                        BmTeams.get("red"), new Location(w, -254.5, 94, -389.5),
                        BmTeams.get("yellow"), new Location(w, -215.5, 94, -389.5)
                ))),
                new BmMap("garg",new Location(w,-198.5, 116, -322.5), new HashMap<>(Map.of(
                        BmTeams.get("blue"), new Location(w, -213.5, 98, -307.5),
                        BmTeams.get("green"), new Location(w, -183.5, 98, -307.5),
                        BmTeams.get("red"), new Location(w, -183.5, 98, -337.5),
                        BmTeams.get("yellow"), new Location(w, -213.5, 98, -337.5)
                ))),
                new BmMap("train",new Location(w,-234.5, 103, -463.5), new HashMap<>(Map.of(
                        BmTeams.get("blue"), new Location(w, -225.5, 99, -472.5),
                        BmTeams.get("green"), new Location(w, -243.5, 99, -472.5),
                        BmTeams.get("red"), new Location(w, -243.5, 99, -454.5),
                        BmTeams.get("yellow"), new Location(w, -225.5, 99, -454.5)
                )))
        };
        currentMap = bmMaps[new Random().nextInt(bmMaps.length)];
        status = MinigameStatus.READY;
    }

    public void startBattle() {
        new BukkitRunnable() {
            @Override
            public void run() {
                bmStatus = BmStatus.BATTLE;
                Main.musicController.clearAndPlayLoop(Main.musicController.getMusicByName("battle" + (new Random().nextInt(battleMusicAmount) + 1)));

                for (BmTeam g : BmTeams.values()) {
                    g.dropKept();
                    g.setReady(false);
                }

                for (BmPlayer p : BmPlayers.values()) {
                    for (BmTeam t : BmTeams.values()) {
                        t.updateDoor(p,false);
                    }
                    p.getPlayer().teleport(currentMap.getPlayerSpawn());
                    p.getPlayer().playSound(p.getPlayer().getLocation(),"preparetofight",SoundCategory.MASTER,0.3f,1f);

                }
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        for (BmTeam g : BmTeams.values()) {
                            g.spawnAll(currentMap.getMinionSpawn().get(g));
                            if (g.getReadySwitch().getBlock().getType() == Material.LEVER) {
                                g.getReadySwitch().getBlock().setBlockData(g.getReadySwitch().getBlock().getBlockData().merge(Bukkit.getServer().createBlockData("minecraft:lever[powered=false]")));
                            }
                        }
                        checkBattleWin();
                    }
                }.runTaskLater(plugin, 40);
            }
        }.runTaskLater(plugin,60);

    }

    public void checkBattleWin() {
        BmTeam winner = null;
        for (BmTeam t : BmTeams.values()) {
            if (!t.isDead()) {
                if (winner == null) {
                    winner = t;
                } else {
                    return;
                }
            }
        }
        if (winner != null) {
            winner.addResearchPoints(1,false);
            winner.winRound();
            if (winner.getWins() >= 12) {
                end(winner.getGameMembers());
            } else {
                for (BmPlayer p : BmPlayers.values()) {
                    p.getPlayer().playSound(p.getPlayer().getLocation(), winner.getTeam().getTitle() + "win", SoundCategory.MASTER, 0.3f, 1f);
                }
                Bukkit.broadcastMessage(ChatColor.GRAY + "[" + winner.getTeam().getColor() + "✪" + ChatColor.GRAY + "] " + winner.getTeam().getColor() + winner.getDisplayName() + " won the round");
                for (BmPlayer p : BmPlayers.values()) {
                    p.getPlayer().sendTitle("", winner.getTeam().getColor() + winner.getDisplayName() + " won the round", 10, 20, 10);
                }
            }
        } else {
            for (BmPlayer p : BmPlayers.values()) {
                p.getPlayer().playSound(p.getPlayer().getLocation(),"draw",SoundCategory.MASTER,0.3f,1f);
            }
            Bukkit.broadcastMessage(ChatColor.GRAY + "[" + ChatColor.WHITE + "✪" + ChatColor.GRAY + "] " + ChatColor.WHITE + " No one won the round");
            for (BmPlayer p : BmPlayers.values()) {
                p.getPlayer().sendTitle("", ChatColor.WHITE + "No one won the round", 10, 20, 10);
            }
        }

        setRound(round + 1);
        currentMap = bmMaps[new Random().nextInt(bmMaps.length)];
        new BukkitRunnable() {
            @Override
            public void run() {
                bmStatus = BmStatus.MINIONS;
                for (BmTeam t : BmTeams.values()) {
                    t.removeEntities();
                    t.addResearchPoints(5,false);
                }
                if (round % 2 == 1) {
                    Random r = new Random(round);
                    Class<? extends BonusRound> nextBonusRound = bonusRounds.get(r.nextInt(bonusRounds.size()));
                    try {
                        Constructor<?> constructor = nextBonusRound.getConstructor(Main.class);
                        setBonusRound((BonusRound) constructor.newInstance(plugin));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    for (BmPlayer p : BmPlayers.values()) {
                        Main.gameController.giveAdvancement(p.getPlayer(),"bmsts/bonusround");
                    }
                    getBonusRound().startBonusRound(Bmsts.this);
                } else {
                    for (BmPlayer p : BmPlayers.values()) {
                        p.getPlayer().getInventory().setHeldItemSlot(4);
                        p.getPlayer().teleport(p.getTeam().getPlayerSpawn());
                        for (BmTeam t : BmTeams.values()) {
                            t.updateDoor(p,false);
                        }
                    }
                    Main.musicController.clearAndPlayLoop(Main.musicController.getMusicByName("elevator"));
                }
            }
        }.runTaskLater(plugin,60);
    }

    public FileConfiguration getConfig() {
        return config;
    }

    //    public BmMap getCurrentMap() {
//        return currentMap;
//    }

    @Override
    public void start() {
        for (GamePlayer player : plugin.getGamePlayers().values()) {
            BmPlayers.put(player.getPlayer(),new BmPlayer(player,plugin,this));
        }
        //todol team select
        status = MinigameStatus.PLAYING;
        Main.musicController.clearAndPlayLoop(Main.musicController.getMusicByName("elevator"));
    }

    @Override
    public void disable() {
        status = MinigameStatus.END;
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
        if (event.getTo() != null) {
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
                    if (minion.getMinion().getTeam() == hitMinion.getMinion().getTeam()) {
                        event.setCancelled(true);
                    }
                }
            }
        }
        if (event.getEntity() instanceof Arrow) {
            event.getEntity().remove();
        }
    }

//    @EventHandler
//    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
//        if (BmPlayers.containsKey(event.getPlayer())) event.setCancelled(true);
//    }

    @EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        if (!BmPlayers.containsKey(event.getPlayer())) return;
        if (bmStatus == BmStatus.MINIONS) {
            event.setCancelled(true);
            event.getPlayer().getInventory().setHeldItemSlot(4);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player player && event.getClickedInventory() != null) {
            if (!BmPlayers.containsKey(player)) return;
            if (bmStatus == BmStatus.MINIONS) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (!BmPlayers.containsKey(Bukkit.getPlayer(event.getPlayer().getName()))) return;
        if (event.getInventory().getType() == InventoryType.HOPPER) {
            event.setCancelled(true);
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
            if (e.getMinion().getTeam() == d.getMinion().getTeam()) {
                event.setCancelled(true);
            }
        }
    }

    public void setRound(int round) {
        this.round = round;
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
        boolean halfHeart = health - (fullHearts * 2) >= 0;
        StringBuilder healthString = new StringBuilder();
        for (int i = 0; i < fullHearts; i++) {
            healthString.append(fullColor).append("♥");
        }
        if (halfHeart) {
            healthString.append(halfColor).append("♥");
        }
        return healthString.toString();
    }

    public ChatColor getTeamColor(Player p, ChatColor defaultColor) {
        if (BmPlayers.containsKey(p)) {
            if (BmPlayers.get(p).getTeam() != null) {
                return BmPlayers.get(p).getTeam().getColor();
            }
        }
        return defaultColor;
    }

    public void endBonusRound() {
        Main.damageRule = DamageRule.NONE;
        bmStatus = BmStatus.MINIONS;
        Main.breakBlocks = false;

        if (status != MinigameStatus.END) {
            for (BmPlayer p : BmPlayers.values()) {
                Objects.requireNonNull(p.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(20.0);
                p.getPlayer().setHealth(20.0);
                p.getPlayer().setGameMode(GameMode.ADVENTURE);
                p.getPlayer().teleport(p.getTeam().playerSpawn);
                p.getPlayer().getInventory().setHeldItemSlot(4);
            }
            Main.musicController.clearAndPlayLoop(Main.musicController.getMusicByName("elevator"));
        }
    }
}
