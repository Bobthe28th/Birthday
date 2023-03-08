package me.bobthe28th.birthday.games.bmsts.minions.entities.t1;

import me.bobthe28th.birthday.games.bmsts.BmTeam;
import me.bobthe28th.birthday.games.bmsts.minions.Rarity;
import me.bobthe28th.birthday.games.bmsts.minions.entities.MinionEntity;
import me.bobthe28th.birthday.games.bmsts.minions.entities.NearestEnemyTargetGoal;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.ZombieAttackGoal;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.projectile.Projectile;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_19_R2.CraftWorld;
import org.bukkit.event.entity.EntityTargetEvent;

import java.util.Objects;

public class ZombieEntity extends Zombie implements MinionEntity {

    BmTeam team;
    Rarity rarity;
    Boolean preview;

    public ZombieEntity(Location loc, BmTeam team, Rarity rarity, Boolean preview, FileConfiguration config) {
        super(EntityType.ZOMBIE, ((CraftWorld) Objects.requireNonNull(loc.getWorld())).getHandle());
        this.team = team;
        this.rarity = rarity;
        this.preview = preview;
        this.setPos(loc.getX(), loc.getY(), loc.getZ());
        this.setCanPickUpLoot(false);
        this.setPersistenceRequired(true);
        if (!preview) {
            this.setCustomNameVisible(true);
        }
    }

    @Override
    public BmTeam getGameTeam() {
        return team;
    }

    @Override
    public void registerGoals() {
        this.goalSelector.addGoal(2, new ZombieAttackGoal(this, 1.0, false));
        this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this,1F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(3, new NearestEnemyTargetGoal(this));
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
                    if (getTarget() == null || (position().distanceTo(damager.position()) < position().distanceToSqr(getTarget().position()))) {
                        this.setTarget((LivingEntity) damager, EntityTargetEvent.TargetReason.TARGET_ATTACKED_ENTITY, true);
                    }
                    for (Entity eE : this.level.getEntities().getAll()) {
                        if (eE instanceof LivingEntity e) {
                            if (e instanceof MinionEntity t && this.getGameTeam() == t.getGameTeam()) {
                                if (e instanceof Mob m) {
                                    if (m.getTarget() == null || m.position().distanceToSqr(damager.position()) < m.position().distanceToSqr(m.getTarget().position())) {
                                        m.setTarget((LivingEntity) damager, EntityTargetEvent.TargetReason.TARGET_ATTACKED_NEARBY_ENTITY, true);
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

    //No armor
    @Override
    protected void populateDefaultEquipmentSlots(RandomSource randomsource, DifficultyInstance difficultydamagescaler) {}

    @Override
    public boolean isPreview() {
        return preview;
    }
}
