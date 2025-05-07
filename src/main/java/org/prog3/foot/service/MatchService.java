package org.prog3.foot.service;

import lombok.AllArgsConstructor;
import org.prog3.foot.exception.BadRequestException;
import org.prog3.foot.exception.NotFoundException;
import org.prog3.foot.models.*;
import org.prog3.foot.repository.ClubRepository;
import org.prog3.foot.repository.MatchRepository;
import org.prog3.foot.repository.SeasonRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class MatchService {
    private final MatchRepository repository;
    public List<Match> matchMaker(Integer seasonYear) {
       return repository.matchMaker(seasonYear);
    }
    public List<Match> getMatches(Integer seasonYear, MatchStatus matchStatus, 
                                 String clubPlayingName, LocalDate matchAfter, 
                                 LocalDate matchBeforeOrEquals) {
        List<Match> matches = repository.getMatches(seasonYear);
        
        // Apply filters if provided
        if (matchStatus != null) {
            matches = matches.stream()
                .filter(match -> match.getActualStatus() == matchStatus)
                .toList();
        }
        
        if (clubPlayingName != null && !clubPlayingName.isEmpty()) {
            matches = matches.stream()
                .filter(match -> 
                    match.getClubPlayingHome().getName().toLowerCase().contains(clubPlayingName.toLowerCase()) ||
                    match.getClubPlayingAway().getName().toLowerCase().contains(clubPlayingName.toLowerCase()))
                .toList();
        }
        
        if (matchAfter != null) {
            matches = matches.stream()
                .filter(match -> match.getMatchDateTime().toLocalDate().isAfter(matchAfter))
                .toList();
        }
        
        if (matchBeforeOrEquals != null) {
            matches = matches.stream()
                .filter(match -> match.getMatchDateTime().toLocalDate().isBefore(matchBeforeOrEquals) ||
                               match.getMatchDateTime().toLocalDate().isEqual(matchBeforeOrEquals))
                .toList();
        }
        
        return matches;
    }

    public Match updateMatchStatus(String id, MatchStatus status) {
       return repository.updateMatchStatus(id, status);
    }
    public Match addGoals(String matchId, List<AddGoal> goals) {
       return repository.addGoals(matchId, goals);
    }



}
