package me.bobthe28th.birthday.commands;

import me.bobthe28th.birthday.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
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
                return true;
            case "pvp":
                Main.pvp = !Main.pvp;
                player.sendMessage(Main.pvp ? ChatColor.GREEN + "PVP Enabled" : ChatColor.RED + "PVP Disabled");
                return true;
        }

        return false;
    }

}
