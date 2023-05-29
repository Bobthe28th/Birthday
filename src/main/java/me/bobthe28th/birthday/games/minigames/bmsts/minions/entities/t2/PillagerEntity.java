package me.bobthe28th.birthday.games.minigames.bmsts.minions.entities.t2;

import me.bobthe28th.birthday.games.minigames.bmsts.BmTeam;
import me.bobthe28th.birthday.games.minigames.bmsts.minions.Rarity;
import me.bobthe28th.birthday.games.minigames.bmsts.minions.entities.MinionEntity;
import me.bobthe28th.birthday.games.minigames.bmsts.minions.entities.NearestEnemyTargetGoal;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.RangedCrossbowAttackGoal;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;

import java.util.Objects;

public class PillagerEntity extends Pillager implements MinionEntity {

    BmTeam team;
    Rarity rarity;
    Boolean preview;

    public PillagerEntity(Location loc, BmTeam team, Rarity rarity, Boolean preview) {
        super(EntityType.PILLAGER, ((CraftWorld) Objects.requireNonNull(loc.getWorld())).getHandle());
//        DifficultyInstance d = ((CraftWorld) loc.getWorld()).getHandle().getCurrentDifficultyAt(new BlockPos(loc.getX(),loc.getY(),loc.getZ()));
        this.team = team;
        this.rarity = rarity;
        this.preview = preview;
        this.setPos(loc.getX(), loc.getY(), loc.getZ());
        this.setCanPickUpLoot(false);
        this.setPersistenceRequired(true);
        this.setPatrolLeader(false);
//        this.populateDefaultEquipmentSlots(this.getRandom(),d);
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.CROSSBOW));
        if (!preview) {
            this.setCustomNameVisible(true);
        }
    }

    @Override
    public void registerGoals() {
        this.goalSelector.addGoal(3, new RangedCrossbowAttackGoal<>(this, 1.0, 8.0F));
        this.goalSelector.addGoal(8, new RandomStrollGoal(this, 0.6));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(2, new NearestEnemyTargetGoal(this));
    }

    @Override
    public boolean hurt(DamageSource damagesource, float f) {
        return minionHurt(this,super.hurt(damagesource,f), damagesource, f);
    }

    protected void enchantSpawnedWeapon(RandomSource var0, float var1) {}

    @Override
    public BmTeam getGameTeam() {
        return team;
    }

    @Override
    public boolean isPreview() {
        return preview;
    }

}
