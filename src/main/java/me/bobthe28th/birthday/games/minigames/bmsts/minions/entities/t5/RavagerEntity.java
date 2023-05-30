package me.bobthe28th.birthday.games.minigames.bmsts.minions.entities.t5;

import me.bobthe28th.birthday.games.minigames.bmsts.minions.Minion;
import me.bobthe28th.birthday.games.minigames.bmsts.minions.entities.MinionEntity;
import me.bobthe28th.birthday.games.minigames.bmsts.minions.entities.NearestEnemyTargetGoal;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.monster.Ravager;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;

import java.util.Objects;

public class RavagerEntity extends Ravager implements MinionEntity {

    Minion minion;
    boolean preview;

    public RavagerEntity(Location loc, Minion minion, Boolean preview) {
        super(EntityType.RAVAGER, ((CraftWorld) Objects.requireNonNull(loc.getWorld())).getHandle());
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
        this.goalSelector.addGoal(4, new RavagerMeleeAttackGoal());
        this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this,1F));
        this.targetSelector.addGoal(2, new NearestEnemyTargetGoal(this));
    }

    private class RavagerMeleeAttackGoal extends MeleeAttackGoal {
        public RavagerMeleeAttackGoal() {
            super(RavagerEntity.this, 1.0, true);
        }

        protected double getAttackReachSqr(LivingEntity entityliving) {
            float f = RavagerEntity.this.getBbWidth() - 0.1F;
            return (double)(f * 2.0F * f * 2.0F + entityliving.getBbWidth());
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
