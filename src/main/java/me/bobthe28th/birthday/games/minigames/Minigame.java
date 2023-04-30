package me.bobthe28th.birthday.games.minigames;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import me.bobthe28th.birthday.Main;
import me.bobthe28th.birthday.games.GamePlayer;
import org.bukkit.*;
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
        //todo
    }

    public void end(MinigamePlayer winner) {
        disable();
        //todo
    }

//    public void end(MinigamePlayer[] winners) {
//        disable();
//        //todo
//    }

    public void endTop3(List<GamePlayer> winners) {
        disable();
        Main.pvp = false;
        status = MinigameStatus.END;
        World w = plugin.getServer().getWorld("world");
        if (w == null) return;
        String[] placeText = new String[]{"first","second","third"};
        ChatColor[] placeColor = new ChatColor[]{ChatColor.GOLD,ChatColor.WHITE,ChatColor.DARK_RED};
        Location[] placeLocation = new Location[]{new Location(w, -138.5, 81, -416.5),new Location(w, -141.5, 79, -416.5),new Location(w, -135.5, 78, -416.5)};
        Vector spectateOffset = new Vector(0,1,3);
        float spectateYaw = 180f;
        Location endLoc = new Location(w,-138.5, 76, -411.5,spectateYaw,0);

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

        PacketContainer packet = new PacketContainer(PacketType.Play.Server.CAMERA);
        packet.getIntegers().write(0,armorStand.getEntityId());

        for (GamePlayer g : plugin.getGamePlayers().values()) {
            g.setCanMove(false);
            g.getPlayer().setInvisible(true);
            g.getPlayer().setVelocity(new Vector(0,0,0));
//            g.getPlayer().teleport(endLoc);
            try {
                ProtocolLibrary.getProtocolManager().sendServerPacket(g.getPlayer(), packet);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        new BukkitRunnable() {
            int p = winners.size() - 1;
            int step = 0;
            @Override
            public void run() {
                if (p < 0) {
                    for (GamePlayer g : plugin.getGamePlayers().values()) {
                        g.setCanMove(true);
                        g.getPlayer().setInvisible(false);
                        PacketContainer packet = new PacketContainer(PacketType.Play.Server.CAMERA);
                        packet.getIntegers().write(0,g.getPlayer().getEntityId());
                        try {
                            ProtocolLibrary.getProtocolManager().sendServerPacket(g.getPlayer(), packet);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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
                            PacketContainer packet = new PacketContainer(PacketType.Play.Server.CAMERA);
                            packet.getIntegers().write(0,winner.getEntityId());
                            try {
                                ProtocolLibrary.getProtocolManager().sendServerPacket(winner, packet);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            winners.get(p).setCanMove(true);
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
