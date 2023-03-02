package me.bobthe28th.birthday;

import me.bobthe28th.birthday.commands.Commands;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        Bukkit.broadcastMessage("Man");

        Commands commands = new Commands(this);
        getCommand("test").setExecutor(commands);

    }

}
