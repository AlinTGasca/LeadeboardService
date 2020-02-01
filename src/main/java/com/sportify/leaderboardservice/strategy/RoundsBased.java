package com.sportify.leaderboardservice.strategy;

public class RoundsBased implements LeaderboardStrategy {
    @Override
    public String calculateLeaderboard() {
        return "WITH stage_matches AS\n" +
                "(SELECT *\n" +
                "FROM matches\n" +
                "WHERE date_time_end IS NOT NULL AND stage_id BETWEEN ? AND ?)\n" +
                "SELECT teams.name,\n" +
                "(SELECT count (*)\n" +
                "FROM stage_matches\n" +
                "WHERE (first_team_id = teams.team_id or second_team_id = teams.team_id)) AS no_matches,\n" +
                "(rules_for_sports.point_per_victory * (SELECT count(stage_matches.match_id)\n" +
                "FROM stage_matches\n" +
                "WHERE (first_team_id = teams.team_id or second_team_id = teams.team_id) AND\n" +
                "(SELECT count(*)\n" +
                "FROM rounds AS first_team_rounds\n" +
                "WHERE first_team_rounds.match_id = stage_matches.match_id AND\n" +
                "(SELECT COALESCE(sum(point_value),0)\n" +
                "FROM points\n" +
                "WHERE team_against_id != teams.team_id AND points.round_id = first_team_rounds.round_id)\n" +
                "> (SELECT COALESCE(sum(point_value),0)\n" +
                "FROM points\n" +
                "WHERE team_against_id = teams.team_id AND points.round_id = first_team_rounds.round_id))\n" +
                ">(SELECT count(*)\n" +
                "FROM rounds AS second_team_rounds\n" +
                "WHERE second_team_rounds.match_id = stage_matches.match_id AND\n" +
                "(SELECT COALESCE(sum(point_value),0)\n" +
                "FROM points\n" +
                "WHERE team_against_id = teams.team_id AND second_team_rounds.round_id = second_team_rounds.round_id)\n" +
                "> (SELECT COALESCE(sum(point_value),0)\n" +
                "FROM points\n" +
                "WHERE team_against_id != teams.team_id and second_team_rounds.round_id = second_team_rounds.round_id))\n" +
                "))AS points\n" +
                "FROM teams JOIN sports ON (sports.sport_id = teams.sport_id)\n" +
                "JOIN rules_for_sports on (rules_for_sports.rules_for_sport_id = sports.rules_for_sport_id)\n" +
                "WHERE teams.sport_id = ? and teams.accepted = true\n" +
                "ORDER BY points DESC;\n";
    }
}
