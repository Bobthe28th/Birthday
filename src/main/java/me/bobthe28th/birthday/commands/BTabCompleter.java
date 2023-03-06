package me.bobthe28th.birthday.commands;

import me.bobthe28th.birthday.games.GameController;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class BTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        switch (command.getName().toLowerCase()) {
            case "start":
                if (args.length == 1) {
                    return new ArrayList<>(GameController.minigames.keySet());
                }
                break;
        }
        return new ArrayList<>();
    }

}
