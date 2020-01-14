package com.sportify.leaderboardservice.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/leaderboard")
public class LeaderboardController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping (path = "/sport/{sportId}/stageStart/{stageStart}/stageEnd/{stageEnd}")
    public List<Map<String, Object>> getLeaderboard(@PathVariable int sportId, @PathVariable int stageStart, @PathVariable int stageEnd) {
        String sql = "WITH stage_matches AS\n" +
                "(SELECT *\n" +
                "FROM matches\n" +
                "WHERE stage_id BETWEEN ? AND ?)\n" +
                "SELECT teams.name, \n" +
                "(SELECT count (*)\n" +
                "FROM stage_matches\n" +
                "WHERE (first_team_id = teams.team_id or second_team_id = teams.team_id)) AS no_matches,\n" +
                "(rules_for_sports.point_per_victory * (SELECT count(stage_matches.match_id)\n" +
                "FROM stage_matches\n" +
                "WHERE (first_team_id = teams.team_id or second_team_id = teams.team_id) AND (select count(*) \n" +
                "FROM points\n" +
                "RIGHT OUTER JOIN rounds ON (points.round_id = rounds.round_id)\n" +
                "WHERE team_score_id = teams.team_id AND stage_matches.match_id = rounds.match_id)\n" +
                "> (SELECT count(*) \n" +
                "FROM points\n" +
                "RIGHT OUTER JOIN rounds ON (points.round_id = rounds.round_id)\n" +
                "WHERE team_score_id != teams.team_id and stage_matches.match_id = rounds.match_id)) +\n" +
                "(SELECT count(stage_matches.match_id)\n" +
                "FROM stage_matches\n" +
                "WHERE (first_team_id = teams.team_id or second_team_id = teams.team_id) AND (select count(*) \n" +
                "FROM points\n" +
                "RIGHT OUTER JOIN rounds ON (points.round_id = rounds.round_id)\n" +
                "WHERE team_score_id = teams.team_id AND stage_matches.match_id = rounds.match_id)\n" +
                "= (SELECT count(*) \n" +
                "FROM points\n" +
                "RIGHT OUTER JOIN rounds ON (points.round_id = rounds.round_id)\n" +
                "WHERE team_score_id != teams.team_id and stage_matches.match_id = rounds.match_id))) AS points\n" +
                "FROM teams JOIN sports ON (sports.sport_id = teams.sport_id) \n" +
                "JOIN rules_for_sports on (rules_for_sports.rules_for_sport_id = sports.rules_for_sport_id)\n" +
                "WHERE teams.sport_id = ? and teams.accepted = true\n" +
                "ORDER BY points DESC;" ;

        List<Map<String, Object>> leaderboard = jdbcTemplate.queryForList(sql, stageStart, stageEnd, sportId);

        return leaderboard;
    }

    @GetMapping (path = "/test")
    public HashMap<String, String> getTest() {
        HashMap<String, String> map = new HashMap<>();
        map.put("key", "value");
        map.put("foo", "bar");
        map.put("aa", "bb");
        return map;
    }
}
