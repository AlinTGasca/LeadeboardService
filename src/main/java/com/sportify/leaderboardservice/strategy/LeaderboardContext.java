package com.sportify.leaderboardservice.strategy;


import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

public class LeaderboardContext {
    private LeaderboardStrategy strategy;

    public LeaderboardContext(LeaderboardStrategy strategy){
        this.strategy = strategy;
    }
    public List<Map<String, Object>> executeStrategy(int stageStart, int stageEnd, int sportId, JdbcTemplate jdbcTemplate){
        String sql = strategy.calculateLeaderboard();
        List<Map<String, Object>> leaderboard = jdbcTemplate.queryForList(sql, stageStart, stageEnd, sportId);
        return leaderboard;
    }
}