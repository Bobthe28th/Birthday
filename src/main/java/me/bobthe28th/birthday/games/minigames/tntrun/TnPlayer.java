package me.bobthe28th.birthday.games.minigames.tntrun;

import me.bobthe28th.birthday.Main;
import me.bobthe28th.birthday.games.GamePlayer;
import me.bobthe28th.birthday.games.minigames.MinigamePlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;

public class TnPlayer extends MinigamePlayer {

    TntRun tntRun;

    public TnPlayer(Main plugin, GamePlayer player, TntRun tntRun) {
        super(plugin,player,tntRun);
        this.tntRun = tntRun;
        Main.gameController.giveAdvancement(player.getPlayer(),"tntrun");
        player.getPlayer().setGameMode(GameMode.SURVIVAL);
    }

    public void remove() {
        player.getPlayer().setHealth(20.0);
    }

    public void death() {
        alive = false;
        player.getPlayer().setGameMode(GameMode.SPECTATOR);
        player.getPlayer().getInventory().clear();
        ChatColor c = ChatColor.RED;
        if (tntRun.isBonusRound) {
            c = tntRun.bmsts.getTeamColor(player.getPlayer(),ChatColor.RED);
        }
        Bukkit.broadcastMessage(ChatColor.GRAY + "[" + c + "☠" + ChatColor.GRAY + "] " + c + player.getPlayer().getDisplayName() + ChatColor.GRAY +  " died");
    }
}
