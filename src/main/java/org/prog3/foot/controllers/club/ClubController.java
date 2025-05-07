package org.prog3.foot.controllers.club;

import lombok.AllArgsConstructor;
import org.prog3.foot.exception.NotFoundException;
import org.prog3.foot.models.Club;
import org.prog3.foot.models.ClubStatistics;
import org.prog3.foot.models.Player;
import org.prog3.foot.service.ClubService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor

public class ClubController {
    private final ClubService service;

    //Get clubs of the championship
    @GetMapping("/clubs")
    public ResponseEntity<List<Club>> getClubs() {
        return ResponseEntity.ok(service.getClubs());
    }

    //Create new clubs or update if already exist
    @PutMapping("/clubs")
    public ResponseEntity<List<Club>> upCreateClub(@RequestBody List<Club> clubs) {
        return ResponseEntity.ok(service.upCreateClub(clubs));
    }

    @GetMapping("/clubs/{id}/players")
    public ResponseEntity<List<Player>> getPlayersFromASpecificClub(@PathVariable String id) {
    if(service.getPlayersFromASpecificClub(id).isEmpty()) {
        return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(service.getPlayersFromASpecificClub(id));
    }

    /**
     * @Description Provided players inside the requestBody erase the existing players inside the club. In case player is detached from club, it is still possible to retrieve his individual statistics and collective statistics do not change. For example, the player has 10 goals for the season, even if he is not part of the club anymore, the club statistics do not change (goals scored). Finally, he must not be inside the list of players can make actions anymore for the remaining matches, if the season is not yet finished.
     * In case, one of existing players is still attached to a club, API must return 400 BAD_REQUEST.
     * @param id/the id of the player
     * @param player/
     * @return
     */
    @PutMapping("/clubs/{id}/players")
    public ResponseEntity<List<Player>> dropPlayer(@PathVariable String id, @RequestBody List<Player> player) {
        try {
            return ResponseEntity.ok(service.dropPlayer(id,player));
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    @PostMapping("/clubs/{id}/players")
    public ResponseEntity<List<Player>> addPlayer(@PathVariable String id, @RequestBody List<Player> player) {
        try {
            return ResponseEntity.ok(service.addPlayer(id,player));
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    @GetMapping("/clubs/statistics/{seasonYear}")
    public ResponseEntity<List<ClubStatistics>> getClubStatistics(@PathVariable int seasonYear) {
        try{
            return ResponseEntity.ok(service.getClubStatistics(seasonYear));
        }catch(NotFoundException e){
            return ResponseEntity.notFound().build();
        }
    }
}
