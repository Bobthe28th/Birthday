//package me.bobthe28th.birthday.games.bmsts.bonusrounds.ghosts;
//
//import com.comphenix.protocol.PacketType;
//import com.comphenix.protocol.ProtocolLibrary;
//import com.comphenix.protocol.events.PacketContainer;
//import me.bobthe28th.birthday.Main;
//import me.bobthe28th.birthday.games.bmsts.BmPlayer;
//import me.bobthe28th.birthday.games.bmsts.Bmsts;
//import me.bobthe28th.birthday.games.bmsts.bonusrounds.BonusRoundOld;
//import me.bobthe28th.birthday.games.bmsts.bonusrounds.BonusRoundMap;
//import net.md_5.bungee.api.ChatMessageType;
//import net.md_5.bungee.api.chat.TextComponent;
//import org.bukkit.ChatColor;
//import org.bukkit.Location;
//import org.bukkit.World;
//import org.bukkit.attribute.Attribute;
//import org.bukkit.block.Block;
//import org.bukkit.block.BlockFace;
//import org.bukkit.entity.Husk;
//import org.bukkit.entity.Player;
//import org.bukkit.event.Listener;
//import org.bukkit.scheduler.BukkitRunnable;
//import org.bukkit.scheduler.BukkitTask;
//import org.bukkit.util.BlockIterator;
//import org.bukkit.util.BoundingBox;
//import org.bukkit.util.Vector;
//
//import java.util.*;
//
//public class GhostsRoundOld extends BonusRoundOld implements Listener {
//
//    Main plugin;
//
//    HashMap<Player, GhostPlayerOld> players = new HashMap<>();
//
//    ArrayList<Husk> ghosts = new ArrayList<>();
//
//    BukkitTask spawn;
//    BukkitTask timer;
//
//    BoundingBox spawnZone;
//    int time = 90;
//
//    public GhostsRoundOld(Main plugin) {
//        super(new BonusRoundMap(new Location(plugin.getServer().getWorld("world"),-215, 99, -355)));
//        this.plugin = plugin;
//        plugin.getServer().getPluginManager().registerEvents(this, plugin);
//        spawnZone = new BoundingBox(-228, 92, -368,-201, 97, -341);
//    }
//
//    @Override
//    public void start() {
//        World w = plugin.getServer().getWorld("world");
//
//        if (w != null) {
//            running = true;
//
//            for (BmPlayer p : Bmsts.BmPlayers.values()) {
//                p.getPlayer().teleport(getSpawnLoc(w));
//                players.put(p.getPlayer(),new GhostPlayerOld(p));
//            }
//
//            spawn = new BukkitRunnable() {
//                @Override
//                public void run() {
//                    if (ghosts.size() < 30 && !this.isCancelled()) {
//                        Husk ghost = w.spawn(getSpawnLoc(w), Husk.class);
//                        ghost.setInvulnerable(true);
//                        ghost.setInvisible(true);
//                        ghost.setAdult();
//                        ghost.setSilent(true);
//                        Objects.requireNonNull(ghost.getAttribute(Attribute.GENERIC_FOLLOW_RANGE)).setBaseValue(5);
//                        Objects.requireNonNull(ghost.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)).setBaseValue(10);
//                        ghosts.add(ghost);
//                        updateTeam(ghost);
//                    } else {
//                        this.cancel();
//                    }
//                }
//            }.runTaskTimer(plugin,20,40);
//
//            timer = new BukkitRunnable() {
//                @Override
//                public void run() {
//                    if (!this.isCancelled()) {
//                        for (BmPlayer p : Bmsts.BmPlayers.values()) {
//                            p.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + "" + time));
//                        }
//                        if (time <= 0) {
//                            this.cancel();
//                            for (GhostPlayerOld p : players.values()) {
//                                if (p.isAlive()) {
//                                    p.getPlayer().getTeam().addResearchPoints(5);
//                                }
//                            }
//                            end(true);
//                        }
//                        time--;
//                    }
//                }
//            }.runTaskTimer(plugin, 0, 20);
//        }
//    }
//
//    public void updateTeam(Husk ghost) {
//        for (BmPlayer p : Bmsts.BmPlayers.values()) {
//            if (p.getTeam() != null) {
//                PacketContainer packet = new PacketContainer(PacketType.Play.Server.SCOREBOARD_TEAM);
//                packet.getModifier().write(0, 3).write(1, p.getTeam().getTeam().getName()).write(2, Collections.singletonList(ghost.getUniqueId().toString()));
//                try {
//                    ProtocolLibrary.getProtocolManager().sendServerPacket(p.getPlayer(), packet); //todo if scoreboard then just use the players scoreboard
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//
//    public void updateTeam(Player p) {
//
//    }
//
//    public Location getSpawnLoc(World w) {
//        int maxAttempts = 20;
//
//        Random random = new Random();
//        for (int i = 0; i < maxAttempts; i++) {
//            Location l = getPossibleSpawnLoc(w,true,random);
//            if (l != null) {
//                return l;
//            }
//        }
//        return getPossibleSpawnLoc(w,false,random);
//    }
//
//    public Location getPossibleSpawnLoc(World w, boolean checkPlayerDist, Random random) {
//
//        Vector start = new Vector(random.nextInt((int) spawnZone.getWidthX()) + spawnZone.getMinX(), spawnZone.getMaxY(), random.nextInt((int) spawnZone.getWidthZ()) + spawnZone.getMinZ());
//
//        BlockIterator blockIterator = new BlockIterator(w, start, new Vector(0, -1, 0), 0, (int) spawnZone.getHeight());
//        while (blockIterator.hasNext()) {
//            Block b = blockIterator.next();
//            if (b.isEmpty() && !b.getRelative(BlockFace.DOWN).isEmpty()) {
//                double distToPlayerSqared = Integer.MAX_VALUE;
//                for (GhostPlayerOld p : players.values()) {
//                    if (p.isAlive()) {
//                        double pDist = b.getLocation().distanceSquared(p.getPlayer().getPlayer().getLocation());
//                        if (distToPlayerSqared > pDist) {
//                            distToPlayerSqared = pDist;
//                        }
//                    }
//                }
//                if (!checkPlayerDist || distToPlayerSqared >= 25) {
//                    return b.getLocation().add(0.5,0,0.5);
//                } else {
//                    break;
//                }
//            }
//        }
//        return null;
//    }
//
//    @Override
//    public void endRound() {
//        spawn.cancel();
//        for (Husk h : ghosts) {
//            h.remove();
//        }
//    }
//}
