package org.prog3.foot.repository;

import org.prog3.foot.models.CreateSeason;
import org.prog3.foot.models.Season;
import org.prog3.foot.models.UpdateSeasonStatus;

import java.util.List;

public interface SeasonRepository {
    public List<Season> GetSeasons();
    public List<Season> AddSeasons(List<CreateSeason> seasons);
    public Season changeSeasonStatus(UpdateSeasonStatus pretending, Integer seasonYear);
}
