package org.encinet.watchplayer.until;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class Process {
    /**
     * 删除内容, 在list为0的时候删掉这个key
     *
     * @param parent 父项
     * @param child 子项
     */
    public static <T> void removeSet(Map<T, Set<T>> map, T parent, T child) {
        if (map.containsKey(parent)) {
            Set<T> set = map.get(parent);
            set.remove(child);
            if (set.size() == 0) {
                map.remove(parent);
            } else {
                map.put(parent, set);
            }
        }
    }
    /**
     * 添加内容
     *
     * @param parent 父项
     * @param child 子项
     */
    public static <T> void addSet(Map<T, Set<T>> map, T parent, T child) {
        if (map.containsKey(parent)) {
            map.get(parent).add(child);
        } else {
            Set<T> set = new CopyOnWriteArraySet<>();
            set.add(child);
            map.put(parent, set);
        }
    }
}
