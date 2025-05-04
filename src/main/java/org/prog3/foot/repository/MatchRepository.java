package org.prog3.foot.repository;

import org.prog3.foot.models.AddGoal;
import org.prog3.foot.models.Match;
import org.prog3.foot.models.MatchStatus;

import java.util.List;

public interface MatchRepository {
    public List<Match> matchMaker(Integer seasonYear) ;
    public List<Match> getMatches(Integer seasonYear) ;
    public Match updateMatchStatus(String id, MatchStatus status) ;
    public Match addGoals(String matchId, List< AddGoal > goals) ;
}
