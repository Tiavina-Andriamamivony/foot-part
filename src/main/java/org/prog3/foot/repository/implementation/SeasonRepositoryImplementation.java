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
    @Override
    public Season changeSeasonStatus(UpdateSeasonStatus pretending, Integer seasonYear) {
        return null;
    }
}
