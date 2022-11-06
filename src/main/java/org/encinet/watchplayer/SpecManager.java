package org.encinet.watchplayer;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.encinet.watchplayer.until.Error;
import org.encinet.watchplayer.until.Process;
import org.encinet.watchplayer.until.Store;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static org.bukkit.GameMode.SPECTATOR;

public class SpecManager {
    private static final Map<Player, Set<Player>> apply = new ConcurrentHashMap<>();// 申请map key是被观看者 value是观看者列表
    private static final Map<Player, Store> watching = new ConcurrentHashMap<>();// 观看者为key的map
    private static final Map<Player, Set<Player>> watched = new ConcurrentHashMap<>();// 被观看者为key的map

    public static void addApply(Player applicant, Player respondent) {
        Process.addSet(apply, respondent, applicant);
    }
    private static void removeApply(Player applicant, Player respondent) {
        Process.removeSet(apply, respondent, applicant);
    }
    public static void addWatched(Player applicant, Player respondent) {
        Process.addSet(watched, respondent, applicant);
    }
    // 移除父项内的子项
    public static void removeWatchedPlayers(Player applicant, Player respondent) {
        Process.removeSet(watched, respondent, applicant);
    }
    // 移除父项
    public static void removeWatched(Player player) {
        watched.remove(player);
    }

    /**
     * 处理申请
     *
     * @param accept     同意或拒绝
     * @param applicant  申请人
     * @param respondent 被申请人
     * @throws Error 返回错误文本
     */
    public static void ProcessApply(boolean accept, Player applicant, Player respondent) throws Error {
        if (apply.containsKey(respondent)) {
            Set<Player> list = apply.get(respondent);
            if (list.contains(applicant)) {
                removeApply(applicant, respondent);
                if (accept) {
                    startSpec(applicant, respondent);
                }
            } else throw new Error("尚未找到该申请人");
        } else throw new Error("尚未找到申请人");
    }

    /**
     * 在没有申请人的情况下处理申请
     *
     * @param accept     同意或拒绝
     * @param respondent 被申请人
     * @return 申请人
     * @throws Error 返回错误文本
     */
    public static Player ProcessApply(boolean accept, Player respondent) throws Error {
        Player watch;
        if (apply.containsKey(respondent)) {
            Set<Player> set = apply.get(respondent);
            if (set.size() == 1) {
                watch = (Player) set.toArray()[0];
                removeApply(watch, respondent);
                if (accept) {
                    startSpec(watch, respondent);
                }
            } else throw new Error("申请人数量大于一 请制定申请人");
        } else throw new Error("尚未找到申请人");
        return watch;
    }

    /**
     * 储存并切换模式
     *
     * @param watch   观看者
     * @param watched 被观看者
     */
    private static void startSpec(Player watch, Player watched) throws Error {
        if (watched.getGameMode() == SPECTATOR) {
            throw new Error(watched.getName() + "也处于观看模式");
        } else if (!watched.isOnline()) {
            throw new Error(watched.getName() + "已退出游戏");
        } else {
            GameMode gamemode = watch.getGameMode();
            Location location = watch.getLocation();
            watch.setGameMode(SPECTATOR);
            watch.setSpectatorTarget(Bukkit.getEntity(watched.getUniqueId()));
            watching.put(watch, new Store(watched, location, gamemode));
            addWatched(watch, watched);
        }
    }

    /**
     * 退出观看
     *
     * @param player 观看者
     */
    public static void stopSpec(Player player) {
        if (watching.containsKey(player)) {
            Store store = watching.get(player);
            Player watched = store.view();
            removeWatchedPlayers(player, watched);
            watching.remove(player);
            player.setSpectatorTarget(null);
            player.teleport(store.location());
            player.setGameMode(store.gameMode());// 要先退出才能改模式，要不然事件会被取消
            if (watched.isOnline()) {
                watched.sendMessage(player.getName() + "退出了观看");
            }
        }
    }

    /**
     * 将所有观看者退出观看
     * 目前此方法仅用于插件卸载事件
     */
    public static void stopAllSpec() {
        for (Map.Entry<Player, Store> entry : watching.entrySet()) {
            stopSpec(entry.getKey());
        }
    }

    /**
     * @param player 玩家
     * @return 玩家是否处在观看状态
     */
    public static boolean isWatching(Player player) {
        return watching.containsKey(player);
    }
    /**
     * 删除观看玩家
     *
     * @param player 玩家
     */
    public static void removeWatching(Player player) {
        watching.remove(player);
    }
    /**
     * @param player 玩家
     * @return 玩家是否处在被观看状态
     */
    public static boolean isWatched(Player player) {
        return watched.containsKey(player);
    }
    /**
     * @param player 玩家
     * @return 返回被观看玩家的观看者列表
     */
    public static Set<Player> getWatchedList(Player player) {
        return watched.getOrDefault(player, null);
    }
    /**
     * @param player 玩家
     * @return 返回观看玩家的储存信息
     */
    public static Store getWatchingStore(Player player) {
        return watching.getOrDefault(player, null);
    }
    /**
     * @param player 玩家
     * @return 返回玩家的申请观看列表
     */
    public static Set<Player> getApplyList(Player player) {
        return apply.getOrDefault(player, null);
    }
}
