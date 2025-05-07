package org.prog3.foot.controllers.match;

import lombok.AllArgsConstructor;
import org.prog3.foot.exception.NotFoundException;
import org.prog3.foot.models.AddGoal;
import org.prog3.foot.models.Match;
import org.prog3.foot.models.MatchStatus;
import org.prog3.foot.models.UpdateMatchStatus;
import org.prog3.foot.service.MatchService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
public class MatchController {
    private final MatchService service;

    /**
     * @Description Create all matches for a specific season including all clubs
     * @param seasonYear/ the season which host the championship
     * @return the list of match created
     */
    @PostMapping("/matchMaker/{seasonYear}")
    public ResponseEntity<List<Match>> matchMaker(@PathVariable Integer seasonYear) {
    try{
        return ResponseEntity.ok(service.matchMaker(seasonYear));
    }catch (NotFoundException e){
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    }

    /**
     * @Description Get all matches for a specific season
     * @param seasonYear The season which host all matches
     * @return the list of all matches this season
     */
    @GetMapping("/matches/{seasonYear}")
    public ResponseEntity<List<Match>> getMatches(@PathVariable Integer seasonYear) {
        try {
            List<Match> matches = service.getMatches(seasonYear);
            if (matches.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(matches);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * @Description Change a specific match status
     * @param id the id of the match
     * @param status the pretending status
     * @return
     */
    @PutMapping("/matches/{id}/status")
    public ResponseEntity<Match> updateMatchStatus(@PathVariable String id, @RequestBody UpdateMatchStatus status) {
        try{
            return ResponseEntity.ok(service.updateMatchStatus(id, status.getStatus()));
        }catch (NotFoundException e){
            return ResponseEntity.notFound().build();
        }
    }

    /**
     *
     * @param id the indentifier of the match to add goals
     * @param goal the list of goal: a goal is looking like that:{
     *     "clubId": "string",
     *     "scorerIdentifier": "string",
     *     "minuteOfGoal": 0
     *   }
     * @JsonReturnType:
     * {
     *   "id": "string",
     *   "clubPlayingHome": {
     *     "id": "string",
     *     "name": "string",
     *     "acronym": "RMA",
     *     "score": 0,
     *     "scorers": [
     *       {
     *         "player": {
     *           "id": "string",
     *           "name": "string",
     *           "number": 0
     *         },
     *         "minuteOfGoal": 0,
     *         "ownGoal": true
     *       }
     *     ]
     *   },
     *   "clubPlayingAway": {
     *     "id": "string",
     *     "name": "string",
     *     "acronym": "RMA",
     *     "score": 0,
     *     "scorers": [
     *       {
     *         "player": {
     *           "id": "string",
     *           "name": "string",
     *           "number": 0
     *         },
     *         "minuteOfGoal": 0,
     *         "ownGoal": true
     *       }
     *     ]
     *   },
     *   "stadium": "string",
     *   "matchDatetime": "2025-05-04T10:04:29.236Z",
     *   "actualStatus": "NOT_STARTED"
     * }
     * @return the match
     */
    @PostMapping("/matches/{id}/goals")
    public ResponseEntity<Match> addGoals(@PathVariable String id, @RequestBody List<AddGoal> goal) {
        try{
            return ResponseEntity.ok(service.addGoals(id, goal));
        }catch(NotFoundException e){
            return ResponseEntity.notFound().build();
        }
    }

}
