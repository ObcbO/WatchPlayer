package org.encinet.watchplayer.event;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.encinet.watchplayer.SpecManager;

import java.util.Set;

public class InventoryManager implements Listener {
    @EventHandler
    public void playerInventoryEvent(InventoryInteractEvent event) {
        // 背包更新
        Player player = Bukkit.getPlayer(event.getEventName());
        if (SpecManager.isWatched(player)) {
            for (HumanEntity he : event.getViewers()) {
                Set<Player> set = SpecManager.getWatchedList(player);
                for (Player n : set) {
                    n.getInventory().clear();
                    n.getInventory().setContents(he.getInventory().getContents());
                    n.getInventory().setArmorContents(he.getInventory().getArmorContents());
                }
            }
        }
    }

    @EventHandler
    public void InventoryClickEvent(InventoryClickEvent event) {
        // 防止观看者移动背包物品
        Player player = (Player) event.getWhoClicked();
        if (SpecManager.isWatching(player)) {
            event.setCancelled(true);
        }
    }
}
