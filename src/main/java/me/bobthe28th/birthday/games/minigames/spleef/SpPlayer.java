package me.bobthe28th.birthday.games.minigames.spleef;

import me.bobthe28th.birthday.Main;
import me.bobthe28th.birthday.games.GamePlayer;
import me.bobthe28th.birthday.games.minigames.MinigamePlayer;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.UUID;

public class SpPlayer extends MinigamePlayer {

    Spleef spleef;

    public SpPlayer(Main plugin, GamePlayer player, Spleef spleef) {
        super(plugin, player, spleef);
        this.spleef = spleef;
        Main.gameController.giveAdvancement(player.getPlayer(),"spleef");
        player.getPlayer().setGameMode(GameMode.SURVIVAL);
    }

    public void giveItems() {
        PlayerInventory inventory = player.getPlayer().getInventory();
        inventory.clear();
        ItemStack shovel = new ItemStack(Material.IRON_SHOVEL);
        ItemMeta meta = shovel.getItemMeta();
        if (meta != null) {
            meta.setUnbreakable(true);
            meta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED,new AttributeModifier(UUID.randomUUID(),"generic_attack_speed",100, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND));
            meta.setLore(List.of("Break snow blocks to get snowballs!"));
        }
        shovel.setItemMeta(meta);
        inventory.setItem(3,shovel);
        inventory.setHeldItemSlot(3);
        player.getPlayer().updateInventory();
    }

    public void breakBlock(boolean projectile) {
        if (!projectile) {
            addBalls(1);
        }
        player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, SoundCategory.MASTER, 1.0f, 1.0f);
    }

    public void addBalls(int amount) {
        PlayerInventory inventory = player.getPlayer().getInventory();
        ItemStack balls = inventory.getItem(5);
        if (balls == null || balls.getType() != Material.SNOWBALL) {
            inventory.setItem(5,new ItemStack(Material.SNOWBALL));
        } else {
            balls.setAmount(balls.getAmount() + amount);
        }
        player.getPlayer().updateInventory();
    }

    public void remove() {
        player.getPlayer().setHealth(20.0);
        player.getPlayer().getInventory().clear();
    }

    public void death() {
        alive = false;
        player.getPlayer().setGameMode(GameMode.SPECTATOR);
        player.getPlayer().getInventory().clear();
        ChatColor c = ChatColor.RED;
        if (spleef.isBonusRound) {
            c = spleef.bmsts.getTeamColor(player.getPlayer(),ChatColor.RED);
        }
        Bukkit.broadcastMessage(ChatColor.GRAY + "[" + c + "☠" + ChatColor.GRAY + "] " + c + player.getPlayer().getDisplayName() + ChatColor.GRAY +  " died");
    }
}
