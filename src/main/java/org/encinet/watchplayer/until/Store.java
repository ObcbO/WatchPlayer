package org.encinet.watchplayer.until;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public record Store(Player view, Location location, GameMode gameMode, ItemStack[] inventories, ItemStack[] armorStacks) {
}
