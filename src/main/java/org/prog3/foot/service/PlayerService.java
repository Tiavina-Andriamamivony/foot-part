package org.prog3.foot.service;

import lombok.AllArgsConstructor;
import org.prog3.foot.models.ClubPlayer;
import org.prog3.foot.repository.PlayerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class PlayerService {
    private final PlayerRepository repository;

    public List<ClubPlayer> getClubPlayers(){
        return repository.getClubPlayers();
    }

}
