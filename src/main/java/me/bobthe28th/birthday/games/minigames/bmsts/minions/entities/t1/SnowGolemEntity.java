package me.bobthe28th.birthday.games.minigames.bmsts.minions.entities.t1;

import me.bobthe28th.birthday.games.minigames.bmsts.minions.Minion;
import me.bobthe28th.birthday.games.minigames.bmsts.minions.entities.MinionEntity;
import me.bobthe28th.birthday.games.minigames.bmsts.minions.entities.NearestEnemyTargetGoal;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.animal.SnowGolem;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;

import java.util.Objects;

public class SnowGolemEntity extends SnowGolem implements MinionEntity {

    Minion minion;
    boolean preview;

    public SnowGolemEntity(Location loc, Minion minion, Boolean preview) {
        super(EntityType.SNOW_GOLEM, ((CraftWorld) Objects.requireNonNull(loc.getWorld())).getHandle());
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
        this.goalSelector.addGoal(1, new RangedAttackGoal(this, 1.25, 20, 10.0F));
        this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this,1F));
        this.targetSelector.addGoal(2, new NearestEnemyTargetGoal(this));
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
