package me.bobthe28th.birthday.commands;

import me.bobthe28th.birthday.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Commands implements CommandExecutor {

    Main plugin;
    public Commands(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {

        if (!(sender instanceof Player player)) {
            return true;
        }

        switch (cmd.getName().toLowerCase()) {
            case "test":
                //todo stuff

                Bukkit.broadcastMessage("test2");
//                Pig pig = (Pig) player.getWorld().spawnEntity(player.getLocation(), EntityType.PIG);

//                PacketContainer packet = new PacketContainer(PacketType.Play.Server.CAMERA);
//                packet.getIntegers().write(0,pig.getEntityId());


//                PacketContainer info = new PacketContainer(PacketType.Play.Server.PLAYER_INFO);
//                info.getPlayerInfoAction().write(0, EnumWrappers.PlayerInfoAction.ADD_PLAYER);
//                info.getPlayerInfoAction().write(0, EnumWrappers.PlayerInfoAction.ADD_PLAYER);

//                PacketContainer packet = new PacketContainer(PacketType.Play.Server.PLAYER_INFO);
//                packet.getPlayerInfoAction().write(0, EnumWrappers.PlayerInfoAction.);

//                PacketContainer packet = new PacketContainer(PacketType.Play.Server.EXPLOSION);
//                packet.getDoubles()
//                    .write(0,player.getLocation().getX())
//                    .write(1,player.getLocation().getY())
//                    .write(2,player.getLocation().getZ());
//                packet.getFloat().write(0,3.0F);

//                try {
//                    ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }

//                PacketContainer add = new PacketContainer(PacketType.Play.Server.PLAYER_INFO);
//                add.getPlayerInfoAction().write(0, EnumWrappers.PlayerInfoAction.ADD_PLAYER);
//                WrappedGameProfile profile = new WrappedGameProfile(player.getUniqueId(), player.getName());
//                WrappedChatComponent name = WrappedChatComponent.fromText(profile.getName());
//                List<PlayerInfoData> playerData = new ArrayList<>();
//                playerData.add(new PlayerInfoData(profile,0,EnumWrappers.NativeGameMode.CREATIVE, name));
//                add.getPlayerInfoDataLists().write(0, playerData);
//
//                PacketContainer spawn = new PacketContainer(PacketType.Play.Server.NAMED_ENTITY_SPAWN);
//                spawn.getIntegers().write(0, 70);
//                spawn.getUUIDs().write(0, player.getUniqueId());
//                spawn.getDoubles().write(0, player.getLocation().getX()).write(1, player.getLocation().getY()).write(2, player.getLocation().getZ());
//
//                PacketContainer outerSkin = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
//                outerSkin.getIntegers().write(0, 70);
//                WrappedDataWatcher watcher = new WrappedDataWatcher(player);
//                watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(17, WrappedDataWatcher.Registry.get(Byte.class)), (byte) 127);
//                outerSkin.getWatchableCollectionModifier().write(0, watcher.getWatchableObjects());
//
//                try {
//                    ProtocolLibrary.getProtocolManager().sendServerPacket(player, add);
//                    ProtocolLibrary.getProtocolManager().sendServerPacket(player, spawn);
//                    ProtocolLibrary.getProtocolManager().sendServerPacket(player, outerSkin);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }

                return true;
        }

        return false;
    }

}
