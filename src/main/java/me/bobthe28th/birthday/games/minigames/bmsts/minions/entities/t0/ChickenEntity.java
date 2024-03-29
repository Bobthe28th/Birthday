package me.bobthe28th.birthday.games.minigames.bmsts.minions.entities.t0;

import me.bobthe28th.birthday.games.minigames.bmsts.minions.Minion;
import me.bobthe28th.birthday.games.minigames.bmsts.minions.entities.MinionEntity;
import me.bobthe28th.birthday.games.minigames.bmsts.minions.entities.NearestEnemyTargetGoal;
import me.bobthe28th.birthday.games.minigames.bmsts.minions.entities.PassiveMeleeAttackGoal;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.animal.Chicken;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;

import java.util.Objects;

public class ChickenEntity extends Chicken implements MinionEntity {

    Minion minion;
    boolean preview;

    public ChickenEntity(Location loc, Minion minion, Boolean preview) {
        super(EntityType.CHICKEN, ((CraftWorld) Objects.requireNonNull(loc.getWorld())).getHandle());
        this.minion = minion;
        this.preview = preview;
        this.setOnGround(true);
        this.setPos(loc.getX(), loc.getY(), loc.getZ());
        this.setCanPickUpLoot(false);
        this.setPersistenceRequired(true);
        if (!preview) {
            this.setCustomNameVisible(true);
        }
        this.eggTime = Integer.MAX_VALUE;
    }

    @Override
    public void registerGoals() {
        this.goalSelector.addGoal(2, new PassiveMeleeAttackGoal(this, 1.0));
        this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this,1F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(3, new NearestEnemyTargetGoal(this));
    }

    @Override
    public boolean hurt(DamageSource damagesource, float f) {
        return minionHurt(this,super.hurt(damagesource,f), damagesource, f);
    }

//    public boolean hurt(DamageSource damagesource, float f) {
//        if (!super.hurt(damagesource, f)) {
//            return false;
//        } else if (!(this.level instanceof ServerLevel)) {
//            return false;
//        } else {
//            Entity damager;
//            if (damagesource.getEntity() instanceof Projectile p) {
//                damager = p.getOwner();
//            } else {
//                damager = damagesource.getEntity();
//            }
//            if (damager instanceof LivingEntity) {
//                if (damager instanceof MinionEntity dm && this.getGameTeam() != dm.getGameTeam()) {
//                    this.setTarget((LivingEntity) damager, EntityTargetEvent.TargetReason.TARGET_ATTACKED_ENTITY, true);
//                    if (getTarget() != null) {
//                        for (Entity eE : this.level.getEntities().getAll()) {
//                            if (eE instanceof LivingEntity e) {
//                                if (e instanceof MinionEntity t && this.getGameTeam() == t.getGameTeam()) {
//                                    if (e instanceof Mob m) {
//                                        if (m.getTarget() == null || m.position().distanceToSqr(getTarget().position()) < m.position().distanceToSqr(m.getTarget().position())) {
//                                            m.setTarget(getTarget(), EntityTargetEvent.TargetReason.TARGET_ATTACKED_NEARBY_ENTITY, true);
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//            return true;
//        }
//    }

    @Override
    public Minion getMinion() {
        return minion;
    }

    @Override
    public boolean isPreview() {
        return preview;
    }
}
