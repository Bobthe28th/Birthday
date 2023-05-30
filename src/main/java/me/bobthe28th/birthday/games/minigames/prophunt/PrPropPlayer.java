package me.bobthe28th.birthday.games.minigames.prophunt;

import me.bobthe28th.birthday.Main;
import me.bobthe28th.birthday.games.GamePlayer;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.Random;

public class PrPropPlayer extends PrPlayer {

    BlockDisplay localPropEntity;
    BlockDisplay globalPropEntity;
    Slime globalPropEntityHitBox;
    ArmorStand globalPropEntityParent;
    int timeToSolidMax = 60;
    int timeToSolid = timeToSolidMax;
    Location spawnLoc;
    boolean hide = false;

    public PrPropPlayer(Main plugin, GamePlayer player, PropHunt propHunt) {
        super(plugin, player, propHunt, PrPlayerType.PROP);
    }

    @Override
    public void giveItems() {

    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public void spawn(Location l) {
        if (l.getWorld() == null) return;
        player.getPlayer().teleport(l);
        hide = true;
        localPropEntity = l.getWorld().spawn(l, BlockDisplay.class);
        player.getPlayer().addPassenger(localPropEntity);
        localPropEntity.setTransformation(new Transformation(new Vector3f(-0.5f,-1.35f,-0.5f), new AxisAngle4f(), new Vector3f(1, 1, 1), new AxisAngle4f()));
        propHunt.hideProp(this);

        globalPropEntityParent = l.getWorld().spawn(l, ArmorStand.class);
        globalPropEntity = l.getWorld().spawn(l, BlockDisplay.class);
        globalPropEntityHitBox = l.getWorld().spawn(l, Slime.class);
        globalPropEntity.setTransformation(new Transformation(new Vector3f(-0.5f,0f,-0.5f), new AxisAngle4f(), new Vector3f(1, 1, 1), new AxisAngle4f()));
        globalPropEntityHitBox.setSize(2);
        globalPropEntityHitBox.setAI(false);
        globalPropEntityHitBox.setCollidable(false);
        globalPropEntityHitBox.setInvulnerable(true);
        globalPropEntityHitBox.setInvisible(true);

        globalPropEntityParent.setMarker(true);
        globalPropEntityParent.setInvulnerable(true);
        globalPropEntityParent.setGravity(false);
        globalPropEntityParent.setVisible(false);
        globalPropEntityParent.addPassenger(globalPropEntity);

        player.getPlayer().hideEntity(plugin,globalPropEntity);
        player.getPlayer().hideEntity(plugin,globalPropEntityParent);

        spawnLoc = l;
        setRandomBlockType();
    }

    public void setRandomBlockType() {
        Random random = new Random(player.getPlayer().getDisplayName().hashCode());
        BlockData data = propHunt.currentMap.props.get(random.nextInt(propHunt.currentMap.props.size())).createBlockData();
        localPropEntity.setBlock(data);
        globalPropEntity.setBlock(data);
    }

    @Override
    public void removePr() {
        propHunt.showProp(this);
        localPropEntity.remove();
        globalPropEntity.remove();
        globalPropEntityParent.remove();
        globalPropEntityHitBox.remove();
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getPlayer() != player.getPlayer()) return;
        if (event.getTo() != null && event.getTo().toVector().equals(event.getFrom().toVector())) return;
        if (globalPropEntityParent != null) {
            Location l = event.getTo();
            l.setYaw(0);
            l.setPitch(0);
            globalPropEntityParent.removePassenger(globalPropEntity);
            globalPropEntityParent.teleport(l);
            globalPropEntityParent.addPassenger(globalPropEntity);
            globalPropEntityHitBox.teleport(l);
        }
    }
}
