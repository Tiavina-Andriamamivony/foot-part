package org.prog3.foot.repository.implementation;

import lombok.AllArgsConstructor;
import org.prog3.foot.configuration.DataSource;
import org.prog3.foot.models.*;
import org.prog3.foot.repository.ClubRepository;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
@AllArgsConstructor
public class ClubRepositoryImplementation implements ClubRepository {
    private final DataSource dataSource;


    /**
     * Gets all clubs in the championship
     * 
     * @return List of all clubs with their complete information (name, acronym, year of creation, stadium, and coach)
     */
    @Override
    public List<Club> getClubs() {
        String sql = """
            SELECT "id", "name", "acronym", "yearCreation", "stadium", "coachName", "coachNationality"
            FROM "Club"
            ORDER BY name ASC
            """;

        List<Club> result = new ArrayList<>();

        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Club club = new Club();
                club.setId(rs.getString("id"));
                club.setName(rs.getString("name"));
                club.setAcronym(rs.getString("acronym"));
                club.setYearCreation(rs.getInt("yearCreation"));
                club.setStadium(rs.getString("stadium"));
                
                // Set coach information
                Coach coach = new Coach();
                coach.setName(rs.getString("coachName"));
                coach.setNationality(rs.getString("coachNationality"));
                club.setCoach(coach);

                result.add(club);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    /**
     * Creates new clubs or updates existing ones
     * 
     * @param clubs List of clubs to create or update
     * @return List of created or updated clubs
     */
    @Override
    public List<Club> upCreateClub(List<Club> clubs) {
        String upsertSql = """
            INSERT INTO "Club" (id, name, acronym, "yearCreation", stadium, "coachName", "coachNationality")
            VALUES (?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (id) DO UPDATE SET
                name = EXCLUDED.name,
                acronym = EXCLUDED.acronym,
                "yearCreation" = EXCLUDED."yearCreation",
                stadium = EXCLUDED.stadium,
                "coachName" = EXCLUDED."coachName",
                "coachNationality" = EXCLUDED."coachNationality"
            RETURNING id, name, acronym, "yearCreation", stadium, "coachName", "coachNationality"
            """;

        List<Club> result = new ArrayList<>();

        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(upsertSql)) {

            for (Club club : clubs) {
                // Generate UUID for new clubs if id is not provided
                String id = club.getId() != null ? club.getId() : java.util.UUID.randomUUID().toString();
                
                ps.setString(1, id);
                ps.setString(2, club.getName());
                ps.setString(3, club.getAcronym());
                ps.setInt(4, club.getYearCreation());
                ps.setString(5, club.getStadium());
                ps.setString(6, club.getCoach().getName());
                ps.setString(7, club.getCoach().getNationality());

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Club updatedClub = new Club();
                        updatedClub.setId(rs.getString("id"));
                        updatedClub.setName(rs.getString("name"));
                        updatedClub.setAcronym(rs.getString("acronym"));
                        updatedClub.setYearCreation(rs.getInt("yearCreation"));
                        updatedClub.setStadium(rs.getString("stadium"));
                        
                        Coach coach = new Coach();
                        coach.setName(rs.getString("coachName"));
                        coach.setNationality(rs.getString("coachNationality"));
                        updatedClub.setCoach(coach);

                        result.add(updatedClub);
                    }
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    /**
     * Retrieves all current players from a specific club
     * 
     * @param id The club identifier
     * @return List of players currently in the club
     */
    @Override
    public List<Player> getPlayersFromASpecificClub(String id) {
        String sql = """
            SELECT p.id, p.name, p.number, p.position, p.nationality, p.age
            FROM "Player" p
            WHERE p."clubId" = ?
            ORDER BY p.number ASC
            """;

        List<Player> result = new ArrayList<>();

        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Player player = new Player();
                    player.setId(rs.getString("id"));
                    player.setName(rs.getString("name"));
                    player.setNumber(rs.getInt("number"));
                    player.setPlayerPosition(PlayerPosition.valueOf(rs.getString("position")));
                    player.setNationality(rs.getString("nationality"));
                    player.setAge(rs.getInt("age"));
                    
                    result.add(player);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    /**
     * Changes players of a specific club. This operation:
     * - Erases existing players from the club
     * - Maintains player statistics even after detachment
     * - Prevents detached players from participating in remaining matches of unfinished seasons
     * 
     * @param id The club identifier
     * @param playersToDrop List of players to set for the club (replacing existing ones)
     * @return Updated player information
     * @throws RuntimeException if any player is still attached to another club
     */
    @Override
    public List<Player> dropPlayer(String id, List<Player> playersToDrop) {
        // Verify that all players to drop belong to the specified club
        String verifyClubSql = """
            SELECT COUNT(*) as count
            FROM "Player"
            WHERE id = ? AND "clubId" = ?
            """;
            
        // Update query to detach players
        String updateSql = """
            UPDATE "Player"
            SET "clubId" = NULL
            WHERE id = ? AND "clubId" = ?
            RETURNING id, name, number, position, nationality, age
            """;

        List<Player> droppedPlayers = new ArrayList<>();

        try (Connection con = dataSource.getConnection()) {
            // First verify each player belongs to the club
            try (PreparedStatement verifyPs = con.prepareStatement(verifyClubSql)) {
                for (Player player : playersToDrop) {
                    verifyPs.setString(1, player.getId());
                    verifyPs.setString(2, id);
                    
                    try (ResultSet rs = verifyPs.executeQuery()) {
                        rs.next();
                        if (rs.getInt("count") == 0) {
                            throw new RuntimeException("Player " + player.getId() + " is not attached to club " + id);
                        }
                    }
                }
            }

            // Then proceed with the update
            try (PreparedStatement updatePs = con.prepareStatement(updateSql)) {
                for (Player player : playersToDrop) {
                    updatePs.setString(1, player.getId());
                    updatePs.setString(2, id);
                    
                    try (ResultSet rs = updatePs.executeQuery()) {
                        while (rs.next()) {
                            Player droppedPlayer = new Player();
                            droppedPlayer.setId(rs.getString("id"));
                            droppedPlayer.setName(rs.getString("name"));
                            droppedPlayer.setNumber(rs.getInt("number"));
                            droppedPlayer.setPlayerPosition(PlayerPosition.valueOf(rs.getString("position")));
                            droppedPlayer.setNationality(rs.getString("nationality"));
                            droppedPlayer.setAge(rs.getInt("age"));
                            
                            droppedPlayers.add(droppedPlayer);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return droppedPlayers;
    }

    /**
     * Adds new or existing players to a specific club. This operation:
     * - Creates new players if they don't exist
     * - Attaches existing players if they're not attached to any club
     * - Preserves existing player attributes if players already exist
     * 
     * @param id The club identifier
     * @param playersToAdd List of players to add to the club
     * @return List of all players in the club (including both old and new players)
     * @throws RuntimeException if any player is still attached to another club
     */
    @Override
    public List<Player> addPlayer(String id, List<Player> playersToAdd) {
        // Check if any player is already attached to another club
        String checkAttachmentSql = """
            SELECT COUNT(*) as count, "clubId"
            FROM "Player"
            WHERE id = ? AND "clubId" IS NOT NULL
            GROUP BY "clubId"
            """;

        // Upsert players
        String upsertSql = """
            INSERT INTO "Player" (id, name, number, position, nationality, age, "clubId")
            VALUES (?, ?, ?, ?::"PlayerPosition", ?, ?, ?)
            ON CONFLICT (id) DO UPDATE SET
                "clubId" = EXCLUDED."clubId"
            WHERE "Player"."clubId" IS NULL
            RETURNING id, name, number, position::"PlayerPosition", nationality, age
            """;

        try (Connection con = dataSource.getConnection()) {
            // First verify no player is attached to another club
            try (PreparedStatement checkPs = con.prepareStatement(checkAttachmentSql)) {
                for (Player player : playersToAdd) {
                    if (player.getId() != null) {
                        checkPs.setString(1, player.getId());
                        try (ResultSet rs = checkPs.executeQuery()) {
                            if (rs.next() && !id.equals(rs.getString("clubId"))) {
                                throw new RuntimeException("Player " + player.getId() + " is already attached to another club");
                            }
                        }
                    }
                }
            }

            // Then proceed with the upsert
            try (PreparedStatement upsertPs = con.prepareStatement(upsertSql)) {
                for (Player player : playersToAdd) {
                    String playerId = player.getId() != null ? player.getId() : java.util.UUID.randomUUID().toString();
                    
                    upsertPs.setString(1, playerId);
                    upsertPs.setString(2, player.getName());
                    upsertPs.setInt(3, player.getNumber());
                    upsertPs.setObject(4, player.getPlayerPosition().name());
                    upsertPs.setString(5, player.getNationality());
                    upsertPs.setInt(6, player.getAge());
                    upsertPs.setString(7, id);

                    upsertPs.addBatch();
                }
                upsertPs.executeBatch();
            }

            // Finally, return all players in the club
            return getPlayersFromASpecificClub(id);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets statistics for all clubs in a specific season. Statistics include:
     * - Ranking points
     * - Scored goals
     * - Conceded goals
     * - Goal difference
     * - Clean sheet number
     * Can be ordered by rankings based on:
     * 1. Ranking points (highest first)
     * 2. Goal difference (highest first)
     * 3. Clean sheets number (highest first)
     * 
     * @param seasonYear The year of the season
     * @return List of club statistics for the specified season
     */
    @Override
    public List<ClubStatistics> getClubStatistics(Integer seasonYear) {
        String sql = """
            WITH CleanSheets AS (
                SELECT m."homeClubId" as clubId, COUNT(*) as cleanSheets
                FROM "Match" m
                LEFT JOIN "Goal" g ON g."matchId" = m.id AND g."clubId" = m."awayClubId"
                WHERE m."seasonId" IN (SELECT id FROM "Season" WHERE year = ?)
                AND g.id IS NULL
                GROUP BY m."homeClubId"
                UNION ALL
                SELECT m."awayClubId" as clubId, COUNT(*) as cleanSheets
                FROM "Match" m
                LEFT JOIN "Goal" g ON g."matchId" = m.id AND g."clubId" = m."homeClubId"
                WHERE m."seasonId" IN (SELECT id FROM "Season" WHERE year = ?)
                AND g.id IS NULL
                GROUP BY m."awayClubId"
            ),
            GoalsScored AS (
                SELECT g."clubId", COUNT(*) as scored
                FROM "Goal" g
                JOIN "Match" m ON m.id = g."matchId"
                WHERE m."seasonId" IN (SELECT id FROM "Season" WHERE year = ?)
                AND g."isOwnGoal" = false
                GROUP BY g."clubId"
            ),
            GoalsConceded AS (
                SELECT g."clubId", COUNT(*) as conceded
                FROM "Goal" g
                JOIN "Match" m ON m.id = g."matchId"
                WHERE m."seasonId" IN (SELECT id FROM "Season" WHERE year = ?)
                GROUP BY g."clubId"
            )
            SELECT 
                c.id,
                c.name,
                c.acronym,
                c."yearCreation",
                c.stadium,
                c."coachName",
                c."coachNationality",
                COALESCE(gs.scored, 0) as scored_goals,
                COALESCE(gc.conceded, 0) as conceded_goals,
                COALESCE(gs.scored, 0) - COALESCE(gc.conceded, 0) as goal_difference,
                COALESCE(cs.cleanSheets, 0) as clean_sheets,
                (COALESCE(gs.scored, 0) * 3) as ranking_points
            FROM "Club" c
            LEFT JOIN GoalsScored gs ON gs."clubId" = c.id
            LEFT JOIN GoalsConceded gc ON gc."clubId" = c.id
            LEFT JOIN (
                SELECT clubId, SUM(cleanSheets) as cleanSheets
                FROM CleanSheets
                GROUP BY clubId
            ) cs ON cs.clubId = c.id
            ORDER BY ranking_points DESC, goal_difference DESC, clean_sheets DESC
            """;

        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            // Set the season year for all subqueries
            ps.setInt(1, seasonYear);
            ps.setInt(2, seasonYear);
            ps.setInt(3, seasonYear);
            ps.setInt(4, seasonYear);

            List<ClubStatistics> statistics = new ArrayList<>();
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ClubStatistics stat = new ClubStatistics();
                    
                    // Set Club information
                    stat.setId(rs.getString("id"));
                    stat.setName(rs.getString("name"));
                    stat.setAcronym(rs.getString("acronym"));
                    stat.setYearCreation(rs.getInt("yearCreation"));
                    stat.setStadium(rs.getString("stadium"));
                    
                    // Set Coach information
                    Coach coach = new Coach();
                    coach.setName(rs.getString("coachName"));
                    coach.setNationality(rs.getString("coachNationality"));
                    stat.setCoach(coach);
                    
                    // Set Statistics
                    stat.setScoreGoals(rs.getInt("scored_goals"));
                    stat.setConcededGoals(rs.getInt("conceded_goals"));
                    stat.setDifferenceGoals(rs.getInt("goal_difference"));
                    stat.setCleanSheetNumber(rs.getInt("clean_sheets"));
                    stat.setRankingPoints(rs.getInt("ranking_points"));
                    
                    statistics.add(stat);
                }
            }
            
            return statistics;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param id
     * @param newPlayers
     * @return
     */
    @Override
    public List<Player> ReplaceAll(String id, List<Player> newPlayers) {
        String deleteSql = """
            DELETE FROM "Player"
            WHERE "clubId" = ?
            """;

        String insertSql = """
            INSERT INTO "Player" (id, name, number, position, nationality, age, "clubId")
            VALUES (?, ?, ?, ?::\"PlayerPosition\", ?, ?, ?)
            ON CONFLICT (id) DO UPDATE SET
                name = EXCLUDED.name,
                number = EXCLUDED.number,
                position = EXCLUDED.position,
                nationality = EXCLUDED.nationality,
                age = EXCLUDED.age,
                "clubId" = EXCLUDED."clubId"
            """;

        try (Connection con = dataSource.getConnection()) {
            // Start transaction
            con.setAutoCommit(false);

            try {
                // First, delete all current players
                try (PreparedStatement deletePs = con.prepareStatement(deleteSql)) {
                    deletePs.setString(1, id);
                    deletePs.executeUpdate();
                }

                // Then, insert new players
                try (PreparedStatement insertPs = con.prepareStatement(insertSql)) {
                    for (Player player : newPlayers) {
                        String playerId = player.getId() != null ? player.getId() : java.util.UUID.randomUUID().toString();

                        // Insert player
                        insertPs.setString(1, playerId);
                        insertPs.setString(2, player.getName());
                        insertPs.setInt(3, player.getNumber());
                        insertPs.setString(4, player.getPlayerPosition().name());
                        insertPs.setString(5, player.getNationality());
                        insertPs.setInt(6, player.getAge());
                        insertPs.setString(7, id);

                        insertPs.addBatch();
                    }
                    insertPs.executeBatch();
                }

                // Commit transaction
                con.commit();

                // Return updated list of players
                return getPlayersFromASpecificClub(id);

            } catch (SQLException e) {
                // Rollback in case of error
                con.rollback();
                throw e;
            } finally {
                // Reset auto-commit
                con.setAutoCommit(true);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error replacing players: " + e.getMessage(), e);
        }
    }

    /**
     * @return
     */
    @Override
    public List<Tansfert> getTransfert() {
        String sql = """
            SELECT 
                p.id as player_id, 
                p.name as player_name,
                p.number as player_number,
                p.position as player_position,
                p.nationality as player_nationality,
                p.age as player_age,
                c.id as club_id,
                c.name as club_name,
                c.acronym as club_acronym,
                c."yearCreation" as club_year,
                c.stadium as club_stadium,
                c."coachName" as coach_name,
                c."coachNationality" as coach_nationality,
                t."transferDate" as transfer_date
            FROM "Transfer" t
            JOIN "Player" p ON p.id = t."playerId"
            LEFT JOIN "Club" c ON c.id = t."clubId"
            ORDER BY t."transferDate" DESC
            """;

        List<Tansfert> transfers = new ArrayList<>();

        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Tansfert transfer = new Tansfert();
                    
                    Player player = new Player();
                    player.setId(rs.getString("player_id"));
                    player.setName(rs.getString("player_name"));
                    player.setNumber(rs.getInt("player_number"));
                    player.setPlayerPosition(PlayerPosition.valueOf(rs.getString("player_position")));
                    player.setNationality(rs.getString("player_nationality"));
                    player.setAge(rs.getInt("player_age"));
                    transfer.setPlayer(player);

                    String clubId = rs.getString("club_id");
                    if (clubId != null) {
                        Club club = new Club();
                        club.setId(clubId);
                        club.setName(rs.getString("club_name"));
                        club.setAcronym(rs.getString("club_acronym"));
                        club.setYearCreation(rs.getInt("club_year"));
                        club.setStadium(rs.getString("club_stadium"));
                        
                        Coach coach = new Coach();
                        coach.setName(rs.getString("coach_name"));
                        coach.setNationality(rs.getString("coach_nationality"));
                        club.setCoach(coach);
                        
                        transfer.setClub(club);
                    }

                    transfer.setDate(rs.getTimestamp("transfer_date").toLocalDateTime().toLocalDate());
                    transfers.add(transfer);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting transfers: " + e.getMessage(), e);
        }

        return transfers;
    }
}
