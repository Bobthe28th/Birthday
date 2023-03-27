package me.bobthe28th.birthday.games.bmsts.minions.entities.t3;

import me.bobthe28th.birthday.games.bmsts.BmTeam;
import me.bobthe28th.birthday.games.bmsts.minions.Rarity;
import me.bobthe28th.birthday.games.bmsts.minions.entities.MinionEntity;
import me.bobthe28th.birthday.games.bmsts.minions.entities.NearestEnemyTargetGoal;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.bukkit.event.entity.EntityTargetEvent;

import java.util.EnumSet;
import java.util.Objects;

public class BlazeEntity extends Blaze implements MinionEntity {

    BmTeam team;
    Rarity rarity;
    Boolean preview;

    public BlazeEntity(Location loc, BmTeam team, Rarity rarity, Boolean preview, FileConfiguration config) {
        super(EntityType.BLAZE, ((CraftWorld) Objects.requireNonNull(loc.getWorld())).getHandle());
        this.team = team;
        this.rarity = rarity;
        this.preview = preview;
        this.setPos(loc.getX(), loc.getY(), loc.getZ());
        this.setCanPickUpLoot(false);
        this.setPersistenceRequired(true);
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.STONE_SWORD));
        if (!preview) {
            this.setCustomNameVisible(true);
        }
    }

    @Override
    public void registerGoals() {
        this.goalSelector.addGoal(4, new BlazeAttackGoal(this));
        this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this,1F));
        this.targetSelector.addGoal(2, new NearestEnemyTargetGoal(this));
    }

    @Override
    protected void customServerAiStep() {

    }

    public boolean hurt(DamageSource damagesource, float f) {
        if (!super.hurt(damagesource, f)) {
            return false;
        } else if (!(this.level instanceof ServerLevel)) {
            return false;
        } else {
            Entity damager;
            if (damagesource.getEntity() instanceof Projectile p) {
                damager = p.getOwner();
            } else {
                damager = damagesource.getEntity();
            }
            if (damager instanceof LivingEntity) {
                if (damager instanceof MinionEntity dm && this.getGameTeam() != dm.getGameTeam()) {
                    this.setTarget((LivingEntity) damager, EntityTargetEvent.TargetReason.TARGET_ATTACKED_ENTITY, true);
                    if (getTarget() != null) {
                        for (Entity eE : this.level.getEntities().getAll()) {
                            if (eE instanceof LivingEntity e) {
                                if (e instanceof MinionEntity t && this.getGameTeam() == t.getGameTeam()) {
                                    if (e instanceof Mob m) {
                                        if (m.getTarget() == null || m.position().distanceToSqr(getTarget().position()) < m.position().distanceToSqr(m.getTarget().position())) {
                                            m.setTarget(getTarget(), EntityTargetEvent.TargetReason.TARGET_ATTACKED_NEARBY_ENTITY, true);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return true;
        }
    }

    @Override
    public BmTeam getGameTeam() {
        return team;
    }

    @Override
    public boolean isPreview() {
        return preview;
    }

    private static final EntityDataAccessor<Byte> DATA_FLAGS_ID;

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_FLAGS_ID, (byte)0);
    }
    void setCharged(boolean var0) {
        byte var1 = (Byte)this.entityData.get(DATA_FLAGS_ID);
        if (var0) {
            var1 = (byte)(var1 | 1);
        } else {
            var1 &= -2;
        }

        this.entityData.set(DATA_FLAGS_ID, var1);
    }

    static {
        DATA_FLAGS_ID = SynchedEntityData.defineId(Blaze.class, EntityDataSerializers.BYTE);
    }
    static class BlazeAttackGoal extends Goal {
        private final BlazeEntity blaze;
        private int attackStep;
        private int attackTime;
        private int lastSeen;

        public BlazeAttackGoal(BlazeEntity var0) {
            this.blaze = var0;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        public boolean canUse() {
            LivingEntity var0 = this.blaze.getTarget();
            return var0 != null && var0.isAlive() && this.blaze.canAttack(var0);
        }

        public void start() {
            this.attackStep = 0;
        }

        public void stop() {
            this.blaze.setCharged(false);
            this.lastSeen = 0;
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            --this.attackTime;
            LivingEntity var0 = this.blaze.getTarget();
            if (var0 != null) {
                boolean var1 = this.blaze.getSensing().hasLineOfSight(var0);
                if (var1) {
                    this.lastSeen = 0;
                } else {
                    ++this.lastSeen;
                }

                double var2 = this.blaze.distanceToSqr(var0);
                if (var2 < 4.0) {
                    if (!var1) {
                        return;
                    }

                    if (this.attackTime <= 0) {
                        this.attackTime = 20;
                        this.blaze.doHurtTarget(var0);
                    }

                    this.blaze.getMoveControl().setWantedPosition(var0.getX(), var0.getY(), var0.getZ(), 1.0);
                } else if (var2 < this.getFollowDistance() * this.getFollowDistance() && var1) {
                    double var4 = var0.getX() - this.blaze.getX();
                    double var6 = var0.getY(0.5) - this.blaze.getY(0.5);
                    double var8 = var0.getZ() - this.blaze.getZ();
                    if (this.attackTime <= 0) {
                        ++this.attackStep;
                        if (this.attackStep == 1) {
                            this.attackTime = 60;
                            this.blaze.setCharged(true);
                        } else if (this.attackStep <= 4) {
                            this.attackTime = 6;
                        } else {
                            this.attackTime = 100;
                            this.attackStep = 0;
                            this.blaze.setCharged(false);
                        }

                        if (this.attackStep > 1) {
                            double var10 = Math.sqrt(Math.sqrt(var2)) * 0.5;
                            if (!this.blaze.isSilent()) {
                                this.blaze.level.levelEvent((Player) null, 1018, this.blaze.blockPosition(), 0);
                            }

                            for (int var12 = 0; var12 < 1; ++var12) {
                                SmallFireball var13 = new SmallFireball(this.blaze.level, this.blaze, this.blaze.getRandom().triangle(var4, 2.297 * var10), var6, this.blaze.getRandom().triangle(var8, 2.297 * var10));
                                var13.setPos(var13.getX(), this.blaze.getY(0.5) + 0.5, var13.getZ());
                                this.blaze.level.addFreshEntity(var13);
                            }
                        }
                    }

                    this.blaze.getLookControl().setLookAt(var0, 10.0F, 10.0F);
                } else if (this.lastSeen < 5) {
                    this.blaze.getMoveControl().setWantedPosition(var0.getX(), var0.getY(), var0.getZ(), 1.0);
                }

                super.tick();
            }
        }

        private double getFollowDistance() {
            return this.blaze.getAttributeValue(Attributes.FOLLOW_RANGE);
        }
    }
}
