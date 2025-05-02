package org.prog3.foot.service;

import lombok.AllArgsConstructor;
import org.prog3.foot.models.ClubPlayer;
import org.prog3.foot.models.Player;
import org.prog3.foot.models.PlayerStatsitic;
import org.prog3.foot.repository.PlayerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PlayerService {
    private final PlayerRepository repository;

    public List<ClubPlayer> getClubPlayers(String name, Integer ageMinimum, Integer ageMaximum, String clubName) {
        return repository.getClubPlayers().stream()
            .filter(player -> {
                // Filter by player name if provided
                if (name != null && !name.isEmpty()) {
                    if (!player.getName().toLowerCase().contains(name.toLowerCase())) {
                        return false;
                    }
                }
                
                // Filter by minimum age if provided
                if (ageMinimum != null && player.getAge() < ageMinimum) {
                    return false;
                }
                
                // Filter by maximum age if provided
                if (ageMaximum != null && player.getAge() > ageMaximum) {
                    return false;
                }
                
                // Filter by club name if provided
                if (clubName != null && !clubName.isEmpty()) {
                    if (player.getClub() == null || 
                        !player.getClub().getName().toLowerCase().contains(clubName.toLowerCase())) {
                        return false;
                    }
                }
                
                return true;
            })
            .collect(Collectors.toList());
    }
    public List<Player> upCreatePlayers(List<Player> players){
        return repository.upCreatePlayers(players);
    }
    public PlayerStatsitic getPlayerStatsitic(String id, Integer seasonYear){
        return repository.getPlayerStatsitic(id, seasonYear);
    };

}
