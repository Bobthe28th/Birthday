package me.bobthe28th.birthday.games.minigames.bmsts;

import me.bobthe28th.birthday.Main;
import me.bobthe28th.birthday.games.minigames.bmsts.minions.Minion;
import me.bobthe28th.birthday.games.minigames.bmsts.minions.Rarity;
import me.bobthe28th.birthday.games.minigames.bmsts.minions.t0.SilverfishMinion;
import me.bobthe28th.birthday.scoreboard.ScoreboardTeam;
import org.bukkit.*;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class BmTeam {

    Main plugin;
    Bmsts bmsts;
    ScoreboardTeam team;
    boolean ready = false;
    boolean dead = true;
    boolean doorOpen = false;

    Color bColor;
    ChatColor color;
    ChatColor darkColor;

    //Locations
    Location playerSpawn;
    Location randomizer;
    Location techUpgrade;
    List<Location> spawners = new ArrayList<>();
    List<Location> doorBlocks = new ArrayList<>();
    Location minionItemSpawn;
    Location readySwitch;
    Location dropButton;
    Location researchPointsDisplayLoc;
    BoundingBox joinPortal;

    int researchPoints = 400;
    TextDisplay researchPointsDisplay;

    ArrayList<Minion> minions = new ArrayList<>();

    @SuppressWarnings("deprecation")
    public BmTeam(Bmsts bmsts, String name, Color bColor, ChatColor color, ChatColor darkColor, Main plugin, Location playerSpawn, Location randomizer, Location techUpgrade, List<Location> spawners, Location readySwitch, Location dropButton, Location minionItemSpawn, List<Location> doorBlocks, Location researchPointsDisplayLoc, BoundingBox joinPortal) {
        this.plugin = plugin;
        this.bmsts = bmsts;
        this.playerSpawn = playerSpawn.clone();
        this.randomizer = randomizer.clone();
        this.techUpgrade = techUpgrade.clone();
        for (Location l : spawners) {
            this.spawners.add(l.clone());
        }
        for (Location l : doorBlocks) {
            this.doorBlocks.add(l.clone());
        }
        this.bColor = bColor;
        this.color = color;
        this.darkColor = darkColor;
        this.readySwitch = readySwitch.clone();
        this.dropButton = dropButton.clone();
        this.minionItemSpawn = minionItemSpawn.clone();
        this.joinPortal = joinPortal.clone();
        if (readySwitch.getBlock().getType() == Material.LEVER) {
            readySwitch.getBlock().setBlockData(readySwitch.getBlock().getBlockData().merge(Bukkit.getServer().createBlockData("minecraft:lever[powered=false]")));
        }

        this.researchPointsDisplayLoc = researchPointsDisplayLoc.clone();
        if (researchPointsDisplayLoc.getWorld() != null) {
            this.researchPointsDisplay = researchPointsDisplayLoc.getWorld().spawn(researchPointsDisplayLoc.clone().add(0,0,-0.01), TextDisplay.class);
            this.researchPointsDisplay.setText(color + String.valueOf(researchPoints));
            this.researchPointsDisplay.setBillboard(Display.Billboard.FIXED);
            this.researchPointsDisplay.setBackgroundColor(Color.fromARGB(0,0,0,0));
            this.researchPointsDisplay.setTransformation(new Transformation(new Vector3f(), new AxisAngle4f((float) (Math.PI),0,1,0), new Vector3f(5, 5, 5), new AxisAngle4f()));
        }

        this.team = new ScoreboardTeam(name,ChatColor.DARK_GRAY + "[" + color + Character.toUpperCase(name.charAt(0)) + ChatColor.DARK_GRAY + "] ",false,true, Team.OptionStatus.FOR_OTHER_TEAMS,color);

        new SilverfishMinion(plugin,bmsts, this, Rarity.COMMON,1).drop(minionItemSpawn.clone().add(0.5,0,0.5));
        new SilverfishMinion(plugin,bmsts, this, Rarity.RARE,2).drop(minionItemSpawn.clone().add(-1.5,0,0.5));
        new SilverfishMinion(plugin,bmsts, this, Rarity.GODLIKE,3).drop(minionItemSpawn.clone().add(-3.5,0,0.5));
        new SilverfishMinion(plugin,bmsts, this, Rarity.AWESOME,1).drop(minionItemSpawn.clone().add(-5.5,0,0.5));

    }

    public void remove() {
        this.researchPointsDisplay.remove();
        removeMinions();
        getTeam().remove();
    }

    public String getDisplayName() {
        return Character.toUpperCase(team.getTitle().charAt(0)) + team.getTitle().substring(1) + " team";
    }

    public void updateDoor(BmPlayer p,boolean open) {
        for (Location l : doorBlocks) {
            p.getPlayer().sendBlockChange(l,(open ? Material.AIR.createBlockData() : l.clone().add(0,7,0).getBlock().getBlockData()));
        }
    }

    public int getResearchPoints() {
        return researchPoints;
    }

    public void addResearchPoints(int amount) {
        this.researchPoints += amount;
        this.researchPointsDisplay.setText(color + String.valueOf(this.researchPoints));
    }

    public void minionDeath() { //todo none spawn can only ready when all minions in
        for (Minion m : minions) {
            if (m.getEntities().size() != 0) return;
        }
        dead = true;
        Bukkit.broadcastMessage(ChatColor.GRAY + "[" + team.getColor() + "☠" + ChatColor.GRAY + "] " + team.getColor() + getDisplayName() + " dead");
        for (BmPlayer p : bmsts.getPlayers().values()) {
            p.getPlayer().sendTitle("",team.getColor() + getDisplayName() + " dead",10,20,10);
        }
        bmsts.checkBattleWin();
    }

    public ArrayList<BmPlayer> getMembers() {
        ArrayList<BmPlayer> members = new ArrayList<>();
        for (BmPlayer p : bmsts.getPlayers().values()) {
            if (p.getTeam() == this) {
                members.add(p);
            }
        }
        return members;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
        this.doorOpen = ready; //todo teleport if outside
        for (BmPlayer p : getMembers()) {
            updateDoor(p,ready);
        }
        if (ready) {
            dropKept();
            boolean allReady = true;
            Bukkit.broadcastMessage(ChatColor.GRAY + "[" + team.getColor() + "✔" + ChatColor.GRAY + "] " + team.getColor() + getDisplayName() + " ready");
            for (BmPlayer p : bmsts.getPlayers().values()) {
                p.getPlayer().sendTitle("", team.getColor() + getDisplayName() + " ready", 10, 20, 10);
            }
            for (BmTeam g : bmsts.getTeams().values()) {
                if (!g.isReady() && g.getMembers().size() > 0) {
                    allReady = false;
                    break;
                }
            }
            if (allReady) {
                bmsts.startBattle();
            }
        }
    }

//    public void showTargets() {
//        new BukkitRunnable() {
//            @Override
//            public void run() {
//                for (Minion m : minions) {
//                    m.showTargets();
//                }
//            }
//        }.runTaskTimer(plugin,0,5);
//    }

    public void spawnAll(Location l) {
        dead = true;
        for (Minion m : minions) {
            if (m.getPlacedLoc() != null && spawners.contains(m.getPlacedLoc())) {
                dead = false;
                m.spawnGroup(l.clone()); //TODO suffocate
            }
        }
    }

    public void dropAll() {
        int offset = 0;
        for (Minion m : minions) {
            if (!m.dropKept(minionItemSpawn.clone().add(0.5 - offset * 2,0,0.5))) {
                m.drop(minionItemSpawn.clone().add(0.5 - offset * 2, 0, 0.5));
            }
            offset ++;
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

    public Location getDropButton() {
        return dropButton;
    }

    public ScoreboardTeam getTeam() {
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

//    public Color getBColor() {
//        return bColor;
//    }

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
        List<Location> cloneDoorBlocks = new ArrayList<>();
        for (Location l : doorBlocks) {
            cloneDoorBlocks.add(l.clone().add(offset));
        }
        return new BmTeam(bmsts,name,bColor,color,darkColor,plugin,playerSpawn.clone().add(offset),randomizer.clone().add(offset),techUpgrade.clone().add(offset),copySpawners,readySwitch.clone().add(offset),dropButton.clone().add(offset),minionItemSpawn.clone().add(offset),cloneDoorBlocks,researchPointsDisplayLoc.clone().add(offset),cjoinPortal);
    }

}
