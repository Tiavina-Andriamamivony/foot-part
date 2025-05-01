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
                   c.year_creation, c.stadium, c.coach_name, c.coach_nationality
            FROM player p
            LEFT JOIN club c ON p.club_id = c.id
            """;

        List<ClubPlayer> result = new ArrayList<>();

        try(Connection con = dataSource.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
            
            while(rs.next()) {
                ClubPlayer clubPlayer = new ClubPlayer();
                
                // Set Player properties
                clubPlayer.setId(rs.getString("id"));
                clubPlayer.setName(rs.getString("name"));
                clubPlayer.setNumber(rs.getInt("number"));
                clubPlayer.setPlayerPosition(PlayerPosition.valueOf(rs.getString("position")));
                clubPlayer.setNationality(rs.getString("nationality"));
                clubPlayer.setAge(rs.getInt("age"));

                // Set Club properties if player has a club
                String clubId = rs.getString("club_id");
                if (clubId != null) {
                    Club club = new Club();
                    club.setId(clubId);
                    club.setName(rs.getString("club_name"));
                    club.setAcronym(rs.getString("club_acronym"));
                    club.setYearCreation(rs.getInt("year_creation"));
                    club.setStadium(rs.getString("stadium"));
                    
                    // Set Coach information
                    Coach coach = new Coach();
                    coach.setName(rs.getString("coach_name"));
                    coach.setNationality(rs.getString("coach_nationality"));
                    club.setCoach(coach);

                    clubPlayer.setClub(club);
                }

                result.add(clubPlayer);
            }

        } catch(SQLException e) {
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
