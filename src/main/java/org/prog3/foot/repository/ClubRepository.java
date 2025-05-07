package org.prog3.foot.repository;

import org.prog3.foot.models.Club;
import org.prog3.foot.models.ClubStatistics;
import org.prog3.foot.models.Player;
import org.prog3.foot.models.Tansfert;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ClubRepository {
    public List<Club> getClubs();
    public List<Club> upCreateClub(List<Club> clubs);
    public List<Player>  getPlayersFromASpecificClub(String id);
    public List<Player> dropPlayer(String id, List<Player> playersToDrop);
    public List<Player> addPlayer( String id, List<Player> playersToAdd);
    public List<ClubStatistics> getClubStatistics(Integer seasonYear);
    public List<Player> ReplaceAll(String id, List<Player> playersToDrop);

    public List<Tansfert> getTransfert();
}

