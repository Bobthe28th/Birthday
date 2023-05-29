package me.bobthe28th.birthday.games.minigames.bmsts.minions.entities.t1;

import me.bobthe28th.birthday.games.minigames.bmsts.BmTeam;
import me.bobthe28th.birthday.games.minigames.bmsts.minions.Rarity;
import me.bobthe28th.birthday.games.minigames.bmsts.minions.entities.MinionEntity;
import me.bobthe28th.birthday.games.minigames.bmsts.minions.entities.NearestEnemyTargetGoal;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.animal.horse.Llama;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;

import java.util.Objects;

public class LlamaEntity extends Llama implements MinionEntity {

    BmTeam team;
    Rarity rarity;
    Boolean preview;

    public LlamaEntity(Location loc, BmTeam team, Rarity rarity, Boolean preview) {
        super(EntityType.LLAMA, ((CraftWorld) Objects.requireNonNull(loc.getWorld())).getHandle());
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
    public void registerGoals() {
        this.goalSelector.addGoal(3, new RangedAttackGoal(this, 1.25, 40, 15.0F));
        this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this,1F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(2, new NearestEnemyTargetGoal(this));
    }

    @Override
    public boolean hurt(DamageSource damagesource, float f) {
        return minionHurt(this,super.hurt(damagesource,f), damagesource, f);
    }

    @Override
    public BmTeam getGameTeam() {
        return team;
    }

    @Override
    public boolean isPreview() {
        return preview;
    }
}
