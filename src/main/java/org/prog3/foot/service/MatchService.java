package org.prog3.foot.service;

import lombok.AllArgsConstructor;
import org.prog3.foot.models.AddGoal;
import org.prog3.foot.models.Match;
import org.prog3.foot.models.MatchStatus;
import org.prog3.foot.repository.MatchRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class MatchService {
    private final MatchRepository repository;
    public List<Match> matchMaker(Integer seasonYear) {
       return repository.matchMaker(seasonYear);
    }
    public List<Match> getMatches(Integer seasonYear) {
       return repository.getMatches(seasonYear);
    }

    public Match updateMatchStatus(String id, MatchStatus status) {
       return repository.updateMatchStatus(id, status);
    }
    public Match addGoals(String matchId, List<AddGoal> goals) {
       return repository.addGoals(matchId, goals);
    }
}
