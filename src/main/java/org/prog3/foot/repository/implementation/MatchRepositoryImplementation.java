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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
