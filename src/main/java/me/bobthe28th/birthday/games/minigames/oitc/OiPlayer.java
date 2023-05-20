package me.bobthe28th.birthday.games.minigames.oitc;

import me.bobthe28th.birthday.Main;
import me.bobthe28th.birthday.games.GamePlayer;
import me.bobthe28th.birthday.games.minigames.MinigamePlayer;
import me.bobthe28th.birthday.games.minigames.MinigameStatus;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.CrossbowMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class OiPlayer extends MinigamePlayer {

    Oitc oitc;
    boolean king = false;
    int points = 0;
    int kills = 0;
    int deaths = 0;

    public OiPlayer(GamePlayer player, Main plugin, Oitc oitc) {
        super(plugin,player,oitc);
        this.oitc = oitc;
        player.getScoreboardController().addSetObjective(oitc.getObjective());
        player.getScoreboardController().addTeam(oitc.getGTeam());
        player.getScoreboardController().addTeam(oitc.getKTeam());
        if (!oitc.isBonusRound) {
            oitc.getGTeam().addMemberGlobal(player.getPlayer());
        }
        oitc.updateTopPoints(this);
    }

    public void respawn() {
        Objects.requireNonNull(player.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(1.0);
        player.getPlayer().setHealth(0.1);
        giveItems();
        alive = true;

        player.getPlayer().teleport(oitc.getCurrentMap().getSpawnLoc(new ArrayList<>(oitc.getOiPlayers().values())));
        player.getPlayer().setGameMode(GameMode.ADVENTURE);
    }

    public void remove() {
        Objects.requireNonNull(player.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(20.0);
        player.getPlayer().setHealth(20.0);
        player.getPlayer().setGlowing(false);
        player.getPlayer().getInventory().clear();
        player.getScoreboardController().removeObjective(oitc.getObjective());
        player.getScoreboardController().removeTeam(oitc.getGTeam());
        player.getScoreboardController().removeTeam(oitc.getKTeam());
        oitc.getGTeam().removeMemberGlobal(player.getPlayer());
        oitc.getKTeam().removeMemberGlobal(player.getPlayer());
    }
    
    public void giveItems() {
        PlayerInventory inventory = player.getPlayer().getInventory();
        inventory.clear();
        if (oitc.cross) {
            ItemStack crossbow = new ItemStack(Material.CROSSBOW);
            CrossbowMeta meta = (CrossbowMeta) crossbow.getItemMeta();
            if (meta != null) {
                meta.setUnbreakable(true);
//                meta.addEnchant(Enchantment.QUICK_CHARGE,3,true);
                meta.addChargedProjectile(new ItemStack(Material.ARROW));
                meta.setLore(List.of("Reloads an arrow on kill"));

            }
            crossbow.setItemMeta(meta);
            inventory.setItem(3,crossbow);
        } else {
            ItemStack bow = new ItemStack(Material.BOW);
            ItemMeta meta = bow.getItemMeta();
            if (meta != null) {
                meta.setUnbreakable(true);
                meta.setLore(List.of("Gains an arrow on kill"));
            }
            bow.setItemMeta(meta);
            inventory.setItem(3,bow);
            inventory.setItem(5,new ItemStack(Material.ARROW));
        }
        player.getPlayer().getInventory().setHeldItemSlot(3);
        player.getPlayer().updateInventory();
    }
    
    public void giveKingItem() {
        PlayerInventory inventory = player.getPlayer().getInventory();
        if (oitc.cross) {
            ItemStack crossbow = inventory.getItem(3);
            if (crossbow != null && crossbow.getType() == Material.CROSSBOW) {
                CrossbowMeta meta = (CrossbowMeta) crossbow.getItemMeta();
                if (meta != null) {
                    meta.addEnchant(Enchantment.MULTISHOT, 3, true);
                    meta.setChargedProjectiles(Arrays.asList(oitc.firework.clone(),oitc.firework.clone(),oitc.firework.clone()));
                    meta.setLore(List.of("Reloads a firework on kill"));

                }
                crossbow.setItemMeta(meta);
                inventory.setItem(3, crossbow);
                player.getPlayer().getInventory().setHeldItemSlot(3);
            }
        } else {
            ItemStack crossbow = new ItemStack(Material.CROSSBOW);
            if (crossbow.getType() == Material.CROSSBOW) {
                CrossbowMeta meta = (CrossbowMeta) crossbow.getItemMeta();
                if (meta != null) {
                    meta.addEnchant(Enchantment.MULTISHOT, 3, true);
                    meta.setChargedProjectiles(Arrays.asList(oitc.firework.clone(),oitc.firework.clone(),oitc.firework.clone()));
                    meta.setLore(List.of("Reloads a firework on kill"));

                }
                crossbow.setItemMeta(meta);
                inventory.setItem(4, crossbow);
                player.getPlayer().getInventory().setHeldItemSlot(4);
            }
        }
        
        player.getPlayer().updateInventory();
    }

    public void giveArrow(boolean pickup, int amount) {
        PlayerInventory inventory = player.getPlayer().getInventory();
        ItemStack arrows = inventory.getItem(5);
        if (arrows == null || arrows.getType() != Material.ARROW) {
            inventory.setItem(5,new ItemStack(Material.ARROW));
        } else {
            arrows.setAmount(arrows.getAmount() + amount);
        }
        player.getPlayer().updateInventory();
        if (pickup) {
            player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, SoundCategory.MASTER, 1.0f, 1.0f);
        }
    }

    public void giveFirework(int amount) {
        PlayerInventory inventory = player.getPlayer().getInventory();
        ItemStack fireworks = inventory.getItemInOffHand();
        if (fireworks.getType() != Material.FIREWORK_ROCKET) {
            inventory.setItem(EquipmentSlot.OFF_HAND,oitc.firework.clone());
        } else {
            fireworks.setAmount(fireworks.getAmount() + amount);
        }
        player.getPlayer().updateInventory();
    }

    public void kill(Player killed) {
        if (oitc.cross) {
            PlayerInventory inventory = player.getPlayer().getInventory();
            ItemStack crossbow = inventory.getItem(3);
            if (crossbow != null && crossbow.getType() == Material.CROSSBOW) {
                CrossbowMeta meta = (CrossbowMeta) crossbow.getItemMeta();
                if (meta != null) {
                    if (meta.hasChargedProjectiles()) {
                        if (king) {
                            giveFirework(1);
                        } else {
                            giveArrow(false, 1);
                        }
                    } else {
                        if (king) {
                            meta.setChargedProjectiles(Arrays.asList(oitc.firework.clone(),oitc.firework.clone(),oitc.firework.clone()));
                        } else {
                            meta.addChargedProjectile(new ItemStack(Material.ARROW));
                        }
                    }
                }
                crossbow.setItemMeta(meta);
            }
        } else {
            if (king) {
                giveFirework(1);
            } else {
                giveArrow(false, 1);
            }
        }
        player.getPlayer().sendTitle("",ChatColor.RED + "☠",0,10,10);
        kills ++;
        points ++;
        oitc.updateTopPoints(this);

        oitc.getObjective().updateRow(2,"Kills: " + kills, player);
        oitc.getObjective().updateRow(3, "Points: " + points, player);

        if (points >= oitc.maxKills && !king) {
            oitc.setKing(this);
        }
        if (points >= oitc.maxKills + oitc.killsPostMax) {
            player.getPlayer().setGlowing(false);
            if (oitc.isBonusRound) {
                oitc.endBonusRound(true);
            } else {
                List<GamePlayer> winners = new ArrayList<>();
                for (int i = 0; i < Math.min(3,oitc.topPoints.size()); i++) {
                    winners.add(oitc.topPoints.get(i).getGamePlayer());
                }
                oitc.endTop3(winners);
            }
        }
    }

    public void death(Player killer) {
        alive = false;
        deaths ++;
        if (oitc.status == MinigameStatus.PLAYING) {
            oitc.getObjective().updateRow(1, "Deaths: " + deaths, player);
            player.getPlayer().setGameMode(GameMode.SPECTATOR);
            player.getPlayer().getInventory().clear();
            if (king) {
                oitc.kingDeath(this);
            }
            new BukkitRunnable() {
                int time = 3;
                final ChatColor[] timeColors = new ChatColor[]{ChatColor.GREEN, ChatColor.YELLOW, ChatColor.RED};

                @Override
                public void run() {
                    if (time <= 0) {
                        player.getPlayer().sendTitle("", ChatColor.YELLOW + "Respawned!", 0, 5, 5);
                        respawn();
                        this.cancel();
                    }
                    if (!this.isCancelled()) {
                        player.getPlayer().sendTitle(ChatColor.GRAY + "Respawning in: " + timeColors[time - 1] + time, (killer != null ? ChatColor.DARK_GRAY + "Killed by " + ChatColor.RED + killer.getDisplayName() : ""), 0, 25, 3);
                        time--;
                    }
                }
            }.runTaskTimer(plugin, 0, 20L);
        }
    }

}
