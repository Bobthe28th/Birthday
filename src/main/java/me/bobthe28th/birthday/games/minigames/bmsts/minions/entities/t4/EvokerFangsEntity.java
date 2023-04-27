package me.bobthe28th.birthday.games.minigames.bmsts.minions.entities.t4;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.EvokerFangs;
import net.minecraft.world.level.Level;
import org.bukkit.craftbukkit.v1_19_R3.event.CraftEventFactory;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class EvokerFangsEntity extends EvokerFangs {
    public EvokerFangsEntity(Level world, double d0, double d1, double d2, float f, int i, LivingEntity entityliving) {
        super(world, d0, d1, d2, f, i, entityliving);
        this.warmupDelayTicks = i;
        this.setOwner(entityliving);
        this.lifeTicks = 22;
    }
    private int warmupDelayTicks;
    private boolean sentSpikeEvent;
    private int lifeTicks;
    private boolean clientSideAttackStarted;
    @Nullable
    private LivingEntity owner;
    @Nullable
    private UUID ownerUUID;

    @Override
    public void setOwner(@Nullable LivingEntity entityliving) {
        this.owner = entityliving;
        this.ownerUUID = entityliving == null ? null : entityliving.getUUID();
    }

    @Override
    public @Nullable LivingEntity getOwner() {
        if (this.owner == null && this.ownerUUID != null && this.level instanceof ServerLevel) {
            Entity entity = ((ServerLevel)this.level).getEntity(this.ownerUUID);
            if (entity instanceof LivingEntity) {
                this.owner = (LivingEntity)entity;
            }
        }

        return this.owner;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag nbttagcompound) {
        this.warmupDelayTicks = nbttagcompound.getInt("Warmup");
        if (nbttagcompound.hasUUID("Owner")) {
            this.ownerUUID = nbttagcompound.getUUID("Owner");
        }

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag nbttagcompound) {
        nbttagcompound.putInt("Warmup", this.warmupDelayTicks);
        if (this.ownerUUID != null) {
            nbttagcompound.putUUID("Owner", this.ownerUUID);
        }

    }

    @Override
    public void tick() {
        super.tick();
        if (this.level.isClientSide) {
            if (this.clientSideAttackStarted) {
                --this.lifeTicks;
                if (this.lifeTicks == 14) {
                    for(int i = 0; i < 12; ++i) {
                        double d0 = this.getX() + (this.random.nextDouble() * 2.0 - 1.0) * (double)this.getBbWidth() * 0.5;
                        double d1 = this.getY() + 0.05 + this.random.nextDouble();
                        double d2 = this.getZ() + (this.random.nextDouble() * 2.0 - 1.0) * (double)this.getBbWidth() * 0.5;
                        double d3 = (this.random.nextDouble() * 2.0 - 1.0) * 0.3;
                        double d4 = 0.3 + this.random.nextDouble() * 0.3;
                        double d5 = (this.random.nextDouble() * 2.0 - 1.0) * 0.3;
                        this.level.addParticle(ParticleTypes.CRIT, d0, d1 + 1.0, d2, d3, d4, d5);
                    }
                }
            }
        } else if (--this.warmupDelayTicks < 0) {
            if (this.warmupDelayTicks == -8) {
                List<LivingEntity> list = this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(0.2, 0.0, 0.2));
                Iterator iterator = list.iterator();

                while(iterator.hasNext()) {
                    LivingEntity entityliving = (LivingEntity)iterator.next();
                    this.dealDamageTo(entityliving);
                }
            }

            if (!this.sentSpikeEvent) {
                this.level.broadcastEntityEvent(this, (byte)4);
                this.sentSpikeEvent = true;
            }

            if (--this.lifeTicks < 0) {
                this.discard();
            }
        }

    }

    private void dealDamageTo(LivingEntity entityliving) {
        LivingEntity entityliving1 = this.getOwner();
        if (entityliving.isAlive() && !entityliving.isInvulnerable() && entityliving != entityliving1) {
            if (entityliving1 == null) {
                CraftEventFactory.entityDamage = this;
                entityliving.hurt(this.damageSources().magic(), 6.0F);
                CraftEventFactory.entityDamage = null;
            } else {
//                if (entityliving1.isAlliedTo(entityliving)) {
//                    return;
//                }

                entityliving.hurt(this.damageSources().indirectMagic(this, entityliving1), 6.0F);
            }
        }

    }

    @Override
    public void handleEntityEvent(byte b0) {
        super.handleEntityEvent(b0);
        if (b0 == 4) {
            this.clientSideAttackStarted = true;
            if (!this.isSilent()) {
                this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.EVOKER_FANGS_ATTACK, this.getSoundSource(), 1.0F, this.random.nextFloat() * 0.2F + 0.85F, false);
            }
        }

    }

    @Override
    public float getAnimationProgress(float f) {
        if (!this.clientSideAttackStarted) {
            return 0.0F;
        } else {
            int i = this.lifeTicks - 2;
            return i <= 0 ? 1.0F : 1.0F - ((float)i - f) / 20.0F;
        }
    }
}
