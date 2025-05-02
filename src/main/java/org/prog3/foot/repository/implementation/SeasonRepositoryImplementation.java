package org.prog3.foot.repository.implementation;

import lombok.AllArgsConstructor;
import org.prog3.foot.configuration.DataSource;
import org.prog3.foot.models.CreateSeason;
import org.prog3.foot.models.Season;
import org.prog3.foot.models.SeasonStatus;
import org.prog3.foot.models.UpdateSeasonStatus;
import org.prog3.foot.repository.SeasonRepository;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
@Repository
@AllArgsConstructor
public class SeasonRepositoryImplementation implements SeasonRepository {
    private final DataSource dataSource;

    /**
     * @return
     */
    @Override
    public List<Season> GetSeasons() {
        String sql = """
            SELECT id, year, alias, status
            FROM "Season"
            ORDER BY year ASC
            """;

        List<Season> result = new ArrayList<>();

        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Season season = new Season();
                season.setId(rs.getString("id"));
                season.setYear(rs.getInt("year"));
                season.setAlias(rs.getString("alias"));
                //TODO: Créer un mapper pour ça
                season.setStatus(SeasonStatus.valueOf(rs.getString("status")));
                result.add(season);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    /**
     * @param seasons
     * @return
     */
    @Override
    public List<Season> AddSeasons(List<CreateSeason> seasons) {
        String insertSql = """
            INSERT INTO "Season" (id, year, alias, status)
            VALUES (?, ?, ?, 'NOT_STARTED')
            RETURNING id, year, alias, status
            """;

        List<Season> result = new ArrayList<>();

        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(insertSql)) {

            for (CreateSeason createSeason : seasons) {
                // Generate UUID for new season
                String id = java.util.UUID.randomUUID().toString();
                
                ps.setString(1, id);
                ps.setInt(2, createSeason.getYear());
                ps.setString(3, createSeason.getAlias());

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Season season = new Season();
                        season.setId(rs.getString("id"));
                        season.setYear(rs.getInt("year"));
                        season.setAlias(rs.getString("alias"));
                        season.setStatus(SeasonStatus.valueOf(rs.getString("status")));
                        result.add(season);
                    }
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    /**
     * @param pretending
     * @param seasonYear
     * @return
     */
     /**
          * Changes the status of a season for a specific year following the transition rules:
          * NOT_STARTED > STARTED > FINISHED
          * 
          * @param pretending The new status to set for the season
          * @param seasonYear The year of the season to update
          * @return The updated season with its new status
          * @throws RuntimeException if the season is not found or if the status transition is invalid
          */
     @Override
     public Season changeSeasonStatus(UpdateSeasonStatus pretending, Integer seasonYear) {
         String selectSql = """
             SELECT id, year, alias, status
             FROM "Season"
             WHERE year = ?
             """;
    
         String updateSql = """
             UPDATE "Season"
             SET status = ?::\"SeasonStatus\"
             WHERE year = ?
             RETURNING id, year, alias, status
             """;
    
         try (Connection con = dataSource.getConnection()) {
             // First get the current season
             Season season = new Season();
             try (PreparedStatement ps = con.prepareStatement(selectSql)) {
                 ps.setInt(1, seasonYear);
                 try (ResultSet rs = ps.executeQuery()) {
                     if (rs.next()) {
                         season.setId(rs.getString("id"));
                         season.setYear(rs.getInt("year"));
                         season.setAlias(rs.getString("alias"));
                         season.setStatus(SeasonStatus.valueOf(rs.getString("status")));
                     } else {
                         throw new RuntimeException("Season not found for year: " + seasonYear);
                     }
                 }
             }
    
             // Check if transition is valid and update status
             String transitionResult = season.transitionStatus(pretending.getStatus());
             if (transitionResult.startsWith("Transition NOT")) {
                 throw new RuntimeException("Invalid status transition");
             }
    
             // Update the season status
             try (PreparedStatement ps = con.prepareStatement(updateSql)) {
                 ps.setString(1, season.getStatus().name());
                 ps.setInt(2, seasonYear);
                 try (ResultSet rs = ps.executeQuery()) {
                     if (rs.next()) {
                         season.setId(rs.getString("id"));
                         season.setYear(rs.getInt("year"));
                         season.setAlias(rs.getString("alias"));
                         season.setStatus(SeasonStatus.valueOf(rs.getString("status")));
                     }
                 }
             }
    
             return season;
    
         } catch (SQLException e) {
             throw new RuntimeException(e);
         }
     }
}
