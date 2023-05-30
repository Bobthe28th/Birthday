package me.bobthe28th.birthday.games.minigames.prophunt;

import me.bobthe28th.birthday.Main;
import me.bobthe28th.birthday.games.GamePlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;

public class PrHunterPlayer extends PrPlayer {

    boolean spawning = false;

    public PrHunterPlayer(Main plugin, GamePlayer player, PropHunt propHunt) {
        super(plugin, player, propHunt, PrPlayerType.HUNTER);
    }

    @Override
    public void spawn(Location l) {
        if (l.getWorld() == null) return;
        player.getPlayer().teleport(l);
        spawning = true;
        player.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS,200,0,true,false,false));
        new BukkitRunnable() {
            @Override
            public void run() {
                spawning = false;
            }
        }.runTaskLater(plugin,200);
    }

    @Override
    public void giveItems() {
        PlayerInventory inventory = player.getPlayer().getInventory();
        inventory.clear();

        ItemStack sword = new ItemStack(Material.STONE_SWORD);
        ItemMeta meta = sword.getItemMeta();
        if (meta != null) {
            meta.setUnbreakable(true);
            meta.setLore(List.of("Hit blocks to find props!"));
        }
        sword.setItemMeta(meta);
        inventory.setItem(4,sword);

        player.getPlayer().getInventory().setHeldItemSlot(4);
        player.getPlayer().updateInventory();
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getPlayer() != player.getPlayer()) return;
        if (!spawning) return;
        if (event.getTo() != null && event.getTo().toVector().equals(event.getFrom().toVector())) return;
        event.getPlayer().setVelocity(new Vector());
        event.setTo(event.getFrom().setDirection(event.getTo().getDirection()));
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() != player.getPlayer()) return;
        if (!(event.getEntity() instanceof Slime s)) return;
        if (spawning) return;
        for (PrPlayer p : propHunt.players.values()) {
            if (p instanceof PrPropPlayer prop) {
                if (prop.globalPropEntityHitBox == s) {
                    event.setCancelled(true);
                    prop.getPlayer().damage(event.getFinalDamage());
                }
            }
        }
    }
}
