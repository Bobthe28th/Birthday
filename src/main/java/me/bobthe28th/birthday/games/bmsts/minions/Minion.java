package me.bobthe28th.birthday.games.bmsts.minions;

import me.bobthe28th.birthday.Main;
import me.bobthe28th.birthday.games.bmsts.BmTeam;
import me.bobthe28th.birthday.games.bmsts.Bmsts;
import me.bobthe28th.birthday.games.bmsts.minions.entities.MinionEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftMob;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.lang.reflect.Constructor;
import java.util.*;

public class Minion implements Listener {

    Main plugin;
    String name;
    Class<? extends MinionEntity> entityType;
    BmTeam team;
    Integer techLevel;
    Rarity rarity;
    Integer strength;
    Integer customModel;

    ItemStack itemStack;
    Item droppedItem;

    ArrayList<Mob> entities = new ArrayList<>();
    double groupSpawnDist = 4;

    Mob previewEntity;
    ArmorStand placedArmorStand;
    Location placedLoc;

    public Minion(Main plugin, String name, Class<? extends MinionEntity> entityType, BmTeam team, Integer techLevel, Rarity rarity, Integer strength, Integer customModel) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.plugin = plugin;
        this.name = name;
        this.entityType = entityType;
        this.team = team;
        this.techLevel = techLevel;
        this.rarity = rarity;
        this.strength = strength;
        this.customModel = customModel;
        team.addMinion(this); //todo add place y offset (when it moves up)
    }

    public void remove(boolean fromList) {
        if (itemStack != null) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getInventory().contains(itemStack)) {
                    p.getInventory().remove(itemStack);
                }
            }
            itemStack = null;
        }
        if (droppedItem != null) droppedItem.remove();
        if (placedArmorStand != null) placedArmorStand.remove();
        if (previewEntity != null) previewEntity.remove(Entity.RemovalReason.DISCARDED);
        if (entities.size() > 0) {
            for (LivingEntity e : entities) {
                e.remove(Entity.RemovalReason.DISCARDED);
            }
        }
        if (fromList) {
            team.removeMinion(this);
        }
    }

    public ItemStack getItem() {
        ItemStack item = new ItemStack(Material.SADDLE);
        if (item.getItemMeta() != null) {
            ItemMeta meta = item.getItemMeta();
            meta.setCustomModelData(customModel);
            meta.setDisplayName(ChatColor.RESET + "" + Bmsts.strengthColor[strength - 1] + strength + " " + (rarity.getColor() == ChatColor.MAGIC ? Main.rainbow(name + (strength > 1 ? "s" : "")) : rarity.getColor() + name + (strength > 1 ? "s" : "")));
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.RESET + "" + ChatColor.WHITE + "Techlevel: " + Bmsts.techLevelColor[techLevel] + techLevel);
            lore.add(ChatColor.RESET + "" + ChatColor.WHITE + "Rarity: " + (rarity.getColor() == ChatColor.MAGIC ? Main.rainbow(rarity.toString()) : rarity.getColor() + rarity.toString()) + ChatColor.RESET);
            lore.add(ChatColor.RESET + "" + ChatColor.WHITE + "Strength: " + Bmsts.strengthColor[strength - 1] + strength);
            lore.add(ChatColor.RESET + "" + team.getTeam().getColor() + team.getTeam().getDisplayName());
            meta.setLore(lore);
            meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "minionitem"), PersistentDataType.BYTE, (byte) 1);
            meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "uniqueid"), PersistentDataType.STRING, UUID.randomUUID().toString());
            item.setItemMeta(meta);
        }
        itemStack = item;
        return item;
    }

    public void giveItem(Player p, boolean raw) {
        getItem();
        if (!raw && p.getInventory().getItemInMainHand().getType() == Material.AIR) {
            p.getInventory().setItemInMainHand(itemStack);
        } else {
            p.getInventory().addItem(itemStack);
        }
    }

    public void drop(Location l) {
        if (l.getWorld() == null) return;
        if (placedArmorStand != null) placedArmorStand.remove();
        if (previewEntity != null) previewEntity.remove(Entity.RemovalReason.DISCARDED);
        if (itemStack == null) getItem();
        droppedItem = l.getWorld().dropItem(l, itemStack);
        droppedItem.setUnlimitedLifetime(true);
        droppedItem.setVelocity(new Vector(0,0.2,0));
    }

    public boolean dropKept(Location l) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getInventory().contains(itemStack)) {
                droppedItem = p.getWorld().dropItem(l, itemStack);
                droppedItem.setUnlimitedLifetime(true);
                droppedItem.setVelocity(new Vector(0,0.2,0));
                p.getInventory().remove(itemStack);
                return true;
            }
        }
        return false;
    }

    public boolean dropKeptBy(Player p, Location l) {
        if (p.getInventory().contains(itemStack)) {
            droppedItem = p.getWorld().dropItem(l, itemStack);
            droppedItem.setUnlimitedLifetime(true);
            droppedItem.setVelocity(new Vector(0,0.2,0));
            p.getInventory().remove(itemStack);
            return true;
        }
        return false;
    }

    public Location getPlacedLoc() {
        return placedLoc;
    }

    public void place(Location loc, boolean preview) {
        if (loc.getWorld() == null) return;
        placedArmorStand = (ArmorStand) loc.getWorld().spawnEntity(loc.clone().add(0,-2,0), EntityType.ARMOR_STAND);
        placedArmorStand.setInvisible(true);
        placedArmorStand.setInvulnerable(true);
        placedArmorStand.teleport(loc);
        placedLoc = loc.clone().add(-0.5,0,-0.5);
        if (preview) {
            spawn(loc.clone().add(0, 1, -3), true);
        }
        if (placedArmorStand.getEquipment() != null) {
            placedArmorStand.getEquipment().setHelmet(itemStack.clone());
        }
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getInventory().contains(itemStack)) {
                p.getInventory().remove(itemStack);
            }
        }
    }

    public void spawn(Location location, boolean preview) {
        ServerLevel w = ((CraftWorld) Objects.requireNonNull(location.getWorld())).getHandle();
        FileConfiguration config = plugin.getConfig();
        try {
            Constructor<?> constructor = entityType.getConstructor(Location.class, BmTeam.class, Rarity.class, Boolean.class, FileConfiguration.class);
            Mob e = (Mob) constructor.newInstance(location.clone(),team,rarity,preview,config);
            if (preview) {
                e.setNoAi(true);
                e.setSilent(true);
                e.setInvulnerable(true);
                previewEntity = e;
            }
            w.addFreshEntity(e);
            if (!preview) {
                e.getBukkitEntity().setCustomName(Bmsts.getHealthString(e.getHealth(), team.getColor(), team.getDarkColor()));
                entities.add(e);
            }
        } catch (Exception ignored) {}
    }

    public void spawnGroup(Location loc) {
        int amount = strength - entities.size();
        loc.add(0.5,0,0.5);
        switch (amount) {
            case 1 -> spawn(loc,false);
            case 2 -> {
                spawn(loc.add(-groupSpawnDist / 2, 0, 0),false);
                spawn(loc.add(groupSpawnDist / 2, 0, 0),false);
            }
            case 3 -> {
                spawn(loc.add(-groupSpawnDist / 2, 0, 0),false);
                spawn(loc.add(groupSpawnDist / 2, 0, 0),false);
                spawn(loc.add(0, 0, groupSpawnDist * 0.866),false);
            }
        }
    }

    public void showTargets() {
        double blocksPerParticle = 0.2;
        for (Mob e : entities) {
            if (e.getTarget() != null) {
                Vec3 dir = e.getTarget().position().subtract(e.position()).normalize();
                Vec3 particlePos = e.position().add(dir.multiply(new Vec3(blocksPerParticle,blocksPerParticle,blocksPerParticle)));
                for (int i = 0; i < e.position().distanceTo(e.getTarget().position()) / blocksPerParticle; i++) {
                    e.getBukkitEntity().getWorld().spawnParticle(Particle.REDSTONE,particlePos.x,particlePos.y + 1,particlePos.z,1,0,0,0,1, new Particle.DustOptions(team.getBColor(),1.0F));
                    particlePos = particlePos.add(dir.multiply(new Vec3(blocksPerParticle,blocksPerParticle,blocksPerParticle)));
                }
            }
        }
    }

    public void removeEntities() {
        if (entities.size() > 0) {
            for (LivingEntity e : entities) {
                e.remove(Entity.RemovalReason.DISCARDED);
            }
        }
        entities.clear();
    }

    public ArrayList<Mob> getEntities() {
        return entities;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamage(EntityDamageEvent event) {
        if (!event.isCancelled()) {
            if (event.getEntity() instanceof org.bukkit.entity.LivingEntity l && l instanceof CraftMob && entities.contains(((CraftMob) l).getHandle())) {
                if (l.getHealth() - event.getFinalDamage() <= 0) {
                    entities.remove(((CraftMob) l).getHandle());
                    l.setCustomName(ChatColor.WHITE + "☠");
                    if (entities.size() == 0) {
                        team.minionDeath();
                    }
                } else {
                    l.setCustomName(Bmsts.getHealthString(l.getHealth() - event.getFinalDamage(), team.getColor(), team.getDarkColor()));
                }
            }
        }
    }

    @EventHandler
    public void onEntityRegainHealth(EntityRegainHealthEvent event) {
        if (event.getEntity() instanceof org.bukkit.entity.LivingEntity l && l instanceof CraftMob && entities.contains(((CraftMob)l).getHandle()) && l.getAttribute(Attribute.GENERIC_MAX_HEALTH) != null) {
            l.setCustomName(Bmsts.getHealthString(Math.min(Objects.requireNonNull(l.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getBaseValue(),l.getHealth() + event.getAmount()),team.getColor(), team.getDarkColor()));
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getItem() != null && event.getClickedBlock() != null && event.getItem().equals(itemStack)) {
            Location placedLocation = event.getClickedBlock().getLocation().add(event.getBlockFace().getDirection());
            if (team.getSpawners().contains(placedLocation)) {
                place(placedLocation.clone().add(0.5,0,0.5),true); //TODO can place in same spot
            } else if (team.getRandomizer().equals(placedLocation) && team.getResearchPoints() >= 1) {
                Random rand = new Random();
                Class<?> mClass = Bmsts.minionTypes[techLevel][rand.nextInt(Bmsts.minionTypes[techLevel].length)];
                try {
                    Constructor<?> constructor = mClass.getConstructor(Main.class, BmTeam.class, Rarity.class, Integer.class);
                    Minion c = (Minion) constructor.newInstance(plugin, team, Rarity.values()[rand.nextInt(Rarity.values().length)], rand.nextInt(3) + 1);
                    Item i = event.getPlayer().getWorld().dropItem(placedLocation.clone().add(0.5,0,0.5),c.getItem()); //TODO particles
                    i.setVelocity(new Vector(0,0.2,0));
                } catch (Exception ignored) {}
                team.addResearchPoints(-1);
                remove(true);
            } else if (team.getTechUpgrade().equals(placedLocation) && team.getResearchPoints() >= 1) { //TODO values
                if (techLevel < 5) {
                    Class<?> mClass = Bmsts.minionTypes[techLevel + 1][0];
                    try {
                        Constructor<?> constructor = mClass.getConstructor(Main.class, BmTeam.class, Rarity.class, Integer.class);
                        Minion c = (Minion) constructor.newInstance(plugin, team, Rarity.COMMON, 1);
                        Item i = event.getPlayer().getWorld().dropItem(placedLocation.clone().add(0.5, 0, 0.5), c.getItem()); //TODO particles
                        i.setVelocity(new Vector(0, 0.2, 0));
                    } catch (Exception ignored) {}
                    team.addResearchPoints(-1); //TODO values
                    remove(true);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (event.getItemDrop().getItemStack().equals(itemStack)) {
            droppedItem = event.getItemDrop();
            droppedItem.setUnlimitedLifetime(true);
        }
    }

    @EventHandler
    public void onPlayerPickupItem(EntityPickupItemEvent event) {
        if (event.getItem().equals(droppedItem)) {
            if (event.getEntity() instanceof Player p && Bmsts.BmPlayers.containsKey(p) && Bmsts.BmPlayers.get(p).getTeam() == team) {
                droppedItem = null;
            } else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        if (event.getRightClicked() == placedArmorStand && !team.isReady()) {
            placedArmorStand.remove();
            placedLoc = null;
            if (previewEntity != null) {
                previewEntity.remove(Entity.RemovalReason.DISCARDED);
            }
            giveItem(event.getPlayer(),false);
        }
    }

}
