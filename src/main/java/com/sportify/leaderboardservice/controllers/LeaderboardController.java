package com.sportify.leaderboardservice.controllers;

import com.sportify.leaderboardservice.strategy.LeaderboardContext;
import com.sportify.leaderboardservice.strategy.PointsBased;
import com.sportify.leaderboardservice.strategy.RoundsBased;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import javax.swing.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/leaderboard")
public class LeaderboardController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping (path = "/sport/{sportId}/stageStart/{stageStart}/stageEnd/{stageEnd}")
    public List<Map<String, Object>> getLeaderboard(@PathVariable int sportId, @PathVariable int stageStart, @PathVariable int stageEnd) {
        String sql = "SELECT victory_condition\n" +
                "FROM rules_for_sports\n" +
                "JOIN sports on (sports.rules_for_sport_id = sports.sport_id)\n" +
                "WHERE sport_id = ?;";
        String victory_condition = (String) jdbcTemplate.queryForObject(sql, new Object[] { sportId }, String.class);
        LeaderboardContext context = null;
        if (victory_condition.equals("points at the end"))
            context = new LeaderboardContext(new PointsBased());
        else if (victory_condition.equals("rounds at the end"))
            context = new LeaderboardContext(new RoundsBased());



        List<Map<String, Object>> leaderboard = context.executeStrategy(stageStart, stageEnd, sportId, jdbcTemplate);

        return leaderboard;
    }
}
