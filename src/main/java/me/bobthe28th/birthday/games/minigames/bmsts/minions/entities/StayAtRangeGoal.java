package me.bobthe28th.birthday.games.minigames.bmsts.minions.entities;

import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class StayAtRangeGoal extends Goal {

    protected final PathfinderMob mob;
    private final double speedModifier;
    private Path path;
//    private double pathedTargetX;
//    private double pathedTargetY;
//    private double pathedTargetZ;
//    private int ticksUntilNextPathRecalculation;
//    private long lastCanUseCheck;

    private final double minRange;
    private final double maxRange;

    public StayAtRangeGoal(PathfinderMob var0, double var1, double minRange, double maxRange) {
        this.mob = var0;
        this.speedModifier = var1;
        this.minRange = minRange;
        this.maxRange = maxRange;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.mob.getTarget();
        if (target == null) {
            return false;
        } else if (!target.isAlive()) {
            return false;
        } else {
            double dist = this.mob.distanceToSqr(target.getX(), target.getY(), target.getZ());
            if (dist <= maxRange * maxRange && dist >= minRange * minRange) {
                this.mob.getNavigation().stop();
                return false;
            } else if (dist <= minRange * minRange) {
                Vec3 var0 = DefaultRandomPos.getPosAway(this.mob, 4, 2, target.position());
                if (var0 == null) {
                    return false;
                } else if (target.distanceToSqr(var0.x, var0.y, var0.z) < target.distanceToSqr(this.mob)) {
                    return false;
                } else {
                    this.path = this.mob.getNavigation().createPath(var0.x, var0.y, var0.z, 0);
                    return this.path != null;
                }
            } else {
                this.path = this.mob.getNavigation().createPath(target, 0);
                if (this.path != null) {
                    return true;
                } else {
                    return dist <= minRange * minRange;
                }
            }
        }
    }



//    public void tick() {
//        LivingEntity target = this.mob.getTarget();
//        if (target != null) {
//            this.mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
//            double dist = this.mob.distanceToSqr(target.getX(), target.getY(), target.getZ());
//            this.ticksUntilNextPathRecalculation = Math.max(this.ticksUntilNextPathRecalculation - 1, 0);
//            if (this.ticksUntilNextPathRecalculation <= 0) {
////                this.pathedTargetX = target.getX();
////                this.pathedTargetY = target.getY();
////                this.pathedTargetZ = target.getZ();
//                this.ticksUntilNextPathRecalculation = 4 + this.mob.getRandom().nextInt(7);
//                if (dist > 1024.0) {
//                    this.ticksUntilNextPathRecalculation += 10;
//                } else if (dist > 256.0) {
//                    this.ticksUntilNextPathRecalculation += 5;
//                }
//
//                if (!this.mob.getNavigation().moveTo(target, this.speedModifier)) {
//                    this.ticksUntilNextPathRecalculation += 15;
//                }
//
//                this.ticksUntilNextPathRecalculation = this.adjustedTickDelay(this.ticksUntilNextPathRecalculation);
//            }
//        }
//    }

    public void start() {
        this.mob.getNavigation().moveTo(this.path, this.speedModifier);
//        this.ticksUntilNextPathRecalculation = 0;
    }

    public void stop() {
        LivingEntity target = this.mob.getTarget();
        if (!EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(target)) {
            this.mob.setTarget(null);
        }
        this.mob.getNavigation().stop();
    }
}
