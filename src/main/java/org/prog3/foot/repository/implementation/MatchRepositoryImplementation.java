package org.prog3.foot.repository.implementation;

import lombok.AllArgsConstructor;
import org.prog3.foot.configuration.DataSource;
import org.prog3.foot.exception.BadRequestException;
import org.prog3.foot.exception.NotFoundException;
import org.prog3.foot.models.*;
import org.prog3.foot.repository.ClubRepository;
import org.prog3.foot.repository.MatchRepository;
import org.prog3.foot.repository.SeasonRepository;
import org.prog3.foot.service.MatchService;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Repository
@AllArgsConstructor
public class MatchRepositoryImplementation implements MatchRepository {
    private final DataSource dataSource;
    private final ClubRepository clubRepository;
    private final SeasonRepository seasonRepository;

    public boolean checkSeason(Integer seasonYear) {
        Season season = seasonRepository.GetSeasons().stream().filter(s -> s.getYear().equals(seasonYear)).findFirst().orElse(null);
        if(season == null){
            throw new NotFoundException("Season not found");
        } else if (!(season.getStatus() == SeasonStatus.STARTED)) {
            throw new BadRequestException("Season is not started");
        }
        return true;
    }

    private Match createMatch(PreparedStatement ps, Club homeClub, Club awayClub,
                              LocalDateTime matchDateTime, Integer seasonYear) throws SQLException {
        String matchId = UUID.randomUUID().toString();

        ps.setString(1, matchId);
        ps.setString(2, homeClub.getId());
        ps.setString(3, awayClub.getId());
        ps.setString(4, homeClub.getStadium());
        ps.setObject(5, matchDateTime);
        ps.setInt(6, seasonYear);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                Match match = new Match();
                match.setId(rs.getString("id"));
                match.setStadium(rs.getString("stadium"));
                match.setMatchDateTime(rs.getObject("matchDateTime", LocalDateTime.class));
                match.setActualStatus(MatchStatus.valueOf(rs.getString("status")));

                // Set home club
                MatchClub homeMatchClub = new MatchClub();
                homeMatchClub.setId(rs.getString("home_id"));
                homeMatchClub.setName(rs.getString("home_name"));
                homeMatchClub.setAcronym(rs.getString("home_acronym"));
                homeMatchClub.setScore(0);
                homeMatchClub.setScorers(new ArrayList<>());
                match.setClubPlayingHome(homeMatchClub);

                // Set away club
                MatchClub awayMatchClub = new MatchClub();
                awayMatchClub.setId(rs.getString("away_id"));
                awayMatchClub.setName(rs.getString("away_name"));
                awayMatchClub.setAcronym(rs.getString("away_acronym"));
                awayMatchClub.setScore(0);
                awayMatchClub.setScorers(new ArrayList<>());
                match.setClubPlayingAway(awayMatchClub);

                return match;
            }
        }
        throw new RuntimeException("Failed to create match");
    }

    /**
     * @Description Create all matches for a specific season including all clubs
     * @param seasonYear/ the season which host the championship
     * @return the list of match created
     */
    @Override
    public List<Match> matchMaker(Integer seasonYear) {
        if (checkSeason(seasonYear)) {
            List<Club> allClubs = clubRepository.getClubs();
            int numClubs = allClubs.size();
            List<Match> matches = new ArrayList<>();

            // Insert matches SQL
            String insertMatchSql = """
    WITH inserted_match AS (
        INSERT INTO "Match" (id, "homeClubId", "awayClubId", stadium, "matchDatetime", status, "seasonId")
        VALUES (?, ?, ?, ?, ?, 'NOT_STARTED', (SELECT id FROM "Season" WHERE year = ?))
        RETURNING *
    )
    SELECT 
        im.id, im."matchDatetime", im.stadium, im.status,
        h.id as home_id, h.name as home_name, h.acronym as home_acronym,
        a.id as away_id, a.name as away_name, a.acronym as away_acronym
    FROM inserted_match im
    JOIN "Club" h ON im."homeClubId" = h.id
    JOIN "Club" a ON im."awayClubId" = a.id
    """;
            try (Connection con = dataSource.getConnection();
                 PreparedStatement ps = con.prepareStatement(insertMatchSql)) {

                // Start date from January 1st of the season year at 20:00
                LocalDateTime startDate = LocalDateTime.of(seasonYear, 1, 1, 20, 0);
                int matchDay = 0;

                // Create matches for each pair of teams
                for (int i = 0; i < numClubs; i++) {
                    for (int j = i + 1; j < numClubs; j++) {
                        // Home match
                        Match homeMatch = createMatch(ps, allClubs.get(i), allClubs.get(j), 
                            startDate.plusDays(matchDay), seasonYear);
                        matches.add(homeMatch);

                        // Away match (reverse fixture)
                        Match awayMatch = createMatch(ps, allClubs.get(j), allClubs.get(i), 
                            startDate.plusDays(matchDay + numClubs - 1), seasonYear);
                        matches.add(awayMatch);

                        matchDay++;
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            return matches;
        }
        throw new RuntimeException("Something went wrong");
    }




    /**
     * @Description Create all matches for a specific season including all clubs
     * @param seasonYear/ the season which host the championship
     * @return the list of match created
     */
    @Override
    public List<Match> getMatches(Integer seasonYear) {
        String sql = """
        WITH match_goals AS (
            SELECT 
                g."matchId",
                g."clubId",
                g."playerId",
                g."minuteOfGoal",
                g."isOwnGoal",
                p.name as player_name,
                p.number as player_number,
                COUNT(*) OVER (PARTITION BY g."matchId", g."clubId") as club_score
            FROM "Goal" g
            JOIN "Player" p ON p.id = g."playerId"
        )
        SELECT 
            m.id, m."matchDatetime", m.stadium, m.status,
            h.id as home_id, h.name as home_name, h.acronym as home_acronym,
            a.id as away_id, a.name as away_name, a.acronym as away_acronym,
            hg."playerId" as home_scorer_id, hg.player_name as home_scorer_name,
            hg.player_number as home_scorer_number, hg."minuteOfGoal" as home_minute,
            hg."isOwnGoal" as home_own_goal, COALESCE(hg.club_score, 0) as home_score,
            ag."playerId" as away_scorer_id, ag.player_name as away_scorer_name,
            ag.player_number as away_scorer_number, ag."minuteOfGoal" as away_minute,
            ag."isOwnGoal" as away_own_goal, COALESCE(ag.club_score, 0) as away_score
        FROM "Match" m
        JOIN "Season" s ON s.id = m."seasonId"
        JOIN "Club" h ON h.id = m."homeClubId"
        JOIN "Club" a ON a.id = m."awayClubId"
        LEFT JOIN match_goals hg ON hg."matchId" = m.id AND hg."clubId" = m."homeClubId"
        LEFT JOIN match_goals ag ON ag."matchId" = m.id AND ag."clubId" = m."awayClubId"
        WHERE s.year = ?
        ORDER BY m."matchDatetime" ASC
        """;

        Map<String, Match> matchesMap = new HashMap<>();

        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, seasonYear);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String matchId = rs.getString("id");

                    Match match = matchesMap.computeIfAbsent(matchId, k -> {
                        try {
                            Match m = new Match();
                            m.setId(matchId);
                            m.setStadium(rs.getString("stadium"));
                            m.setMatchDateTime(rs.getObject("matchDatetime", LocalDateTime.class));
                            m.setActualStatus(MatchStatus.valueOf(rs.getString("status")));

                            // Home club
                            MatchClub home = new MatchClub();
                            home.setId(rs.getString("home_id"));
                            home.setName(rs.getString("home_name"));
                            home.setAcronym(rs.getString("home_acronym"));
                            home.setScore(rs.getInt("home_score"));
                            home.setScorers(new ArrayList<>());
                            m.setClubPlayingHome(home);

                            // Away club
                            MatchClub away = new MatchClub();
                            away.setId(rs.getString("away_id"));
                            away.setName(rs.getString("away_name"));
                            away.setAcronym(rs.getString("away_acronym"));
                            away.setScore(rs.getInt("away_score"));
                            away.setScorers(new ArrayList<>());
                            m.setClubPlayingAway(away);

                            return m;
                        } catch (SQLException e) {
                            throw new RuntimeException("Erreur lors de la lecture des données du match ID=" + matchId, e);
                        }
                    });

                    try {
                        // Home scorers
                        if (rs.getString("home_scorer_id") != null) {
                            addScorer(match.getClubPlayingHome().getScorers(),
                                    rs.getString("home_scorer_id"),
                                    rs.getString("home_scorer_name"),
                                    rs.getInt("home_scorer_number"),
                                    rs.getInt("home_minute"),
                                    rs.getBoolean("home_own_goal"));
                        }

                        // Away scorers
                        if (rs.getString("away_scorer_id") != null) {
                            addScorer(match.getClubPlayingAway().getScorers(),
                                    rs.getString("away_scorer_id"),
                                    rs.getString("away_scorer_name"),
                                    rs.getInt("away_scorer_number"),
                                    rs.getInt("away_minute"),
                                    rs.getBoolean("away_own_goal"));
                        }

                    } catch (SQLException e) {
                        throw new RuntimeException("Erreur lors de l'ajout d’un buteur pour le match ID=" + matchId, e);
                    }
                }

            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des matchs pour l’année " + seasonYear, e);
        }

        return new ArrayList<>(matchesMap.values());
    }


    private void addScorer(List<Scorer> scorers, String playerId, String playerName, 
                          int playerNumber, int minute, boolean isOwnGoal) {
        PlayerMinimumInfo player = new PlayerMinimumInfo();
        player.setId(playerId);
        player.setName(playerName);
        player.setNumber(playerNumber);

        Scorer scorer = new Scorer();
        scorer.setId(player.getId());
        scorer.setName(player.getName());
        scorer.setNumber(player.getNumber());
        scorer.setMinuteOfGoal(minute);
        scorer.setOwnGoal(isOwnGoal);

        scorers.add(scorer);
    }

    /**
     * @Description Change a specific match status
     * @param id the id of the match
     * @param status the pretending status
     * @return
     */

    @Override
    public Match updateMatchStatus(String id, MatchStatus status) {
        // First get the current match
        String getMatchSql = """
            SELECT m.id, m.stadium, m."matchDatetime", m.status,
                   h.id as home_id, h.name as home_name, h.acronym as home_acronym,
                   a.id as away_id, a.name as away_name, a.acronym as away_acronym,
                   (SELECT COUNT(*) FROM "Goal" g WHERE g."matchId" = m.id AND g."clubId" = m."homeClubId") as home_score,
                   (SELECT COUNT(*) FROM "Goal" g WHERE g."matchId" = m.id AND g."clubId" = m."awayClubId") as away_score
            FROM "Match" m
            JOIN "Club" h ON h.id = m."homeClubId"
            JOIN "Club" a ON a.id = m."awayClubId"
            WHERE m.id = ?
            """;

        try (Connection con = dataSource.getConnection()) {
            Match match = null;
            
            // Get current match
            try (PreparedStatement ps = con.prepareStatement(getMatchSql)) {
                ps.setString(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new NotFoundException("Match not found");
                    }
                    
                    match = new Match();
                    match.setId(rs.getString("id"));
                    match.setStadium(rs.getString("stadium"));
                    match.setMatchDateTime(rs.getObject("matchDatetime", LocalDateTime.class));
                    match.setActualStatus(MatchStatus.valueOf(rs.getString("status")));

                    // Set home club with score
                    MatchClub homeClub = new MatchClub();
                    homeClub.setId(rs.getString("home_id"));
                    homeClub.setName(rs.getString("home_name"));
                    homeClub.setAcronym(rs.getString("home_acronym"));
                    homeClub.setScore(rs.getInt("home_score"));
                    homeClub.setScorers(new ArrayList<>());
                    match.setClubPlayingHome(homeClub);

                    // Set away club with score
                    MatchClub awayClub = new MatchClub();
                    awayClub.setId(rs.getString("away_id"));
                    awayClub.setName(rs.getString("away_name"));
                    awayClub.setAcronym(rs.getString("away_acronym"));
                    awayClub.setScore(rs.getInt("away_score"));
                    awayClub.setScorers(new ArrayList<>());
                    match.setClubPlayingAway(awayClub);
                }
            }

            // Check if transition is valid
            if (!match.transitionStatus(status)) {
                throw new BadRequestException("Invalid status transition");
            }

            // Update match status
            String updateSql = "UPDATE \"Match\" SET status = ?::\"MatchStatus\" WHERE id = ?";
            try (PreparedStatement ps = con.prepareStatement(updateSql)) {
                ps.setString(1, status.name());
                ps.setString(2, id);
                ps.executeUpdate();
            }

            // If match is finished, update club rankings
            if (status == MatchStatus.FINISHED) {
                updateClubRankings(con, match);
            }

            // Get updated match with scorers
            return getMatchWithScorers(con, id);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateClubRankings(Connection con, Match match) throws SQLException {
        int homeScore = match.getClubPlayingHome().getScore();
        int awayScore = match.getClubPlayingAway().getScore();
        
        // Determine points based on match result
        int homePoints = homeScore > awayScore ? 3 : (homeScore == awayScore ? 1 : 0);
        int awayPoints = awayScore > homeScore ? 3 : (homeScore == awayScore ? 1 : 0);

        // Update rankings in the database
        String updateRankingSql = """
            INSERT INTO "ClubRanking" ("clubId", "seasonId", points)
            VALUES (?, (SELECT "seasonId" FROM "Match" WHERE id = ?), ?)
            ON CONFLICT ("clubId", "seasonId") DO UPDATE 
            SET points = "ClubRanking".points + EXCLUDED.points
            """;

        try (PreparedStatement ps = con.prepareStatement(updateRankingSql)) {
            // Update home club points
            ps.setString(1, match.getClubPlayingHome().getId());
            ps.setString(2, match.getId());
            ps.setInt(3, homePoints);
            ps.executeUpdate();

            // Update away club points
            ps.setString(1, match.getClubPlayingAway().getId());
            ps.setString(2, match.getId());
            ps.setInt(3, awayPoints);
            ps.executeUpdate();
        }
    }

    private Match getMatchWithScorers(Connection con, String matchId) throws SQLException {
        String sql = """
            WITH match_goals AS (
                SELECT 
                    g."matchId",
                    g."clubId",
                    g."playerId",
                    g."minuteOfGoal",
                    g."isOwnGoal",
                    p.name as player_name,
                    p.number as player_number,
                    COUNT(*) OVER (PARTITION BY g."matchId", g."clubId") as club_score
                FROM "Goal" g
                JOIN "Player" p ON p.id = g."playerId"
                WHERE g."matchId" = ?
            )
            SELECT 
                m.id, m."matchDatetime", m.stadium, m.status,
                h.id as home_id, h.name as home_name, h.acronym as home_acronym,
                a.id as away_id, a.name as away_name, a.acronym as away_acronym,
                hg."playerId" as home_scorer_id, hg.player_name as home_scorer_name,
                hg.player_number as home_scorer_number, hg."minuteOfGoal" as home_minute,
                hg."isOwnGoal" as home_own_goal, COALESCE(hg.club_score, 0) as home_score,
                ag."playerId" as away_scorer_id, ag.player_name as away_scorer_name,
                ag.player_number as away_scorer_number, ag."minuteOfGoal" as away_minute,
                ag."isOwnGoal" as away_own_goal, COALESCE(ag.club_score, 0) as away_score
            FROM "Match" m
            JOIN "Club" h ON h.id = m."homeClubId"
            JOIN "Club" a ON a.id = m."awayClubId"
            LEFT JOIN match_goals hg ON hg."matchId" = m.id AND hg."clubId" = m."homeClubId"
            LEFT JOIN match_goals ag ON ag."matchId" = m.id AND ag."clubId" = m."awayClubId"
            WHERE m.id = ?
            """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, matchId);
            ps.setString(2, matchId);

            Map<String, Match> matchMap = new HashMap<>();
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Match match = matchMap.computeIfAbsent(matchId, k -> {
                        Match m = new Match();
                        try {
                            m.setId(matchId);
                            m.setStadium(rs.getString("stadium"));
                            m.setMatchDateTime(rs.getObject("matchDatetime", LocalDateTime.class));
                            m.setActualStatus(MatchStatus.valueOf(rs.getString("status")));

                            MatchClub home = new MatchClub();
                            home.setId(rs.getString("home_id"));
                            home.setName(rs.getString("home_name"));
                            home.setAcronym(rs.getString("home_acronym"));
                            home.setScore(rs.getInt("home_score"));
                            home.setScorers(new ArrayList<>());
                            m.setClubPlayingHome(home);

                            MatchClub away = new MatchClub();
                            away.setId(rs.getString("away_id"));
                            away.setName(rs.getString("away_name"));
                            away.setAcronym(rs.getString("away_acronym"));
                            away.setScore(rs.getInt("away_score"));
                            away.setScorers(new ArrayList<>());
                            m.setClubPlayingAway(away);
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                        return m;
                    });

                    // Add home scorer if exists
                    if (rs.getString("home_scorer_id") != null) {
                        addScorer(match.getClubPlayingHome().getScorers(),
                                rs.getString("home_scorer_id"),
                                rs.getString("home_scorer_name"),
                                rs.getInt("home_scorer_number"),
                                rs.getInt("home_minute"),
                                rs.getBoolean("home_own_goal"));
                    }

                    // Add away scorer if exists
                    if (rs.getString("away_scorer_id") != null) {
                        addScorer(match.getClubPlayingAway().getScorers(),
                                rs.getString("away_scorer_id"),
                                rs.getString("away_scorer_name"),
                                rs.getInt("away_scorer_number"),
                                rs.getInt("away_minute"),
                                rs.getBoolean("away_own_goal"));
                    }
                }
            }

            return matchMap.get(matchId);
        }
    }

    /**
     *
     * @param matchId the indentifier of the match to add goals
     * @param goals the list of goal: a goal is looking like that:{
     *     "clubId": "string",
     *     "scorerIdentifier": "string",
     *     "minuteOfGoal": 0
     *   }
     * @JsonReturnType:
     * {
     *   "id": "string",
     *   "clubPlayingHome": {
     *     "id": "string",
     *     "name": "string",
     *     "acronym": "RMA",
     *     "score": 0,
     *     "scorers": [
     *       {
     *         "player": {
     *           "id": "string",
     *           "name": "string",
     *           "number": 0
     *         },
     *         "minuteOfGoal": 0,
     *         "ownGoal": true
     *       }
     *     ]
     *   },
     *   "clubPlayingAway": {
     *     "id": "string",
     *     "name": "string",
     *     "acronym": "RMA",
     *     "score": 0,
     *     "scorers": [
     *       {
     *         "player": {
     *           "id": "string",
     *           "name": "string",
     *           "number": 0
     *         },
     *         "minuteOfGoal": 0,
     *         "ownGoal": true
     *       }
     *     ]
     *   },
     *   "stadium": "string",
     *   "matchDatetime": "2025-05-04T10:04:29.236Z",
     *   "actualStatus": "NOT_STARTED"
     * }
     * @return the match
     */
    @Override
    public Match addGoals(String matchId, List<AddGoal> goals) {
        // First check if match exists and is in STARTED status
        String checkMatchSql = """
            SELECT status FROM "Match" WHERE id = ?
            """;
        
        try (Connection con = dataSource.getConnection()) {
            // Check match status
            try (PreparedStatement ps = con.prepareStatement(checkMatchSql)) {
                ps.setString(1, matchId);
                ResultSet rs = ps.executeQuery();
                if (!rs.next()) {
                    throw new NotFoundException("Match not found");
                }
                MatchStatus status = MatchStatus.valueOf(rs.getString("status"));
                if (status != MatchStatus.STARTED) {
                    throw new BadRequestException("Goals can only be added to matches in STARTED status");
                }
            }

            // Insert goals
            String insertGoalSql = """
                INSERT INTO "Goal" (id, "matchId", "playerId", "clubId", "minuteOfGoal", "isOwnGoal")
                VALUES (?, ?, ?, ?, ?, false)
                """;

            try (PreparedStatement ps = con.prepareStatement(insertGoalSql)) {
                for (AddGoal goal : goals) {
                    // Validate minute of goal
                    if (goal.getMinuteOfGoal() < 1 || goal.getMinuteOfGoal() > 90) {
                        throw new BadRequestException("Minute of goal must be between 1 and 90");
                    }

                    ps.setString(1, UUID.randomUUID().toString());
                    ps.setString(2, matchId);
                    ps.setString(3, goal.getScorerIdentifier());
                    ps.setString(4, goal.getClubId());
                    ps.setInt(5, goal.getMinuteOfGoal());
                    ps.executeUpdate();
                }
            }

            // Return updated match with new goals
            return getMatchWithScorers(con, matchId);
        } catch (SQLException e) {
            throw new RuntimeException("Error adding goals to match: " + e.getMessage());
        }
    }
}
