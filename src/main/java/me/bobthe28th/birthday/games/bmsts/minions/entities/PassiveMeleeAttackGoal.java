package me.bobthe28th.birthday.games.bmsts.minions.entities;

import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.pathfinder.Path;

import java.util.EnumSet;

public class PassiveMeleeAttackGoal extends Goal {

    protected final PathfinderMob mob;
    private final double speedModifier;
    private Path path;
    private double pathedTargetX;
    private double pathedTargetY;
    private double pathedTargetZ;
    private int ticksUntilNextPathRecalculation;
    private int ticksUntilNextAttack;
    private long lastCanUseCheck;

    public PassiveMeleeAttackGoal(PathfinderMob var0, double var1) {
        this.mob = var0;
        this.speedModifier = var1;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        long gameTime = this.mob.level.getGameTime();
        if (gameTime - this.lastCanUseCheck < 20L) {
            return false;
        } else {
            this.lastCanUseCheck = gameTime;
            LivingEntity target = this.mob.getTarget();
            if (target == null) {
                return false;
            } else if (!target.isAlive()) {
                return false;
            } else {
                this.path = this.mob.getNavigation().createPath(target, 0);
                if (this.path != null) {
                    return true;
                } else {
                    return this.getAttackReachSqr(target) >= this.mob.distanceToSqr(target.getX(), target.getY(), target.getZ());
                }
            }
        }
    }

    public boolean canContinueToUse() {
        LivingEntity target = this.mob.getTarget();
        if (target == null) {
            return false;
        } else if (!target.isAlive()) {
            return false;
        } else if (!this.mob.isWithinRestriction(target.blockPosition())) {
            return false;
        } else {
            return !(target instanceof Player) || !target.isSpectator() && !((Player)target).isCreative();
        }
    }

    public void start() {
        this.mob.getNavigation().moveTo(this.path, this.speedModifier);
        this.mob.setAggressive(true);
        this.ticksUntilNextPathRecalculation = 0;
        this.ticksUntilNextAttack = 0;
    }

    public void stop() {
        LivingEntity target = this.mob.getTarget();
        if (!EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(target)) {
            this.mob.setTarget(null);
        }

        this.mob.setAggressive(false);
        this.mob.getNavigation().stop();
    }

    public boolean requiresUpdateEveryTick() {
        return true;
    }

    public void tick() {
        LivingEntity target = this.mob.getTarget();
        if (target != null) {
            this.mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
            double dist = this.mob.distanceToSqr(target.getX(), target.getY(), target.getZ());
            this.ticksUntilNextPathRecalculation = Math.max(this.ticksUntilNextPathRecalculation - 1, 0);
            if (this.ticksUntilNextPathRecalculation <= 0 && (this.pathedTargetX == 0.0 && this.pathedTargetY == 0.0 && this.pathedTargetZ == 0.0 || target.distanceToSqr(this.pathedTargetX, this.pathedTargetY, this.pathedTargetZ) >= 1.0 || this.mob.getRandom().nextFloat() < 0.05F)) {
                this.pathedTargetX = target.getX();
                this.pathedTargetY = target.getY();
                this.pathedTargetZ = target.getZ();
                this.ticksUntilNextPathRecalculation = 4 + this.mob.getRandom().nextInt(7);
                if (dist > 1024.0) {
                    this.ticksUntilNextPathRecalculation += 10;
                } else if (dist > 256.0) {
                    this.ticksUntilNextPathRecalculation += 5;
                }

                if (!this.mob.getNavigation().moveTo(target, this.speedModifier)) {
                    this.ticksUntilNextPathRecalculation += 15;
                }

                this.ticksUntilNextPathRecalculation = this.adjustedTickDelay(this.ticksUntilNextPathRecalculation);
            }

            this.ticksUntilNextAttack = Math.max(this.ticksUntilNextAttack - 1, 0);
            this.checkAndPerformAttack(target, dist);
        }
    }

    protected void checkAndPerformAttack(LivingEntity target, double dist) {
        double reachDist = this.getAttackReachSqr(target);
        if (dist <= reachDist && this.ticksUntilNextAttack <= 0) {
            this.resetAttackCooldown();
            ((org.bukkit.entity.LivingEntity)target.getBukkitEntity()).damage(1.0F,this.mob.getBukkitEntity());
        }
    }

    protected void resetAttackCooldown() {
        this.ticksUntilNextAttack = this.adjustedTickDelay(20);
    }

    protected double getAttackReachSqr(LivingEntity var0) {
        return this.mob.getBbWidth() * 2.0F * this.mob.getBbWidth() * 2.0F + var0.getBbWidth();
    }
}
