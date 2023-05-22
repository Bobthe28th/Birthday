package me.bobthe28th.birthday.commands;

import me.bobthe28th.birthday.Main;
import me.bobthe28th.birthday.music.Music;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;

public class BCommands implements CommandExecutor {

    Main plugin;
    public BCommands(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {

        if (!(sender instanceof Player player)) {
            return true;
        }

//        if (!player.getName().equals("Bobthe29th")) return true;

        switch (cmd.getName().toLowerCase()) {
            case "join":
                if (!plugin.getGamePlayers().containsKey(player)) {
                    Main.gameController.addNewPlayer(player);
                    Main.gameController.playerJoin(plugin.getGamePlayers().get(player));
                    player.sendMessage(ChatColor.GREEN + "Joined!");
                } else {
                    player.sendMessage(ChatColor.RED + "You are already joined.");
                }
                return true;
            case "test":

                Main.musicController.getQueue().clearQueue();
                String n = "battle" + (new Random().nextInt(10) + 1);
                Bukkit.broadcastMessage(n);
                Main.musicController.getQueue().addLoopQueue(Main.musicController.getMusicByName(n));
                Main.musicController.start();
 //
//
//                PacketContainer packet = new PacketContainer(PacketType.Play.Server.CAMERA);
//
//                packet.getIntegers().write(0,armorStand.getEntityId());

//                PacketContainer packet = new PacketContainer(PacketType.Play.Server.GAME_STATE_CHANGE);
//                packet.get
                        //.write(0, new byte[]{5});
//                packet.getIntegers().write(0,5);
//                packet.getBytes().write(0,(byte)5);
//                packet.getFloat().write(0,102f);
//                packet.getIntegerArrays().write(0,new int[]{5});

//                ClientboundGameEventPacket packet = new ClientboundGameEventPacket(ClientboundGameEventPacket.DEMO_EVENT,0f);


//                ((CraftPlayer)player).getHandle().connection.send(packet);

//                try {
//                    ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
                return true;
            case "pvp":
                Main.pvp = !Main.pvp;
                player.sendMessage(Main.pvp ? ChatColor.GREEN + "PVP Enabled" : ChatColor.RED + "PVP Disabled");
                return true;
            case "start":
                if (args.length == 0) {
                    player.sendMessage(ChatColor.RED + "Provide a minigame");
                } else if (Main.gameController.minigames.containsKey(args[0])) {
                    Main.gameController.setMinigame(Main.gameController.minigames.get(args[0]),plugin);
                    player.sendMessage(ChatColor.GREEN + "Starting " + args[0]);
                } else {
                    player.sendMessage(ChatColor.RED + "Minigame not found.");
                }

                return true;
            case "music":
                if (args.length == 0) {
                    player.sendMessage(ChatColor.RED + "Provide an action");
                } else {
                    switch (args[0].toLowerCase()) {
                        case "clear":
                            Main.musicController.getQueue().clearQueue();
                            player.sendMessage(ChatColor.GREEN + "Cleared queue");
                            break;
                        case "start":
                            Main.musicController.start();
                            player.sendMessage(ChatColor.GREEN + "Started music");
                            break;
                        case "stop":
                            Main.musicController.stopCurrent();
                            player.sendMessage(ChatColor.GREEN + "Stopped playing current music");
                            break;
                        case "skip":
                            Main.musicController.playNext();
                            player.sendMessage(ChatColor.GREEN + "Skipped music");
                            break;
                        case "list":
                            List<String> q = Main.musicController.getQueue().getQueueName();
                            List<String> l = Main.musicController.getQueue().getLoopQueueName();
                            String c = Main.musicController.getCurrentlyPlayingName();
                            String currently = (c == null ? "Nothing is playing!\n\n" : "Currently playing: " + c + "\n\n");
                            if (q.size() == 0 && l.size() == 0) {
                                player.sendMessage(currently + "The queue is empty!");
                                break;
                            } else {
                                StringBuilder qList = new StringBuilder("List queue:\n");
                                if (q.size() == 0) {
                                    qList = new StringBuilder("List queue empty.\n");
                                } else {
                                    for (String qn : q) {
                                        qList.append(qn).append("\n");
                                    }
                                }
                                StringBuilder lList = new StringBuilder("Loop queue:\n");
                                if (l.size() == 0) {
                                    lList = new StringBuilder("Loop queue empty.\n");
                                } else {
                                    for (String ln : l) {
                                        lList.append(ln).append("\n");
                                    }
                                }
                                player.sendMessage(currently + "Queue:\n" + qList + lList);
                            }
                            break;
                        case "play":
                            if (args[1] == null) {
                                player.sendMessage(ChatColor.RED + "Provide an play action");
                                break;
                            }
                            switch (args[1].toLowerCase()) {
                                case "now":
                                    if (args[2] != null) {
                                        Music m = Main.musicController.getMusicByName(args[2]);
                                        if (m != null) {
                                            Main.musicController.getQueue().addQueueStart(m);
                                            Main.musicController.playNext();
                                            player.sendMessage(ChatColor.RED + "Playing " + m.getName());
                                        } else {
                                            player.sendMessage(ChatColor.RED + "Invalid music");
                                        }
                                    } else {
                                        player.sendMessage(ChatColor.RED + "Provide music name");
                                    }
                                    break;
                                case "queue":
                                    if (args[2] == null) {
                                        player.sendMessage(ChatColor.RED + "Provide a queue action");
                                        break;
                                    }
                                    Music m;
                                    if (args[3] != null) {
                                        m = Main.musicController.getMusicByName(args[3]);
                                    } else {
                                        player.sendMessage(ChatColor.RED + "Provide music name");
                                        break;
                                    }
                                    if (m == null) {
                                        player.sendMessage(ChatColor.RED + "Invalid music");
                                        break;
                                    }
                                    switch (args[2].toLowerCase()) {
                                        case "list" -> Main.musicController.getQueue().addQueue(m);
                                        case "loop" -> Main.musicController.getQueue().addLoopQueue(m);
                                    }
                                    player.sendMessage(ChatColor.GREEN + "Added " + m.getName() + " to the queue");
                                    break;
                            }
                    }
                }
                return true;
        }

        return false;
    }

}
