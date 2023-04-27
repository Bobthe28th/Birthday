package me.bobthe28th.birthday.games.minigames.bmsts.minions.entities.t2;

import me.bobthe28th.birthday.games.minigames.bmsts.BmTeam;
import me.bobthe28th.birthday.games.minigames.bmsts.minions.Rarity;
import me.bobthe28th.birthday.games.minigames.bmsts.minions.entities.MinionEntity;
import me.bobthe28th.birthday.games.minigames.bmsts.minions.entities.NearestEnemyTargetGoal;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.RangedCrossbowAttackGoal;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.bukkit.event.entity.EntityTargetEvent;

import java.util.Objects;

public class PillagerEntity extends Pillager implements MinionEntity {

    BmTeam team;
    Rarity rarity;
    Boolean preview;

    public PillagerEntity(Location loc, BmTeam team, Rarity rarity, Boolean preview, FileConfiguration config) {
        super(EntityType.PILLAGER, ((CraftWorld) Objects.requireNonNull(loc.getWorld())).getHandle());
//        DifficultyInstance d = ((CraftWorld) loc.getWorld()).getHandle().getCurrentDifficultyAt(new BlockPos(loc.getX(),loc.getY(),loc.getZ()));
        this.team = team;
        this.rarity = rarity;
        this.preview = preview;
        this.setPos(loc.getX(), loc.getY(), loc.getZ());
        this.setCanPickUpLoot(false);
        this.setPersistenceRequired(true);
        this.setPatrolLeader(false);
//        this.populateDefaultEquipmentSlots(this.getRandom(),d);
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.CROSSBOW));
        if (!preview) {
            this.setCustomNameVisible(true);
        }
    }

    @Override
    public void registerGoals() {
        this.goalSelector.addGoal(3, new RangedCrossbowAttackGoal<>(this, 1.0, 8.0F));
        this.goalSelector.addGoal(8, new RandomStrollGoal(this, 0.6));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(2, new NearestEnemyTargetGoal(this));
    }

    public boolean hurt(DamageSource damagesource, float f) {
        if (!super.hurt(damagesource, f)) {
            return false;
        } else if (!(this.level instanceof ServerLevel)) {
            return false;
        } else {
            Entity damager;
            if (damagesource.getEntity() instanceof Projectile p) {
                damager = p.getOwner();
            } else {
                damager = damagesource.getEntity();
            }
            if (damager instanceof LivingEntity) {
                if (damager instanceof MinionEntity dm && this.getGameTeam() != dm.getGameTeam()) {
                    this.setTarget((LivingEntity) damager, EntityTargetEvent.TargetReason.TARGET_ATTACKED_ENTITY, true);
                    if (getTarget() != null) {
                        for (Entity eE : this.level.getEntities().getAll()) {
                            if (eE instanceof LivingEntity e) {
                                if (e instanceof MinionEntity t && this.getGameTeam() == t.getGameTeam()) {
                                    if (e instanceof Mob m) {
                                        if (m.getTarget() == null || m.position().distanceToSqr(getTarget().position()) < m.position().distanceToSqr(m.getTarget().position())) {
                                            m.setTarget(getTarget(), EntityTargetEvent.TargetReason.TARGET_ATTACKED_NEARBY_ENTITY, true);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return true;
        }
    }

    protected void enchantSpawnedWeapon(RandomSource var0, float var1) {}

    @Override
    public BmTeam getGameTeam() {
        return team;
    }

    @Override
    public boolean isPreview() {
        return preview;
    }

}
