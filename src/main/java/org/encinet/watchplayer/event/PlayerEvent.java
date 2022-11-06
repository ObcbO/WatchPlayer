package org.encinet.watchplayer.event;

import com.destroystokyo.paper.event.player.PlayerAdvancementCriterionGrantEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.encinet.watchplayer.SpecManager;

import java.util.Set;

public class PlayerEvent implements Listener {
    @EventHandler
    public void playerToggleSneakEvent(PlayerToggleSneakEvent event) {
        if (SpecManager.isWatching(event.getPlayer())) {
            // 取消观看玩家的shift事件
            // 防止观看玩家退出视角
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void playerGameModeChangeEvent(PlayerGameModeChangeEvent event) {
        Player player = event.getPlayer();
        if (SpecManager.isWatching(player)) {
            // 取消观看玩家的更改游戏模式事件
            // 防止观看玩家切换世界造成的游戏模式更改
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void playerMoveEvent(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        // 防止玩家以各种奇怪的方式退出指定视角
        if (SpecManager.isWatched(event.getPlayer())) {
            forceTarget(player);
        } else if (SpecManager.isWatching(event.getPlayer())) {
            player.setSpectatorTarget(null);
            player.setSpectatorTarget(Bukkit.getEntity(SpecManager.getWatchingStore(player).view().getUniqueId()));
        }
    }

    @EventHandler
    public void playerChangedWorldEvent(PlayerChangedWorldEvent event) {
        // 如果被观看者传送 观看者也传送
        Player watched = event.getPlayer();
        if (SpecManager.isWatched(watched)) {
            forceTarget(watched);
        }
    }

    @EventHandler
    public void playerTeleportEvent(PlayerTeleportEvent event) {
        // 如果被观看者传送 观看者也传送
        Player watched = event.getPlayer();
        if (SpecManager.isWatched(watched)) {
            forceTarget(watched);
        }
    }

    @EventHandler
    public void playerQuitEvent(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (SpecManager.isWatched(player)) {
            // 如果被观看者退出 所有观看者都退出观看模式
            Set<Player> watchingPlayers = SpecManager.getWatchedList(player);
            for (Player n : watchingPlayers) {
                SpecManager.stopSpec(n);
                n.sendMessage("由于" + player.getName() + "退出了游戏 所以退出观看");
            }
            SpecManager.removeWatched(player);
        } else if (SpecManager.isWatching(player)) {
            Player watched = SpecManager.getWatchingStore(player).view();
            SpecManager.removeWatchedPlayers(player, watched);
            SpecManager.removeWatching(player);
        }
    }

    @EventHandler
    public void playerDeathEvent(PlayerDeathEvent event) {
        Player player = event.getPlayer();
        if (SpecManager.isWatched(player)) {
            // 如果被观看者死亡 所有观看者都退出观看模式
            Set<Player> watchingPlayers = SpecManager.getWatchedList(player);
            for (Player n : watchingPlayers) {
                SpecManager.stopSpec(n);
                n.sendMessage("由于" + player.getName() + "死亡 所以退出观看");
            }
            SpecManager.removeWatched(player);
        } else if (SpecManager.isWatching(player)) {
            // 如果观看者死亡 则退出模式
            SpecManager.stopSpec(player);
            player.sendMessage("由于你死亡 所以退出观看");
        }
    }

    @EventHandler
    public void playerAdvancementCriterionGrantEvent(PlayerAdvancementCriterionGrantEvent event) {
        // 观看者不能获得进度
        Player player = event.getPlayer();
        if (SpecManager.isWatching(player)) {
            event.setCancelled(true);
        }
    }

    private static void forceTarget(Player player) {
        Set<Player> set = SpecManager.getWatchedList(player);
        for (Player n : set) {
            // Minecraft特性
            n.setSpectatorTarget(null);
            n.setSpectatorTarget(Bukkit.getEntity(player.getUniqueId()));
        }
    }
}
