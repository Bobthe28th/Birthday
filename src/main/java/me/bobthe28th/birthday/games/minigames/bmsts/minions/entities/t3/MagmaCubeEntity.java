package me.bobthe28th.birthday.games.minigames.bmsts.minions.entities.t3;

import me.bobthe28th.birthday.games.minigames.bmsts.BmTeam;
import me.bobthe28th.birthday.games.minigames.bmsts.minions.Rarity;
import me.bobthe28th.birthday.games.minigames.bmsts.minions.entities.MinionEntity;
import me.bobthe28th.birthday.games.minigames.bmsts.minions.entities.NearestEnemyTargetGoal;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.MagmaCube;
import net.minecraft.world.entity.monster.Slime;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;

import java.util.EnumSet;
import java.util.Objects;

public class MagmaCubeEntity extends MagmaCube implements MinionEntity {

    BmTeam team;
    Rarity rarity;
    Boolean preview;

    public MagmaCubeEntity(Location loc, BmTeam team, Rarity rarity, Boolean preview) {
        super(EntityType.MAGMA_CUBE, ((CraftWorld) Objects.requireNonNull(loc.getWorld())).getHandle());
        this.team = team;
        this.rarity = rarity;
        this.preview = preview;
        this.setPos(loc.getX(), loc.getY(), loc.getZ());
        this.setCanPickUpLoot(false);
        this.setPersistenceRequired(true);
        this.setSize((int) Math.ceil(1*rarity.getMulti()),true);
        if (!preview) {
            this.setCustomNameVisible(true);
        }
    }

    @Override
    public void registerGoals() {
        this.goalSelector.addGoal(3, new SlimeAttackGoal(this));
        this.goalSelector.addGoal(4, new SlimeRandomDirectionGoal(this));
        this.goalSelector.addGoal(6, new SlimeKeepOnJumpingGoal(this));
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

    float getSoundPitch() {
        float f = this.isTiny() ? 1.4F : 0.8F;
        return ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) * f;
    }

    private static class SlimeMoveControl extends MoveControl {
        private float yRot;
        private int jumpDelay;
        private final MagmaCubeEntity slime;
        private boolean isAggressive;

        public SlimeMoveControl(MagmaCubeEntity entityslime) {
            super(entityslime);
            this.slime = entityslime;
            this.yRot = 180.0F * entityslime.getYRot() / 3.1415927F;
        }

        public void setDirection(float f, boolean flag) {
            this.yRot = f;
            this.isAggressive = flag;
        }

        public void setWantedMovement(double d0) {
            this.speedModifier = d0;
            this.operation = Operation.MOVE_TO;
        }

        public void tick() {
            this.mob.setYRot(this.rotlerp(this.mob.getYRot(), this.yRot, 90.0F));
            this.mob.yHeadRot = this.mob.getYRot();
            this.mob.yBodyRot = this.mob.getYRot();
            if (this.operation != Operation.MOVE_TO) {
                this.mob.setZza(0.0F);
            } else {
                this.operation = Operation.WAIT;
                if (this.mob.isOnGround()) {
                    this.mob.setSpeed((float)(this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
                    if (this.jumpDelay-- <= 0) {
                        this.jumpDelay = this.slime.getJumpDelay();
                        if (this.isAggressive) {
                            this.jumpDelay /= 3;
                        }

                        this.slime.getJumpControl().jump();
                        if (this.slime.doPlayJumpSound()) {
                            this.slime.playSound(this.slime.getJumpSound(), this.slime.getSoundVolume(), this.slime.getSoundPitch());
                        }
                    } else {
                        this.slime.xxa = 0.0F;
                        this.slime.zza = 0.0F;
                        this.mob.setSpeed(0.0F);
                    }
                } else {
                    this.mob.setSpeed((float)(this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
                }
            }

        }
    }

    private static class SlimeAttackGoal extends Goal {
        private final Slime slime;

        public SlimeAttackGoal(Slime entityslime) {
            this.slime = entityslime;
            this.setFlags(EnumSet.of(Flag.LOOK));
        }

        public boolean canUse() {
            LivingEntity entityliving = this.slime.getTarget();
            return entityliving != null && (this.slime.canAttack(entityliving) && this.slime.getMoveControl() instanceof SlimeMoveControl);
        }

        public void start() {
            super.start();
        }

        public boolean canContinueToUse() {
            LivingEntity entityliving = this.slime.getTarget();
            return entityliving != null && (this.slime.canAttack(entityliving));
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            LivingEntity entityliving = this.slime.getTarget();
            if (entityliving != null) {
                this.slime.lookAt(entityliving, 10.0F, 10.0F);
            }

            MoveControl controllermove = this.slime.getMoveControl();
            if (controllermove instanceof SlimeMoveControl entityslime_controllermoveslime) {
                entityslime_controllermoveslime.setDirection(this.slime.getYRot(), true);
            }

        }
    }

    private static class SlimeRandomDirectionGoal extends Goal {
        private final Slime slime;
        private float chosenDegrees;
        private int nextRandomizeTime;

        public SlimeRandomDirectionGoal(Slime entityslime) {
            this.slime = entityslime;
            this.setFlags(EnumSet.of(Flag.LOOK));
        }

        public boolean canUse() {
            return this.slime.getTarget() == null && (this.slime.onGround || this.slime.isInWater() || this.slime.isInLava() || this.slime.hasEffect(MobEffects.LEVITATION)) && this.slime.getMoveControl() instanceof SlimeMoveControl;
        }

        public void tick() {
            if (--this.nextRandomizeTime <= 0) {
                this.nextRandomizeTime = this.adjustedTickDelay(40 + this.slime.getRandom().nextInt(60));
                this.chosenDegrees = (float)this.slime.getRandom().nextInt(360);
            }

            MoveControl controllermove = this.slime.getMoveControl();
            if (controllermove instanceof SlimeMoveControl entityslime_controllermoveslime) {
                entityslime_controllermoveslime.setDirection(this.chosenDegrees, false);
            }

        }
    }

    private static class SlimeKeepOnJumpingGoal extends Goal {
        private final Slime slime;

        public SlimeKeepOnJumpingGoal(Slime entityslime) {
            this.slime = entityslime;
            this.setFlags(EnumSet.of(Flag.JUMP, Flag.MOVE));
        }

        public boolean canUse() {
            return !this.slime.isPassenger();
        }

        public void tick() {
            MoveControl controllermove = this.slime.getMoveControl();
            if (controllermove instanceof SlimeMoveControl entityslime_controllermoveslime) {
                entityslime_controllermoveslime.setWantedMovement(1.0);
            }

        }
    }
}
