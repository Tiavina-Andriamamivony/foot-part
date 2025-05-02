package org.prog3.foot.controllers.player;

import lombok.AllArgsConstructor;
import org.prog3.foot.exception.NotFoundException;
import org.prog3.foot.models.ClubPlayer;
import org.prog3.foot.models.Player;
import org.prog3.foot.models.PlayerStatsitic;
import org.prog3.foot.service.PlayerService;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<List<ClubPlayer>> getClubPlayers(@RequestParam(required = false, name ="name" )String name,
                                                           @RequestParam(required = false,name = "ageMinimum")Integer ageMinimum,
                                                           @RequestParam(required = false,name = "ageMaximum")Integer ageMaximum,
                                                           @RequestParam(required = false,name = "clubName")String clubName) {
        if(service.getClubPlayers(name,ageMinimum,ageMaximum,clubName).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(service.getClubPlayers(name,ageMinimum,ageMaximum,clubName));

    }

    /**
     * @Description Create or update players without attaching them into club
     * @return A list of player Update or created if they don't exist
     */
    @PutMapping("/players")
    public ResponseEntity<List<Player>> upCreatePlayers(@RequestBody List<Player> players) {
        return ResponseEntity.ok(service.upCreatePlayers(players));
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
