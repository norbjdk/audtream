package com.audtream.desktop.manager;

import com.audtream.desktop.model.dto.UserStatsDTO;
import com.audtream.desktop.model.entity.User;
import com.audtream.desktop.model.event.*;
import com.audtream.desktop.service.UserService;
import java.io.IOException;

public class UserDataManager {
    private static UserDataManager instance;
    private final UserService userService;
    private UserStatsDTO cachedStats;
    private long lastStatsFetch;
    private static final long STATS_CACHE_TTL = 60000;

    private UserDataManager() {
        this.userService = new UserService();
        this.lastStatsFetch = 0;
    }

    public static UserDataManager getInstance() {
        if (instance == null) {
            instance = new UserDataManager();
        }
        return instance;
    }

    public User getUserByUsername(String username) throws IOException {
        return userService.getUserByUsername(username);
    }

    public UserStatsDTO getArtistStats(boolean forceRefresh) throws IOException {
        long now = System.currentTimeMillis();
        if (!forceRefresh && cachedStats != null && (now - lastStatsFetch) < STATS_CACHE_TTL) {
            return cachedStats;
        }

        cachedStats = userService.getArtistStats();
        lastStatsFetch = now;
        EventBus.getInstance().publish(new DataRefreshedEvent("user_stats"));
        return cachedStats;
    }

    public void clearCache() {
        cachedStats = null;
        lastStatsFetch = 0;
    }
}