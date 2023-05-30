package me.bobthe28th.birthday.games.minigames.ctfsmall;

import me.bobthe28th.birthday.DamageRule;
import me.bobthe28th.birthday.Main;
import me.bobthe28th.birthday.games.GamePlayer;
import me.bobthe28th.birthday.games.minigames.MinigameStatus;
import me.bobthe28th.birthday.games.minigames.bmsts.BmTeam;
import me.bobthe28th.birthday.games.minigames.bmsts.bonusrounds.BonusRound;
import me.bobthe28th.birthday.scoreboard.ScoreboardTeam;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class CtfSmall extends BonusRound {

    HashMap<Player, CtPlayer> players = new HashMap<>();

    BukkitTask timerTask;

    int time = 90;
    CtMap map;

    ScoreboardTeam[] teams = new ScoreboardTeam[4];

    public CtfSmall(Main plugin) {
        super(plugin);
        World w = plugin.getServer().getWorld("world");
        Location[] teamSpawns = new Location[]{
            new Location(w,-258.5, 99, -475.5),
            new Location(w,-302.5, 99, -475.5),
            new Location(w,-280.5, 99, -497.5),
            new Location(w,-280.5, 99, -453.5)
        };
        map = new CtMap("ctf",w,new Location(w,-280.5, 105, -475.5),teamSpawns);
    }

    @Override
    public void start() {
        status = MinigameStatus.PLAYING;
        Main.damageRule = DamageRule.ALL;
        Main.breakBlocks = false;

        for (GamePlayer p : plugin.getGamePlayers().values()) {
            players.put(p.getPlayer(),new CtPlayer(plugin, p, this));
        }

        if (!isBonusRound) {
            teams[0] = new ScoreboardTeam("blue",false,true, Team.OptionStatus.FOR_OTHER_TEAMS, ChatColor.BLUE);
            teams[1] = new ScoreboardTeam("red",false,true, Team.OptionStatus.FOR_OTHER_TEAMS, ChatColor.RED);
            teams[2] = new ScoreboardTeam("green",false,true, Team.OptionStatus.FOR_OTHER_TEAMS, ChatColor.GREEN);
            teams[3] = new ScoreboardTeam("yellow",false,true, Team.OptionStatus.FOR_OTHER_TEAMS, ChatColor.YELLOW);

            List<Player> randomPlayers = new ArrayList<>(players.keySet());
            Collections.shuffle(randomPlayers);

            int playersPerTeam = (int) Math.ceil(players.size() / 4.0); //todo this probably doesnt work
            int t = 0;

            for (int i = 0; i < randomPlayers.size(); i++) {

                for (ScoreboardTeam team : teams) {
                    team.addPlayer(players.get(randomPlayers.get(i)).getGamePlayer().getScoreboardController());
                }

                if (i*t < playersPerTeam) {
                    teams[Math.min(t,3)].addMemberGlobal(randomPlayers.get(i));
                } else {
                    t ++;
                    playersPerTeam = (int) Math.floor((players.size() - i) / (4.0-t));
                }
            }
        } else {
            int i = 0;
            for (BmTeam t : bmsts.getTeams().values()) {
                teams[Math.min(3,t.getId())] = t.getTeam();
                i ++;
                if (i > 3) {
                    break;
                }
            }
        }

        for (CtPlayer p : players.values()) {
            p.getPlayer().teleport(map.getTeamSpawnLoc(getTeam(p)));
        }
    }

    public ScoreboardTeam[] getTeams() {
        return teams;
    }

    public int getTeam(CtPlayer p) {
        for (int i = 0; i < teams.length; i ++) {
            if (teams[i].getGlobalMembers().contains(p.getPlayer())) {
                return i;
            }
        }
        return 0;
    }

    @Override
    public void disable() {
        if (!isBonusRound) {
            for (CtPlayer p : players.values()) {
                p.remove();
            }
            players.clear();
            for (ScoreboardTeam t : teams) {
                t.remove();
            }
        }
    }

    @Override
    public void onPlayerJoin(GamePlayer player) {
        players.put(player.getPlayer(),new CtPlayer(plugin, player, this));
        int leastPlayers = 0;
        for (int i = 0; i < teams.length; i ++) {
            if (teams[i].getGlobalMembers().size() < teams[leastPlayers].getGlobalMembers().size()) {
                leastPlayers = i;
            }
        }
        teams[leastPlayers].addMemberGlobal(player.getPlayer());
        player.getPlayer().teleport(map.getTeamSpawnLoc(leastPlayers));
    }

    @Override
    public void onPlayerLeave(GamePlayer player) {
        if (players.containsKey(player.getPlayer())) {
            players.get(player.getPlayer()).remove();
            players.remove(player.getPlayer());
        }
    }

    @Override
    public void awardPoints() {

    }
}
