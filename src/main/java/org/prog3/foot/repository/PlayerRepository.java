package org.prog3.foot.repository;

import org.prog3.foot.models.ClubPlayer;
import org.prog3.foot.models.Player;
import org.prog3.foot.models.PlayerStatsitic;

import java.util.List;

public interface PlayerRepository {
     List<ClubPlayer> getClubPlayers();
     List<Player> upCreatePlayers(List<Player> players);
     PlayerStatsitic getPlayerStatsitic();
}
