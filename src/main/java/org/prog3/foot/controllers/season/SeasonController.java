package org.prog3.foot.controllers.season;

import lombok.AllArgsConstructor;
import org.prog3.foot.models.CreateSeason;
import org.prog3.foot.models.Season;
import org.prog3.foot.models.UpdateSeasonStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
public class SeasonController {
    /**
     * @JsonReturnType:
     * {
     *   "year": 0,
     *   "alias": "string",
     *   "id": "string",
     *   "status": "NOT_STARTED"
     * }
     * @Description Return all seasons of the ligue
     */
    @GetMapping("/seasons")
    public ResponseEntity<List<Season>> GetSeasons() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     *
     * @param seasons/ List of season to post
     * @JsonReturnType: [
     *   {
     *     "year": 0,
     *     "alias": "string",
     *     "id": "string",
     *     "status": "NOT_STARTED"
     *   }
     * ]
     * @Description Post a list of Create season
     */
    @PostMapping("/seasons")
    public ResponseEntity<List<Season>> AddSeason(@RequestBody List<CreateSeason> seasons) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     *
     * @param seasonYear
     * @param seasonStatus
     * @JsonReturnType:
     * {
     *   "id": "string",
     *   "status": "NOT_STARTED",
     *   "year": 0,
     *   "alias": "string"
     * }
     * @Description Set a season to a spécific year
     */
    @PutMapping("/seasons/{seasonYear}/status")
    public ResponseEntity<Season> changeSeasonStatus(@RequestBody UpdateSeasonStatus seasonStatus, @PathVariable Integer seasonYear) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
