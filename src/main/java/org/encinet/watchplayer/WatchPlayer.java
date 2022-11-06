package org.encinet.watchplayer;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.encinet.watchplayer.event.InventoryManager;
import org.encinet.watchplayer.event.PlayerEvent;

import java.util.Objects;

public final class WatchPlayer extends JavaPlugin {
    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getPluginManager().registerEvents(new PlayerEvent(), this);
        Bukkit.getPluginManager().registerEvents(new InventoryManager(), this);
        Objects.requireNonNull(Bukkit.getPluginCommand("watch")).setExecutor(new Command());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        SpecManager.stopAllSpec();
    }
}
