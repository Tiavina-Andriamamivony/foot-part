package org.prog3.foot.controllers.player;

import lombok.AllArgsConstructor;
import org.prog3.foot.models.ClubPlayer;
import org.prog3.foot.models.Player;
import org.prog3.foot.models.PlayerStatsitic;
import org.prog3.foot.service.PlayerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
public class PlayerController {
    private final PlayerService service;
    //TODO: Créer la table player et ses mapper
    /**
     * @Description Get list of players in the championship
     * @return List of ClubPlayer
     */
    @GetMapping("/players")
    public ResponseEntity<List<ClubPlayer>> getClubPlayers() {
        return ResponseEntity.ok(service.getClubPlayers());
    }

    /**
     * @Description Create or update players without attaching them into club
     * @return A list of player Update or created if they don't exist
     */
    @PutMapping("/players")
    public ResponseEntity<List<Player>> upCreatePlayers(@RequestBody List<Player> players) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * @Description :Get statistics for a specific player
     * @param id / the identifier of the player
     * @param seasonYear / the specific season  he played
     * @return / this spec player's stat
     */
    @GetMapping("/players/{id}/statistics/{seasonYear}")
    public ResponseEntity<PlayerStatsitic> GetPlayerStatistic(@PathVariable String id, @PathVariable Integer seasonYear) {
        throw new UnsupportedOperationException("Not supported yet.");
    }


}
