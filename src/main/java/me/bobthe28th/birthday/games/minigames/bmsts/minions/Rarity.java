package me.bobthe28th.birthday.games.minigames.bmsts.minions;

import org.bukkit.ChatColor;

public enum Rarity {
    COMMON(ChatColor.GREEN),
    RARE(ChatColor.BLUE),
    LEGENDARY(ChatColor.GOLD),
    GODLIKE(ChatColor.DARK_RED),
    AWESOME(ChatColor.MAGIC);

    final ChatColor color;
    Rarity(ChatColor color) {
        this.color = color;
    }
    public ChatColor getColor() {
        return color;
    }
}
