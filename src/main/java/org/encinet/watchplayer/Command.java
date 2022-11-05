package org.encinet.watchplayer;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.encinet.watchplayer.until.Error;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Command implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player) {// 模式变量
            switch (args.length) {
                case 0 -> sender.sendMessage("你还没有输入信息");
                case 1 -> {
                    switch (args[0]) {
                        case "apply" -> sender.sendMessage("你还没有输入要观看的玩家");
                        case "accept", "deny" -> {
                            boolean accept = "accept".equals(args[0]);
                            try {
                                Player watch = SpecManager.ProcessApply(accept, player);
                                sender.sendMessage("已成功" + (accept ? "同意" : "拒绝") + "请求");
                                watch.sendMessage(player.getName() + (accept ? "同意" : "拒绝") + "了你的请求" + (accept ? " 输入/watch stop可以退出观看" : ""));
                            } catch (Error e) {
                                sender.sendMessage(e.getMessage());
                            }
                        }
                        case "stop" -> {
                            SpecManager.stopSpec(player);
                            sender.sendMessage("成功退出观看");
                        }
                        case "kick" -> sender.sendMessage("你还没有输入要停止观看你的玩家");
                    }
                }
                case 2 -> {
                    switch (args[0]) {
                        case "apply" -> {
                            if (player.getName().equalsIgnoreCase(args[1])) {
                                sender.sendMessage("你不能申请观看自己");
                            } else if (SpecManager.isWatching(player)) {
                                sender.sendMessage("你已在观看模式");
                            } else {
                                OfflinePlayer owatched = Bukkit.getOfflinePlayer(args[1]);
                                if (owatched.hasPlayedBefore() || owatched.isOnline()) {
                                    Player watched = (Player) owatched;
                                    SpecManager.addApply(player, watched);
                                    sender.sendMessage("成功发送请求");
                                    watched.sendMessage("收到一个来自 " + player.getName() + " 的观看请求 请输入/watch accept " + player.getName() + "来同意, 或/watch deny " + player.getName() + "来拒绝");
                                } else {
                                    sender.sendMessage("找不到此玩家");
                                }
                            }
                        }
                        case "accept", "deny" -> {
                            boolean accept = "accept".equals(args[0]);

                            OfflinePlayer owatch = Bukkit.getOfflinePlayer(args[1]);
                            if (owatch.hasPlayedBefore() || owatch.isOnline()) {

                                Player watch = (Player) owatch;
                                try {
                                    SpecManager.ProcessApply(accept, watch, player);
                                    sender.sendMessage("已成功" + (accept ? "同意" : "拒绝") + "请求");
                                    watch.sendMessage(player.getName() + (accept ? "同意" : "拒绝") + "了你的请求" + (accept ? " 输入/watch stop可以退出观看" : ""));
                                } catch (Error e) {
                                    sender.sendMessage(e.getMessage());
                                }
                            } else {
                                sender.sendMessage("找不到此玩家");
                            }
                        }
                        case "kick" -> {
                            // TODO
                        }
                    }
                }
            }
        } else {
            sender.sendMessage("你不是玩家");
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender
                                                        sender, org.bukkit.command.@NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> list = new ArrayList<>();
        switch (args.length) {
            case 1 -> {
                list.add("apply");
                list.add("accept");
                list.add("deny");
                list.add("stop");
                list.add("kick");
            }
            case 2 -> {
                switch (args[0]) {
                    case "kick" -> {
                        if (sender instanceof Player player) {
                            List<Player> players = SpecManager.getWatchedList(player);
                            if (players != null) {
                                for (Player n : players) {
                                    String name = n.getName();
                                    if (determineTAB(args[1], name)) {
                                        list.add(name);
                                    }
                                }
                            }
                        }
                    }
                    case "apply" -> {
                        for (Player n : Bukkit.getOnlinePlayers()) {
                            String name = n.getName();
                            // id不能相同
                            if (!name.equalsIgnoreCase(sender.getName()) && determineTAB(args[1], name)) {
                                list.add(name);
                            }
                        }
                    }
                    case "accept", "deny" -> {
                        if (sender instanceof Player player) {
                            List<Player> players = SpecManager.getApplyList(player);
                            if (players != null) {
                                for (Player n : players) {
                                    String name = n.getName();
                                    if (determineTAB(args[1], name)) {
                                        list.add(name);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return list;
    }

    /**
     * @param user      用户文本
     * @param candidate 候选文本
     * @return 是否要显示在tab
     */
    private static boolean determineTAB(String user, String candidate) {
        int length = user.length();
        if (length > candidate.length()) {
            return false;// 长度超出候选
        } else return candidate.toLowerCase().startsWith(user.toLowerCase());
    }
}