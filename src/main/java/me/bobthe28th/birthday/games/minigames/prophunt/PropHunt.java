package me.bobthe28th.birthday.games.minigames.prophunt;

import me.bobthe28th.birthday.Main;
import me.bobthe28th.birthday.games.GamePlayer;
import me.bobthe28th.birthday.games.minigames.Minigame;
import me.bobthe28th.birthday.games.minigames.MinigameStatus;
import me.bobthe28th.birthday.games.minigames.bmsts.Bmsts;
import me.bobthe28th.birthday.games.minigames.bmsts.bonusrounds.BonusRound;
import org.bukkit.World;
import org.bukkit.event.Listener;

public class PropHunt extends Minigame implements Listener, BonusRound {

    public PropHunt(Main plugin) {
        super(plugin);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        status = MinigameStatus.READY;
        World w = plugin.getServer().getWorld("world");

    }

    @Override
    public void start() {

    }

    @Override
    public void disable() {

    }

    @Override
    public void onPlayerJoin(GamePlayer player) {

    }

    @Override
    public void onPlayerLeave(GamePlayer player) {

    }

    @Override
    public void startBonusRound(Bmsts bmsts) {

    }

    @Override
    public void endBonusRound(boolean points) {

    }
}
