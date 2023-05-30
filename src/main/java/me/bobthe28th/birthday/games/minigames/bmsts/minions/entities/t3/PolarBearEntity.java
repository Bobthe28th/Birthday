package me.bobthe28th.birthday.games.minigames.bmsts.minions.entities.t3;

import me.bobthe28th.birthday.games.minigames.bmsts.minions.Minion;
import me.bobthe28th.birthday.games.minigames.bmsts.minions.entities.MinionEntity;
import me.bobthe28th.birthday.games.minigames.bmsts.minions.entities.NearestEnemyTargetGoal;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.animal.PolarBear;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;

import java.util.Objects;

public class PolarBearEntity extends PolarBear implements MinionEntity {

    Minion minion;
    boolean preview;

    public PolarBearEntity(Location loc, Minion minion, Boolean preview) {
        super(EntityType.POLAR_BEAR, ((CraftWorld) Objects.requireNonNull(loc.getWorld())).getHandle());
        this.minion = minion;
        this.preview = preview;
        this.setPos(loc.getX(), loc.getY(), loc.getZ());
        this.setCanPickUpLoot(false);
        this.setPersistenceRequired(true);
        if (!preview) {
            this.setCustomNameVisible(true);
        }
    }

    @Override
    public void registerGoals() {
        this.goalSelector.addGoal(4, new PolarBearMeleeAttackGoal());
        this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this,1F));
        this.targetSelector.addGoal(2, new NearestEnemyTargetGoal(this));
    }

    class PolarBearMeleeAttackGoal extends MeleeAttackGoal {
        public PolarBearMeleeAttackGoal() {
            super(PolarBearEntity.this, 1.25, true);
        }

        protected void checkAndPerformAttack(LivingEntity var0, double var1) {
            double var3 = this.getAttackReachSqr(var0);
            if (var1 <= var3 && this.isTimeToAttack()) {
                this.resetAttackCooldown();
                this.mob.doHurtTarget(var0);
                PolarBearEntity.this.setStanding(false);
            } else if (var1 <= var3 * 2.0) {
                if (this.isTimeToAttack()) {
                    PolarBearEntity.this.setStanding(false);
                    this.resetAttackCooldown();
                }

                if (this.getTicksUntilNextAttack() <= 10) {
                    PolarBearEntity.this.setStanding(true);
                    PolarBearEntity.this.playWarningSound();
                }
            } else {
                this.resetAttackCooldown();
                PolarBearEntity.this.setStanding(false);
            }

        }

        public void stop() {
            PolarBearEntity.this.setStanding(false);
            super.stop();
        }

        protected double getAttackReachSqr(LivingEntity var0) {
            return (double)(4.0F + var0.getBbWidth());
        }
    }

    @Override
    public boolean hurt(DamageSource damagesource, float f) {
        return minionHurt(this,super.hurt(damagesource,f), damagesource, f);
    }

    @Override
    public Minion getMinion() {
        return minion;
    }

    @Override
    public boolean isPreview() {
        return preview;
    }

}
