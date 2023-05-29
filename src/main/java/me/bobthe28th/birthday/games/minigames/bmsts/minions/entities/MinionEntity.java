package me.bobthe28th.birthday.games.minigames.bmsts.minions.entities;

import me.bobthe28th.birthday.games.minigames.bmsts.minions.Minion;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.Projectile;
import org.bukkit.event.entity.EntityTargetEvent;

public interface MinionEntity {

    Minion getMinion();
    boolean isPreview();

    default boolean minionHurt(Mob self, boolean hurt, DamageSource damagesource, float f) {
        if (!hurt) {
            return false;
        } else if (!(self.level instanceof ServerLevel)) {
            return false;
        } else {
            Entity damager;
            if (damagesource.getEntity() instanceof Projectile p) {
                damager = p.getOwner();
            } else {
                damager = damagesource.getEntity();
            }
            if (damager instanceof LivingEntity) {
                if (damager instanceof MinionEntity dm && this.getMinion().getTeam() != dm.getMinion().getTeam()) {
                    self.setTarget((LivingEntity) damager, EntityTargetEvent.TargetReason.TARGET_ATTACKED_ENTITY, true);
                    if (self.getTarget() != null) {
                        for (Entity eE : self.level.getEntities().getAll()) {
                            if (eE instanceof LivingEntity e) {
                                if (e instanceof MinionEntity t && this.getMinion().getTeam() == t.getMinion().getTeam()) {
                                    if (e instanceof Mob m) {
                                        if (m.getTarget() == null || m.position().distanceToSqr(self.getTarget().position()) < m.position().distanceToSqr(m.getTarget().position())) {
                                            m.setTarget(self.getTarget(), EntityTargetEvent.TargetReason.TARGET_ATTACKED_NEARBY_ENTITY, true);
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
}
