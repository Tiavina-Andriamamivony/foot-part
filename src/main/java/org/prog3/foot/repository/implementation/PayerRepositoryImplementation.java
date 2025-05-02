package org.prog3.foot.repository.implementation;

import lombok.AllArgsConstructor;
import org.prog3.foot.configuration.DataSource;
import org.prog3.foot.models.*;
import org.prog3.foot.repository.PlayerRepository;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
@Repository
@AllArgsConstructor
public class PayerRepositoryImplementation implements PlayerRepository {
    private final DataSource dataSource;

    /**@Use GET/players
     * @Status Done
     * @return result/ List of all players and their clubs
     */
    @Override
    public List<ClubPlayer> getClubPlayers() {
        String sql = """
        SELECT p.*, c.name as club_name, c.acronym as club_acronym, 
               c."yearCreation" as club_year_creation, c.stadium as club_stadium, 
               c."coachName" as club_coach_name, c."coachNationality" as club_coach_nationality
        FROM "Player" p
        LEFT JOIN "Club" c ON p."clubId" = c.id
        """;

        List<ClubPlayer> result = new ArrayList<>();

        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ClubPlayer clubPlayer = new ClubPlayer();

                // Joueur
                clubPlayer.setId(rs.getString("id"));
                clubPlayer.setName(rs.getString("name"));
                clubPlayer.setNumber(rs.getInt("number"));
                clubPlayer.setPlayerPosition(PlayerPosition.valueOf(rs.getString("position")));
                clubPlayer.setNationality(rs.getString("nationality"));
                clubPlayer.setAge(rs.getInt("age"));

                // Club (si existant)
                String clubId = rs.getString("clubId");
                if (clubId != null) {
                    Club club = new Club();
                    club.setId(clubId);
                    club.setName(rs.getString("club_name"));
                    club.setAcronym(rs.getString("club_acronym"));
                    club.setYearCreation(rs.getInt("club_year_creation")); // Correction ici
                    club.setStadium(rs.getString("club_stadium"));

                    // Entraîneur
                    Coach coach = new Coach();
                    coach.setName(rs.getString("club_coach_name")); // Correction ici
                    coach.setNationality(rs.getString("club_coach_nationality")); // Correction ici
                    club.setCoach(coach);

                    clubPlayer.setClub(club);
                }

                result.add(clubPlayer);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    /**
     * @Use PUT/players
     * @Status To test
     * @param players/ List of player to upCreate
     * @return result/The list of players upCreated
     */
    @Override
    public List<Player> upCreatePlayers(List<Player> players) {
        String upsertSql = """
            INSERT INTO "Player" (id, name, number, position, nationality, age)
            VALUES (?, ?, ?, ?::\"PlayerPosition\", ?, ?)
            ON CONFLICT (id) DO UPDATE SET
                name = EXCLUDED.name,
                number = EXCLUDED.number,
                position = EXCLUDED.position::\"PlayerPosition\",
                nationality = EXCLUDED.nationality,
                age = EXCLUDED.age
            RETURNING *
            """;

        List<Player> result = new ArrayList<>();

        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(upsertSql)) {

            for (Player player : players) {
                ps.setString(1, player.getId());
                ps.setString(2, player.getName());
                ps.setInt(3, player.getNumber());
                ps.setString(4, player.getPlayerPosition().name());
                ps.setString(5, player.getNationality());
                ps.setInt(6, player.getAge());

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Player updatedPlayer = new Player();
                        updatedPlayer.setId(rs.getString("id"));
                        updatedPlayer.setName(rs.getString("name"));
                        updatedPlayer.setNumber(rs.getInt("number"));
                        updatedPlayer.setPlayerPosition(PlayerPosition.valueOf(rs.getString("position")));
                        updatedPlayer.setNationality(rs.getString("nationality"));
                        updatedPlayer.setAge(rs.getInt("age"));
                        result.add(updatedPlayer);
                    }
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    /**
     * @return
     */
    @Override
    public PlayerStatsitic getPlayerStatsitic(String id, Integer seasonYear) {
        String sql = """
            SELECT 
                COUNT(CASE WHEN g.id IS NOT NULL AND g."isOwnGoal" = false THEN 1 END) as scored_goals
            FROM "Player" p
            LEFT JOIN "Goal" g ON g."playerId" = p.id
            LEFT JOIN "Match" m ON g."matchId" = m.id
            LEFT JOIN "Season" s ON m."seasonId" = s.id
            WHERE p.id = ? AND s.year = ?
            GROUP BY p.id
            """;

        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, id);
            ps.setInt(2, seasonYear);

            PlayerStatsitic statistics = new PlayerStatsitic();
            // Default values
            statistics.setScoreGoals(0);

            PlayingTime playingTime = new PlayingTime();
            playingTime.setValue(0);
            playingTime.setDurationUnit(DurationUnit.SECOND);
            statistics.setPlayingTime(playingTime);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    statistics.setScoreGoals(rs.getInt("scored_goals"));
                }
            }

            return statistics;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
