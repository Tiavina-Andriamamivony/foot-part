package org.prog3.foot.service;

import lombok.AllArgsConstructor;
import org.prog3.foot.models.Season;
import org.prog3.foot.repository.SeasonRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class SeasonService {
    private  final SeasonRepository repository;

    public List<Season> GetSeasons(){
        return repository.GetSeasons();
    }


}
