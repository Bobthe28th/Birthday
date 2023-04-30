package me.bobthe28th.birthday.scoreboard;

import me.bobthe28th.birthday.games.GamePlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.HashMap;

public class ScoreboardTeam {

    String title;
    HashMap<ScoreboardController, Team> teams = new HashMap<>();
    ArrayList<Entity> globalMembers = new ArrayList<>();
    boolean friendlyFire = true;
    boolean seeInvisibles = false;
    Team.OptionStatus nameTag = Team.OptionStatus.ALWAYS;

    ChatColor color = ChatColor.WHITE;

    public ScoreboardTeam(String title) {
        this.title = title;
    }

    public ScoreboardTeam(String title, boolean friendlyFire, boolean seeInvisibles, Team.OptionStatus nameTag, ChatColor color) {
        this.title = title;
        this.friendlyFire = friendlyFire;
        this.seeInvisibles = seeInvisibles;
        this.nameTag = nameTag;
        this.color = color;
    }

    public Team getTeam(GamePlayer p) {
        return teams.get(p.getScoreboardController());
    }

    public Team getTeam(ScoreboardController p) {
        return teams.get(p);
    }

    public void addPlayer(ScoreboardController p) {
        Team t = p.getScoreboard().registerNewTeam(title);
        t.setAllowFriendlyFire(friendlyFire);
        t.setCanSeeFriendlyInvisibles(seeInvisibles);
        t.setOption(Team.Option.NAME_TAG_VISIBILITY,nameTag);
        t.setColor(color);
        teams.put(p,t);
        for (Entity e : globalMembers) {
            if (e instanceof Player pl) {
                t.addEntry(pl.getName());
            } else {
                t.addEntry(e.getUniqueId().toString());
            }
        }
    }

    public void addMember(Entity member, ScoreboardController p) {
        teams.get(p).addEntry(member.getUniqueId().toString());
    }

    public void removeMember(Entity member, ScoreboardController p) {
        teams.get(p).removeEntry(member.getUniqueId().toString());
    }

    public void addMemberGlobal(Entity member) {
        if (!globalMembers.contains(member)) globalMembers.add(member);
        for (Team t : teams.values()) {
            if (member instanceof Player p) {
                t.addEntry(p.getName());
            } else {
                t.addEntry(member.getUniqueId().toString());
            }
        }
    }

    public void removeMemberGlobal(Entity member) {
        globalMembers.remove(member);
        for (Team t : teams.values()) {
            if (member instanceof Player p) {
                t.removeEntry(p.getName());
            } else {
                t.removeEntry(member.getUniqueId().toString());
            }
        }
    }

    public void remove() {
        for (ScoreboardController p : teams.keySet()) {
            p.removeTeam(this);
        }
    }

    public void removePlayer(ScoreboardController p) {
        teams.get(p).unregister();
        teams.remove(p);
    }
}
