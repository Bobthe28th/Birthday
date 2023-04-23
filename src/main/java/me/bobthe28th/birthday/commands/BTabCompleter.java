package me.bobthe28th.birthday.commands;

import me.bobthe28th.birthday.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        switch (command.getName().toLowerCase()) {
            case "start":
                if (args.length == 1) {
                    return new ArrayList<>(Main.gameController.minigames.keySet());
                }
                break;
            case "music":
                switch (args.length) {
                    case 1:
                        return Arrays.asList("start","play","stop","skip","list","clear");
                    case 2:
                        if (args[0].equals("play")) {
                            return Arrays.asList("now","queue");
                        }
                        break;
                    case 3:
                        if (args[0].equals("play")) {
                            if (args[1].equals("now")) {
                                return Main.musicController.getMusicNameList();
                            } else if (args[1].equals("queue")) {
                                return Arrays.asList("list","loop");
                            }
                        }
                        break;
                    case 4:
                        if (args[0].equals("play") && args[1].equals("queue")) {
                            return Main.musicController.getMusicNameList();
                        }
                        break;
                }
                break;
        }
        return new ArrayList<>();
    }

}
