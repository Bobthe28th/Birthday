package me.bobthe28th.birthday.games.bmsts;

import me.bobthe28th.birthday.Main;
import me.bobthe28th.birthday.games.bmsts.bonusrounds.survive.SurviveRound;
import me.bobthe28th.birthday.games.bmsts.minions.Minion;
import me.bobthe28th.birthday.games.bmsts.minions.Rarity;
import me.bobthe28th.birthday.games.bmsts.minions.t0.SilverfishMinion;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class BmTeam {

    Main plugin;
    Team team;
    boolean ready = false;
    boolean dead = false;

    Color bColor;
    ChatColor color;
    ChatColor darkColor;

    //Locations
    Location playerSpawn;
    Location randomizer;
    Location techUpgrade;
    List<Location> spawners = new ArrayList<>();
    Location minionItemSpawn;
    Location readySwitch;
    BoundingBox joinPortal;

    int researchPoints = 400;

    ArrayList<Minion> minions = new ArrayList<>();

    public BmTeam(String name, Color bColor, ChatColor color, ChatColor darkColor, Main plugin, Location playerSpawn, Location randomizer, Location techUpgrade, List<Location> spawners, Location readySwitch, Location minionItemSpawn, BoundingBox joinPortal) {
        this.plugin = plugin;
        this.playerSpawn = playerSpawn.clone();
        this.randomizer = randomizer.clone();
        this.techUpgrade = techUpgrade.clone();
        for (Location l : spawners) {
            this.spawners.add(l.clone());
        }
        this.bColor = bColor;
        this.color = color;
        this.darkColor = darkColor;
        this.readySwitch = readySwitch.clone();
        this.minionItemSpawn = minionItemSpawn.clone();
        this.joinPortal = joinPortal.clone();
        if (readySwitch.getBlock().getType() == Material.LEVER) {
            readySwitch.getBlock().setBlockData(readySwitch.getBlock().getBlockData().merge(Bukkit.getServer().createBlockData("minecraft:lever[powered=false]")));
        }

        team = Main.board.registerNewTeam("bdaybmsts" + name);
        team.setDisplayName(name.substring(0, 1).toUpperCase() + name.substring(1) + " Team");
        team.setColor(color);
        team.setAllowFriendlyFire(false);
        team.setCanSeeFriendlyInvisibles(true);
        team.setPrefix(ChatColor.DARK_GRAY + "[" + color + Character.toUpperCase(name.charAt(0)) + ChatColor.DARK_GRAY + "]" + " ");

        new SilverfishMinion(plugin, this, Rarity.COMMON,1).drop(minionItemSpawn.clone().add(0.5,0,0.5)); //todo on game ready
        new SilverfishMinion(plugin, this, Rarity.RARE,2).drop(minionItemSpawn.clone().add(-1.5,0,0.5));
        new SilverfishMinion(plugin, this, Rarity.GODLIKE,3).drop(minionItemSpawn.clone().add(-3.5,0,0.5));
        new SilverfishMinion(plugin, this, Rarity.AWESOME,1).drop(minionItemSpawn.clone().add(-5.5,0,0.5));

    }

    public int getResearchPoints() {
        return researchPoints;
    }

    public void addResearchPoints(int amount) {
        this.researchPoints += amount;
    }

    public void minionDeath() { //todo none spawn
        for (Minion m : minions) {
            if (m.getEntities().size() != 0) return;
        }
        dead = true;
        Bukkit.broadcastMessage(ChatColor.GRAY + "[" + team.getColor() + "☠" + ChatColor.GRAY + "] " + team.getColor() + team.getDisplayName() + " dead");
        for (BmPlayer p : Bmsts.BmPlayers.values()) {
            p.getPlayer().sendTitle("",team.getColor() + team.getDisplayName() + " dead",10,20,10);
        }
        BmTeam winner = null;
        for (BmTeam t : Bmsts.BmTeams.values()) {
            if (!t.isDead()) {
                if (winner == null) {
                    winner = t;
                } else {
                    return;
                }
            }
        }
        if (winner != null) {
            for (BmTeam t : Bmsts.BmTeams.values()) {
                t.removeEntities();
            }
            Bukkit.broadcastMessage(ChatColor.GRAY + "[" + winner.getTeam().getColor() + "✪" + ChatColor.GRAY + "] " + winner.getTeam().getColor() + winner.getTeam().getDisplayName() + " won the round");
            for (BmPlayer p : Bmsts.BmPlayers.values()) {
                p.getPlayer().sendTitle("", winner.getTeam().getColor() + winner.getTeam().getDisplayName() + " won the round", 10, 20, 10);
                p.getPlayer().teleport(p.getTeam().getPlayerSpawn().clone().add(0.5,0,0.5));
            }
            Bmsts.setRound(Bmsts.getRound() + 1);
            if (Bmsts.getRound() % 2 == 1) {
                Bmsts.currentBonusRound = new SurviveRound(plugin);
                Bmsts.currentBonusRound.start();
            }
        }
    }

    public void setReady(boolean ready) {
        this.ready = ready;
        if (ready) {
            boolean allReady = true;
            Bukkit.broadcastMessage(ChatColor.GRAY + "[" + team.getColor() + "✔" + ChatColor.GRAY + "] " + team.getColor() + team.getDisplayName() + " ready");
            for (BmPlayer p : Bmsts.BmPlayers.values()) {
                p.getPlayer().sendTitle("", team.getColor() + team.getDisplayName() + " ready", 10, 20, 10);
            }
            for (BmTeam g : Bmsts.BmTeams.values()) {
                if (!g.isReady()) { //todo check if has players
                    allReady = false;
                    break;
                }
            }
            if (allReady) {

                for (BmTeam g : Bmsts.BmTeams.values()) {
                    g.dropKept();
                    g.setReady(false);
                }

                for (BmPlayer p : Bmsts.BmPlayers.values()) {
                    p.getPlayer().teleport(Bmsts.getCurrentMap().getPlayerSpawn().clone().add(0.5, 0, 0.5));
                }
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        for (BmTeam g : Bmsts.BmTeams.values()) {
                            g.spawnAll(Bmsts.getCurrentMap().getMinionSpawn().get(g));
                            if (g.getReadySwitch().getBlock().getType() == Material.LEVER) {
                                g.getReadySwitch().getBlock().setBlockData(g.getReadySwitch().getBlock().getBlockData().merge(Bukkit.getServer().createBlockData("minecraft:lever[powered=false]")));
                            }
//                        g.showTargets();
                        }
                    }
                }.runTaskLater(plugin, 40);
            }
        }
    }

    public void showTargets() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Minion m : minions) {
                    m.showTargets();
                }
            }
        }.runTaskTimer(plugin,0,5);
    }

    public void spawnAll(Location l) {
        if (minions.size() > 0) {
            dead = false;
        }
        for (Minion m : minions) {
            if (m.getPlacedLoc() != null && spawners.contains(m.getPlacedLoc())) {
                m.spawnGroup(l.clone()); //TODO suffocate
            }
        }
    }

    public void dropKept() {
        int offset = 0;
        for (Minion m : minions) {
            if (m.dropKept(minionItemSpawn.clone().add(0.5 - offset * 2,0,0.5))) {
                offset ++;
            }
        }
    }

    public void dropKeptBy(Player p) {
        int offset = 0;
        for (Minion m : minions) {
            if (m.dropKeptBy(p, minionItemSpawn.clone().add(0.5 - offset * 2,0,0.5))) {
                offset ++;
            }
        }
    }

    public void removeEntities() {
        for (Minion m : minions) {
            m.removeEntities();
        }
    }

    public void removeMinions() {
        for (Minion minion : minions) {
            minion.remove(false);
        }
        minions.clear();
    }

    public void addMinion(Minion m) {
        minions.add(m);
    }

    public void removeMinion(Minion m) {
        minions.remove(m);
    }

    public boolean isDead() {
        return dead;
    }

    public boolean isReady() {
        return ready;
    }

    public Location getPlayerSpawn() {
        return playerSpawn;
    }

    public Location getReadySwitch() {
        return readySwitch;
    }

    public Team getTeam() {
        return team;
    }

    public List<Location> getSpawners() {
        return spawners;
    }

    public Location getRandomizer() {
        return randomizer;
    }

    public Location getTechUpgrade() {
        return techUpgrade;
    }

    public Color getBColor() {
        return bColor;
    }

    public ChatColor getColor() {
        return color;
    }

    public ChatColor getDarkColor() {
        return darkColor;
    }

    public BoundingBox getJoinPortal() {
        return joinPortal;
    }

    public BmTeam copy(String name, Color bColor, ChatColor color, ChatColor darkColor, Main plugin, Vector offset, BoundingBox cjoinPortal) {
        List<Location> copySpawners = new ArrayList<>();
        for (Location l : spawners) {
            copySpawners.add(l.clone().add(offset));
        }
        return new BmTeam(name,bColor,color,darkColor,plugin,playerSpawn.clone().add(offset),randomizer.clone().add(offset),techUpgrade.clone().add(offset),copySpawners,readySwitch.clone().add(offset),minionItemSpawn.clone().add(offset),cjoinPortal);
    }

}
