package org.encinet.watchplayer.until;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class Process {
    /**
     * 删除内容, 在list为0的时候删掉这个key
     *
     * @param parent 父项
     * @param child 子项
     */
    public static <T> void removeList(Map<T, List<T>> map, T parent, T child) {
        if (map.containsKey(parent)) {
            List<T> list = map.get(parent);
            list.remove(child);
            if (list.size() == 0) {
                map.remove(parent);
            } else {
                map.put(parent, list);
            }
        }
    }
    /**
     * 添加内容
     *
     * @param parent 父项
     * @param child 子项
     */
    public static <T> void addList(Map<T, List<T>> map,T parent, T child) {
        if (map.containsKey(parent)) {
            map.get(parent).add(child);
        } else {
            List<T> list = new CopyOnWriteArrayList<>();
            list.add(child);
            map.put(parent, list);
        }
    }
}
