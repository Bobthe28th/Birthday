package me.bobthe28th.birthday.games.minigames;

import me.bobthe28th.birthday.DamageRule;
import me.bobthe28th.birthday.Main;
import me.bobthe28th.birthday.MoveOption;
import me.bobthe28th.birthday.games.GamePlayer;
import net.minecraft.network.protocol.game.ClientboundSetCameraPacket;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;

public abstract class Minigame implements Listener {

    public MinigameStatus status = MinigameStatus.WAITING;
    public boolean isBonusRound = false;
    public Main plugin;

    public Minigame(Main plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public abstract void start();

    public void end() {
        disable();
        Main.damageRule = DamageRule.NONE;
        Main.breakBlocks = false;
        status = MinigameStatus.END;
        //todo
    }

    public void end(MinigamePlayer winner) {
        end();
        //todo
    }

//    public void end(MinigamePlayer[] winners) {
//        disable();
//        //todo
//    }

    public void endTop3(List<GamePlayer> winners) {
        end();
        World w = plugin.getServer().getWorld("world");
        if (w == null) return;
        String[] placeText = new String[]{"first","second","third"};
        ChatColor[] placeColor = new ChatColor[]{ChatColor.GOLD,ChatColor.WHITE,ChatColor.DARK_RED};
        Location[] placeLocation = new Location[]{new Location(w, -196, 98, -407),new Location(w, -196, 97, -405),new Location(w, -196, 96, -409)};
        Vector spectateOffset = new Vector(3,1,0);
        float spectateYaw = 90f;
        Location endLoc = new Location(w,-192, 95, -407,spectateYaw,0);

        for (GamePlayer g : plugin.getGamePlayers().values()) {
            g.getPlayer().getInventory().clear();
            g.getPlayer().setGameMode(GameMode.SPECTATOR);
            g.getPlayer().teleport(endLoc);
        }

        Location l = placeLocation[0].clone().add(spectateOffset);
        l.setYaw(spectateYaw);
        ArmorStand armorStand = w.spawn(l, ArmorStand.class);
        armorStand.setMarker(true);
        armorStand.setGravity(false);
        armorStand.setVisible(false);

        ClientboundSetCameraPacket packet = new ClientboundSetCameraPacket(((CraftEntity) armorStand).getHandle());

        for (GamePlayer g : plugin.getGamePlayers().values()) {
            g.setMoveOption(MoveOption.NONE);
            g.getPlayer().setInvisible(true);
            g.getPlayer().setVelocity(new Vector(0,0,0));
//            g.getPlayer().teleport(endLoc);
            ((CraftPlayer)g.getPlayer()).getHandle().connection.send(packet);
        }

        new BukkitRunnable() {
            int p = winners.size() - 1;
            int step = 0;
            @Override
            public void run() {
                if (p < 0) {
                    for (GamePlayer g : plugin.getGamePlayers().values()) {
                        g.setMoveOption(MoveOption.ALL);
                        g.getPlayer().setInvisible(false);
                        ClientboundSetCameraPacket packet = new ClientboundSetCameraPacket(((CraftPlayer)g.getPlayer()).getHandle());
                        ((CraftPlayer)g.getPlayer()).getHandle().connection.send(packet);
                        g.getPlayer().setGameMode(GameMode.ADVENTURE);
                    }
                    armorStand.remove();
                    this.cancel();
                }
                if (!this.isCancelled()) {

                    switch (step) {
                        case 0 -> {
                            Location l = placeLocation[p].clone().add(spectateOffset);
                            l.setYaw(spectateYaw);
                            armorStand.teleport(l);
                            Bukkit.broadcastMessage(placeColor[p] + "In " + placeText[p] + " place...");
                        }
                        case 1 -> {
                            Player winner = winners.get(p).getPlayer();
                            Bukkit.broadcastMessage(placeColor[p] + winner.getDisplayName() + "!");
                            winner.setGameMode(GameMode.ADVENTURE);
                            winner.setInvisible(false);
                            Location l = placeLocation[p].clone();
                            l.setYaw(spectateYaw - 180);
                            winner.teleport(l);
                            if (winner.getPlayer() != null) {
                                ClientboundSetCameraPacket packet = new ClientboundSetCameraPacket(((CraftPlayer)winner.getPlayer()).getHandle());
                                ((CraftPlayer) winner.getPlayer()).getHandle().connection.send(packet);
                            }
                            winners.get(p).setMoveOption(MoveOption.VERTICAL);
                        }
                    }

                    step ++;
                    if (step >= 2) {
                        p --;
                        step = 0;
                    }
                }
            }
        }.runTaskTimer(plugin,0L, 60L);
    }

    public abstract void disable();

    public abstract void onPlayerJoin(GamePlayer player);

    public abstract void onPlayerLeave(GamePlayer player);

}
