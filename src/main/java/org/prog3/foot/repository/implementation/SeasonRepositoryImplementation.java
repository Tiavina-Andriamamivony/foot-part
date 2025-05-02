package org.prog3.foot.repository.implementation;

import lombok.AllArgsConstructor;
import org.prog3.foot.configuration.DataSource;
import org.prog3.foot.models.CreateSeason;
import org.prog3.foot.models.Season;
import org.prog3.foot.models.UpdateSeasonStatus;
import org.prog3.foot.repository.SeasonRepository;
import org.springframework.stereotype.Repository;

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
        return List.of();
    }

    /**
     * @param seasons
     * @return
     */
    @Override
    public List<Season> AddSeasons(List<CreateSeason> seasons) {
        return List.of();
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
