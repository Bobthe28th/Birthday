package me.bobthe28th.birthday.games.bmsts.minions.entities;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class SpinGoal extends Goal {

    private final Mob mob;
    private double angle = 0;

    public SpinGoal(Mob mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return true;
    }

    public boolean canContinueToUse() {
        return true;
    }

    public boolean requiresUpdateEveryTick() {
        return true;
    }

    public void tick() {
        angle = angle + 0.15;
        this.mob.getLookControl().setLookAt(this.mob.getX() + Math.cos(angle), this.mob.getEyeY(), this.mob.getZ() + Math.sin(angle));
    }
}
