package org.encinet.watchplayer.until;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public record Store(Player view, Location location, GameMode gameMode) {
}
