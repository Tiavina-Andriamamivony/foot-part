package org.prog3.foot.repository.implementation;

import lombok.AllArgsConstructor;
import org.prog3.foot.configuration.DataSource;
import org.prog3.foot.models.Club;
import org.prog3.foot.models.ClubStatistics;
import org.prog3.foot.models.Coach;
import org.prog3.foot.models.Player;
import org.prog3.foot.models.PlayerPosition;
import org.prog3.foot.repository.ClubRepository;
import org.springframework.http.ResponseEntity;
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
        return List.of();
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
    public ResponseEntity<List<ClubStatistics>> getClubStatistics(Integer seasonYear) {
        return null;
    }
}
