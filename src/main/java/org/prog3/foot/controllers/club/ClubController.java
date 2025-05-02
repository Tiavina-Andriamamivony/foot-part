package org.prog3.foot.controllers.club;

import org.prog3.foot.models.Club;
import org.prog3.foot.models.ClubStatistics;
import org.prog3.foot.models.Player;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ClubController {
    @GetMapping("/clubs")
    public ResponseEntity<List<Club>> getClubs() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @PutMapping("/clubs")
    public ResponseEntity<Club> upCreateClub(@RequestBody Club club) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @GetMapping("/clubs/{id}/players")
    public ResponseEntity<List<Player>> getPlayersFromASpecificClub(@PathVariable int id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * @Description Provided players inside the requestBody erase the existing players inside the club. In case player is detached from club, it is still possible to retrieve his individual statistics and collective statistics do not change. For example, the player has 10 goals for the season, even if he is not part of the club anymore, the club statistics do not change (goals scored). Finally, he must not be inside the list of players can make actions anymore for the remaining matches, if the season is not yet finished.
     * In case, one of existing players is still attached to a club, API must return 400 BAD_REQUEST.
     * @param id/the id of the player
     * @param player/
     * @return
     */
    @PutMapping("/clubs/{id}/players")
    public ResponseEntity<Player> updatePlayer(@PathVariable int id, @RequestBody Player player) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    @PostMapping("/clubs/{id}/players")
    public ResponseEntity<Player> addPlayer(@PathVariable int id, @RequestBody Player player) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    @GetMapping("/clubs/statistics/{seasonYear}")
    public ResponseEntity<List<ClubStatistics>> getClubStatistics(@PathVariable int seasonYear) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
