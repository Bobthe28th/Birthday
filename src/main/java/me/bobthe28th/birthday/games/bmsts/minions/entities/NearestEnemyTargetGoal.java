package me.bobthe28th.birthday.games.bmsts.minions.entities;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import org.bukkit.event.entity.EntityTargetEvent;

import java.util.EnumSet;
import java.util.Iterator;

public class NearestEnemyTargetGoal extends TargetGoal {

    protected LivingEntity target;


    public NearestEnemyTargetGoal(Mob entityinsentient) {
        super(entityinsentient, true);
        this.setFlags(EnumSet.of(Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        this.findTarget();
        return this.target != null;
    }

    protected void findTarget() {
        Iterator<Entity> i = this.mob.level.getEntities().getAll().iterator();
        double dist = -1.0;
        LivingEntity closest = null;
        while(true) {
            if (!i.hasNext()) {
                this.target = closest;
                return;
            }
            Entity eE = i.next();
            if (eE instanceof LivingEntity e) {
                if (e instanceof MinionEntity t && !t.isPreview() && this.mob instanceof MinionEntity mt && mt.getGameTeam() != t.getGameTeam()) {
                    double tDist = e.distanceToSqr(this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());
                    if ((dist == -1.0 || tDist < dist)) { //TODO and random range
                        dist = tDist;
                        closest = e;
                    }
                }
            }
        }
    }

    public void start() {
        this.mob.setTarget(this.target, this.target instanceof ServerPlayer ? EntityTargetEvent.TargetReason.CLOSEST_PLAYER : EntityTargetEvent.TargetReason.CLOSEST_ENTITY, true);
        super.start();
    }

}
