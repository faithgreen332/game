package org.tinygame.herostory;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Author: ljf
 * CreatedAt: 2021/4/12 下午11:04
 */
public final class UserManager {

    private static final ConcurrentHashMap<Integer, User> _userMap = new ConcurrentHashMap<>();

    private UserManager() {
    }

    public static void addUser(User u) {
        _userMap.putIfAbsent(u.userId, u);
    }

    public static void removeUser(Integer userId) {
        _userMap.remove(userId);
    }

    public static Collection<? extends User> listUsers() {
        return _userMap.values();
    }
}
