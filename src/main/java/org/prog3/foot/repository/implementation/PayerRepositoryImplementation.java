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

    /**
     * @return
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
     * @param players
     * @return
     */
    @Override
    public List<Player> upCreatePlayers(List<Player> players) {
        return List.of();
    }

    /**
     * @return
     */
    @Override
    public PlayerStatsitic getPlayerStatsitic() {
        return null;
    }
}
