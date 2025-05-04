package org.prog3.foot.repository.implementation;

import lombok.AllArgsConstructor;
import org.prog3.foot.configuration.DataSource;
import org.prog3.foot.exception.BadRequestException;
import org.prog3.foot.exception.NotFoundException;
import org.prog3.foot.models.*;
import org.prog3.foot.repository.MatchRepository;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
@AllArgsConstructor
public class MatchRepositoryImplementation implements MatchRepository {
    private final DataSource dataSource;


    /**
     * @Description Create all matches for a specific season including all clubs
     * @param seasonYear/ the season which host the championship
     * @return the list of match created
     */
    @Override
    public List<Match> matchMaker(Integer seasonYear) {
        // First, verify season exists and has STARTED status
        String checkSeasonSql = """
            SELECT status FROM "Season" WHERE year = ?
            """;
        
        // Check if matches already exist
        String checkMatchesSql = """
            SELECT COUNT(*) FROM "Match" m
            JOIN "Season" s ON s.id = m."seasonId"
            WHERE s.year = ?
            """;

        // Get all clubs
        String getClubsSql = """
            SELECT id, name, acronym, stadium FROM "Club"
            """;

        // Insert match
        String insertMatchSql = """
            INSERT INTO "Match" (id, "homeClubId", "awayClubId", stadium, "matchDatetime", "actualStatus", "seasonId")
            VALUES (?, ?, ?, ?, ?, 'NOT_STARTED'::\"MatchStatus\", (SELECT id FROM "Season" WHERE year = ?))
            RETURNING id, "homeClubId", "awayClubId", stadium, "matchDatetime", "actualStatus"
            """;

        List<Match> matches = new ArrayList<>();

        try (Connection con = dataSource.getConnection()) {
            // Check season status
            try (PreparedStatement ps = con.prepareStatement(checkSeasonSql)) {
                ps.setInt(1, seasonYear);
                ResultSet rs = ps.executeQuery();
                if (!rs.next()) {
                    throw new NotFoundException("Season " + seasonYear + " not found");
                }
                String status = rs.getString("status");
                if (!"STARTED".equals(status)) {
                    throw new BadRequestException("Season must be in STARTED status");
                }
            }

            // Check if matches already exist
            try (PreparedStatement ps = con.prepareStatement(checkMatchesSql)) {
                ps.setInt(1, seasonYear);
                ResultSet rs = ps.executeQuery();
                rs.next();
                if (rs.getInt(1) > 0) {
                    throw new RuntimeException("Matches already exist for season " + seasonYear);
                }
            }

            // Get all clubs
            List<Club> clubs = new ArrayList<>();
            try (PreparedStatement ps = con.prepareStatement(getClubsSql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Club club = new Club();
                    club.setId(rs.getString("id"));
                    club.setName(rs.getString("name"));
                    club.setAcronym(rs.getString("acronym"));
                    club.setStadium(rs.getString("stadium"));
                    clubs.add(club);
                }
            }

            // Create matches
            try (PreparedStatement ps = con.prepareStatement(insertMatchSql)) {
                LocalDate startDate = LocalDate.of(seasonYear, 8, 1); // Season starts August 1st
                int matchDay = 0;

                // Generate home and away matches
                for (int i = 0; i < clubs.size(); i++) {
                    for (int j = i + 1; j < clubs.size(); j++) {
                        Club homeClub = clubs.get(i);
                        Club awayClub = clubs.get(j);

                        // Create home match
                        String matchId = UUID.randomUUID().toString();
                        ps.setString(1, matchId);
                        ps.setString(2, homeClub.getId());
                        ps.setString(3, awayClub.getId());
                        ps.setString(4, homeClub.getStadium());
                        ps.setObject(5, startDate.plusDays(matchDay));
                        ps.setInt(6, seasonYear);

                        try (ResultSet rs = ps.executeQuery()) {
                            if (rs.next()) {
                                Match match = createMatchFromResultSet(rs, homeClub, awayClub);
                                matches.add(match);
                            }
                        }

                        // Create away match (return match)
                        matchId = UUID.randomUUID().toString();
                        ps.setString(1, matchId);
                        ps.setString(2, awayClub.getId());
                        ps.setString(3, homeClub.getId());
                        ps.setString(4, awayClub.getStadium());
                        ps.setObject(5, startDate.plusDays(matchDay + 1));
                        ps.setInt(6, seasonYear);

                        try (ResultSet rs = ps.executeQuery()) {
                            if (rs.next()) {
                                Match match = createMatchFromResultSet(rs, awayClub, homeClub);
                                matches.add(match);
                            }
                        }

                        matchDay += 2;
                    }
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return matches;
    }

    private Match createMatchFromResultSet(ResultSet rs, Club homeClub, Club awayClub) throws SQLException {
        Match match = new Match();
        
        MatchClub home = new MatchClub();
        home.setId(homeClub.getId());
        home.setName(homeClub.getName());
        home.setAcronym(homeClub.getAcronym());
        home.setScore(0);
        home.setScorers(new ArrayList<>());
        match.setClubPlayingHome(home);

        MatchClub away = new MatchClub();
        away.setId(awayClub.getId());
        away.setName(awayClub.getName());
        away.setAcronym(awayClub.getAcronym());
        away.setScore(0);
        away.setScorers(new ArrayList<>());
        match.setClubPlayingAway(away);

        match.setStadium(rs.getString("stadium"));
        match.setMatchDateTime(rs.getObject("matchDatetime", LocalDate.class));
        match.setActualStatus(MatchStatus.valueOf(rs.getString("actualStatus")));

        return match;
    }

    /**
     * @Description Create all matches for a specific season including all clubs
     * @param seasonYear/ the season which host the championship
     * @return the list of match created
     */
    @Override
    public List<Match> getMatches(Integer seasonYear) {
        return List.of();
    }

    /**
     * @Description Change a specific match status
     * @param id the id of the match
     * @param status the pretending status
     * @return
     */

    @Override
    public Match updateMatchStatus(String id, MatchStatus status) {
        return null;
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
        return null;
    }
}
