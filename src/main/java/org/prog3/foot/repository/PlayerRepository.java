package org.prog3.foot.repository;

import org.prog3.foot.models.ClubPlayer;
import org.prog3.foot.models.Player;
import org.prog3.foot.models.PlayerMinimumInfo;
import org.prog3.foot.models.PlayerStatsitic;

import java.util.List;
import java.util.Optional;

public interface PlayerRepository {
     List<ClubPlayer> getClubPlayers();
     List<Player> upCreatePlayers(List<Player> players);
     PlayerStatsitic getPlayerStatsitic();
}
