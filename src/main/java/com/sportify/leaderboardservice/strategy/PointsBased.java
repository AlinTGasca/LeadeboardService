package com.sportify.leaderboardservice.strategy;

public class PointsBased implements LeaderboardStrategy {

    @Override
    public String calculateLeaderboard() {
        return "WITH stage_matches AS\n" +
                "(SELECT *\n" +
                "FROM matches\n" +
                "WHERE stage_id BETWEEN ? AND ?)\n" +
                "SELECT teams.name, \n" +
                "(SELECT count (*)\n" +
                "FROM stage_matches\n" +
                "WHERE (first_team_id = teams.team_id or second_team_id = teams.team_id)) AS no_matches,\n" +
                "(rules_for_sports.point_per_victory * (SELECT count(stage_matches.match_id)\n" +
                "FROM stage_matches\n" +
                "WHERE (first_team_id = teams.team_id or second_team_id = teams.team_id) AND \n" +
                "(SELECT COALESCE(sum(point_value),0)\n" +
                "FROM points\n" +
                "RIGHT OUTER JOIN rounds ON (points.round_id = rounds.round_id)\n" +
                "WHERE team_score_id = teams.team_id AND stage_matches.match_id = rounds.match_id)\n" +
                "> (SELECT COALESCE(sum(point_value),0)\n" +
                "FROM points\n" +
                "RIGHT OUTER JOIN rounds ON (points.round_id = rounds.round_id)\n" +
                "WHERE team_score_id != teams.team_id and stage_matches.match_id = rounds.match_id)) +\n" +
                "(SELECT count(stage_matches.match_id)\n" +
                "FROM stage_matches\n" +
                "WHERE (first_team_id = teams.team_id or second_team_id = teams.team_id) AND \n" +
                "(SELECT COALESCE(sum(point_value),0)\n" +
                "FROM points\n" +
                "RIGHT OUTER JOIN rounds ON (points.round_id = rounds.round_id)\n" +
                "WHERE team_score_id = teams.team_id AND stage_matches.match_id = rounds.match_id)\n" +
                "= (SELECT COALESCE(sum(point_value),0) \n" +
                "FROM points\n" +
                "RIGHT OUTER JOIN rounds ON (points.round_id = rounds.round_id)\n" +
                "WHERE team_score_id != teams.team_id and stage_matches.match_id = rounds.match_id))) AS points\n" +
                "FROM teams JOIN sports ON (sports.sport_id = teams.sport_id) \n" +
                "JOIN rules_for_sports on (rules_for_sports.rules_for_sport_id = sports.rules_for_sport_id)\n" +
                "WHERE teams.sport_id = ? and teams.accepted = true\n" +
                "ORDER BY points DESC;" ;
    }
}