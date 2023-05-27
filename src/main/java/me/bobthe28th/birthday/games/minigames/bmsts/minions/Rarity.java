package me.bobthe28th.birthday.games.minigames.bmsts.minions;

import org.bukkit.ChatColor;

public enum Rarity {
    COMMON(ChatColor.GREEN,5),
    RARE(ChatColor.BLUE,5),
    LEGENDARY(ChatColor.GOLD,3),
    GODLIKE(ChatColor.DARK_RED,2),
    AWESOME(ChatColor.MAGIC,1);

    final ChatColor color;
    final int weight;
    Rarity(ChatColor color, int weight) {
        this.color = color;
        this.weight = weight;
    }
    public ChatColor getColor() {
        return color;
    }
    public int getWeight() {
        return weight;
    }
}
