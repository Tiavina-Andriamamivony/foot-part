package org.prog3.foot.service;

import lombok.AllArgsConstructor;
import org.prog3.foot.models.Club;
import org.prog3.foot.models.ClubStatistics;
import org.prog3.foot.models.Player;
import org.prog3.foot.repository.ClubRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ClubService {
 private final ClubRepository repository;

    public List<Club> getClubs() {
    return repository.getClubs();
    }

    public List<Club> upCreateClub(List<Club> clubs) {
    return repository.upCreateClub(clubs);
    }
    public List<Player> getPlayersFromASpecificClub(String id) {
    return repository.getPlayersFromASpecificClub(id);
    }
    public List<Player> dropPlayer(String id, List<Player> playersToDrop) {
    return repository.dropPlayer(id, playersToDrop);
    }
    public List<Player> addPlayer(String id, List<Player> playersToAdd) {
    return repository.addPlayer(id, playersToAdd);
    }
    public List<ClubStatistics> getClubStatistics(Integer seasonYear) {
    return repository.getClubStatistics(seasonYear);
    }
}
