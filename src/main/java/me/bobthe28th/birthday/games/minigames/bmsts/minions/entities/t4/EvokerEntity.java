package me.bobthe28th.birthday.games.minigames.bmsts.minions.entities.t4;

import me.bobthe28th.birthday.games.minigames.bmsts.BmTeam;
import me.bobthe28th.birthday.games.minigames.bmsts.minions.Rarity;
import me.bobthe28th.birthday.games.minigames.bmsts.minions.entities.MinionEntity;
import me.bobthe28th.birthday.games.minigames.bmsts.minions.entities.NearestEnemyTargetGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.monster.Evoker;
import net.minecraft.world.entity.monster.SpellcasterIllager;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;

import java.util.Objects;

public class EvokerEntity extends Evoker implements MinionEntity {

    BmTeam team;
    Rarity rarity;
    Boolean preview;

    public EvokerEntity(Location loc, BmTeam team, Rarity rarity, Boolean preview, FileConfiguration config) {
        super(EntityType.EVOKER, ((CraftWorld) Objects.requireNonNull(loc.getWorld())).getHandle());
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
        //todol new goal so it stays in range close
        this.goalSelector.addGoal(1, new EvokerCastingSpellGoal());
        this.goalSelector.addGoal(5, new EvokerAttackSpellGoal());
        this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this,1F));
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

    private class EvokerCastingSpellGoal extends SpellcasterIllager.SpellcasterCastingSpellGoal {

        public void tick() {
            if (EvokerEntity.this.getTarget() != null) {
                EvokerEntity.this.getLookControl().setLookAt(EvokerEntity.this.getTarget(), (float)EvokerEntity.this.getMaxHeadYRot(), (float)EvokerEntity.this.getMaxHeadXRot());
            }
        }
    }

    private class EvokerAttackSpellGoal extends SpellcasterIllager.SpellcasterUseSpellGoal {

        protected int getCastingTime() {
            return 40;
        }

        protected int getCastingInterval() {
            return 100;
        } //todos random?

        protected void performSpellCasting() {
            LivingEntity entityliving = EvokerEntity.this.getTarget();
            double d0 = Math.min(entityliving.getY(), EvokerEntity.this.getY());
            double d1 = Math.max(entityliving.getY(), EvokerEntity.this.getY()) + 1.0;
            float f = (float) Mth.atan2(entityliving.getZ() - EvokerEntity.this.getZ(), entityliving.getX() - EvokerEntity.this.getX());
            int i;
            if (EvokerEntity.this.distanceToSqr(entityliving) < 9.0) {
                float f1;
                for(i = 0; i < 5; ++i) {
                    f1 = f + (float)i * 3.1415927F * 0.4F;
                    this.createSpellEntity(EvokerEntity.this.getX() + (double)Mth.cos(f1) * 1.5, EvokerEntity.this.getZ() + (double)Mth.sin(f1) * 1.5, d0, d1, f1, 0);
                }

                for(i = 0; i < 8; ++i) {
                    f1 = f + (float)i * 3.1415927F * 2.0F / 8.0F + 1.2566371F;
                    this.createSpellEntity(EvokerEntity.this.getX() + (double)Mth.cos(f1) * 2.5, EvokerEntity.this.getZ() + (double)Mth.sin(f1) * 2.5, d0, d1, f1, 3);
                }
            } else {
                for(i = 0; i < 16; ++i) {
                    double d2 = 1.25 * (double)(i + 1);
                    this.createSpellEntity(EvokerEntity.this.getX() + (double)Mth.cos(f) * d2, EvokerEntity.this.getZ() + (double)Mth.sin(f) * d2, d0, d1, f, i);
                }
            }

        }

        private void createSpellEntity(double d0, double d1, double d2, double d3, float f, int i) {
            BlockPos blockposition = BlockPos.containing(d0, d3, d1);
            boolean flag = false;
            double d4 = 0.0;

            do {
                BlockPos blockposition1 = blockposition.below();
                BlockState iblockdata = EvokerEntity.this.level.getBlockState(blockposition1);
                if (iblockdata.isFaceSturdy(EvokerEntity.this.level, blockposition1, Direction.UP)) {
                    if (!EvokerEntity.this.level.isEmptyBlock(blockposition)) {
                        BlockState iblockdata1 = EvokerEntity.this.level.getBlockState(blockposition);
                        VoxelShape voxelshape = iblockdata1.getCollisionShape(EvokerEntity.this.level, blockposition);
                        if (!voxelshape.isEmpty()) {
                            d4 = voxelshape.max(Direction.Axis.Y);
                        }
                    }

                    flag = true;
                    break;
                }

                blockposition = blockposition.below();
            } while(blockposition.getY() >= Mth.floor(d2) - 1);

            if (flag) {
                EvokerEntity.this.level.addFreshEntity(new EvokerFangsEntity(EvokerEntity.this.level, d0, (double)blockposition.getY() + d4, d1, f, i, EvokerEntity.this));
            }
        }

        protected SoundEvent getSpellPrepareSound() {
            return SoundEvents.EVOKER_PREPARE_ATTACK;
        }

        protected SpellcasterIllager.IllagerSpell getSpell() {
            return IllagerSpell.FANGS;
        }
    }

}
