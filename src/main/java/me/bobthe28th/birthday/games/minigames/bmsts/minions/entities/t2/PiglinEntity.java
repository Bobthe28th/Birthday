package me.bobthe28th.birthday.games.minigames.bmsts.minions.entities.t2;

import com.google.common.collect.ImmutableList;
import me.bobthe28th.birthday.games.minigames.bmsts.minions.Minion;
import me.bobthe28th.birthday.games.minigames.bmsts.minions.entities.MinionEntity;
import me.bobthe28th.birthday.games.minigames.bmsts.minions.entities.NearestEnemyTargetGoal;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.monster.piglin.Piglin;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class PiglinEntity extends Piglin implements MinionEntity {

    Minion minion;
    boolean preview;

    public PiglinEntity(Location loc, Minion minion, Boolean preview) {
        super(EntityType.PIGLIN, ((CraftWorld) Objects.requireNonNull(loc.getWorld())).getHandle());
        this.minion = minion;
        this.preview = preview;
        this.setPos(loc.getX(), loc.getY(), loc.getZ());
        this.setCanPickUpLoot(false);
        this.setPersistenceRequired(true);
        this.setImmuneToZombification(true);
        this.setAggressive(true);
        this.setItemInHand(InteractionHand.MAIN_HAND, CraftItemStack.asNMSCopy(new ItemStack(Material.GOLDEN_SWORD)));
        if (!preview) {
            this.setCustomNameVisible(true);
        }
    }

    @Override
    protected Brain.Provider<Piglin> brainProvider() {
        return Brain.provider(MEMORY_TYPES, ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS));
    }

    @Override
    public void registerGoals() {
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0, false));
        this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this,1F));
        this.targetSelector.addGoal(2, new NearestEnemyTargetGoal(this));
    }

    @Override
    public boolean hurt(DamageSource damagesource, float f) {
        return minionHurt(this,super.hurt(damagesource,f), damagesource, f);
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource randomsource, DifficultyInstance difficultydamagescaler) {}

    @Override
    public Minion getMinion() {
        return minion;
    }

    @Override
    public boolean isPreview() {
        return preview;
    }

}
