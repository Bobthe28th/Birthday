package me.bobthe28th.birthday.games.minigames.bmsts.minions;

import org.bukkit.ChatColor;

public enum Rarity {
    COMMON(ChatColor.GREEN,13,1f),
    RARE(ChatColor.BLUE,6,1.125f),
    LEGENDARY(ChatColor.GOLD,4,1.25f),
    GODLIKE(ChatColor.DARK_RED,2,1.375f),
    AWESOME(ChatColor.MAGIC,1,1.5f);

    final ChatColor color;
    final int weight;
    final float multi;
    Rarity(ChatColor color, int weight, float multi) {
        this.color = color;
        this.weight = weight;
        this.multi = multi;
    }
    public ChatColor getColor() {
        return color;
    }
    public int getWeight() {
        return weight;
    }
    public float getMulti() {
        return multi;
    }
}
