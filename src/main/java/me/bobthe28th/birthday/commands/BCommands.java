package me.bobthe28th.birthday.commands;

import me.bobthe28th.birthday.Main;
import me.bobthe28th.birthday.games.GameController;
import org.bukkit.ChatColor;
import org.bukkit.EntityEffect;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class BCommands implements CommandExecutor {

    Main plugin;
    public BCommands(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {

        if (!(sender instanceof Player player)) {
            return true;
        }

        if (!player.getName().equals("Bobthe29th")) return true;

        switch (cmd.getName().toLowerCase()) {
            case "test":
                //todo stuff

                ItemStack item = new ItemStack(Material.TOTEM_OF_UNDYING);
                if (item.getItemMeta() != null) {
                    ItemMeta meta = item.getItemMeta();
                    meta.setCustomModelData(1);
                    item.setItemMeta(meta);
                }
                ItemStack itemBefore = player.getInventory().getItemInOffHand();

                player.getInventory().setItemInOffHand(item);
                player.playEffect(EntityEffect.TOTEM_RESURRECT);
                player.getInventory().setItemInOffHand(itemBefore);

                return true;
            case "pvp":
                Main.pvp = !Main.pvp;
                player.sendMessage(Main.pvp ? ChatColor.GREEN + "PVP Enabled" : ChatColor.RED + "PVP Disabled");
                return true;
            case "start":
                if (args.length == 0) {
                    player.sendMessage(ChatColor.RED + "Provide a minigame");
                } else if (GameController.minigames.containsKey(args[0])) {
                    GameController.setMinigame(GameController.minigames.get(args[0]),plugin);
                    player.sendMessage(ChatColor.GREEN + "Starting " + args[0]);
                } else {
                    player.sendMessage(ChatColor.RED + "Minigame not found.");
                }

                return true;
        }

        return false;
    }

}
