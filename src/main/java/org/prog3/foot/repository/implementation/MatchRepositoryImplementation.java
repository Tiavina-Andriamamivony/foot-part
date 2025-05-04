package org.prog3.foot.repository.implementation;

import lombok.AllArgsConstructor;
import org.prog3.foot.configuration.DataSource;
import org.prog3.foot.models.AddGoal;
import org.prog3.foot.models.Match;
import org.prog3.foot.models.MatchStatus;
import org.prog3.foot.repository.MatchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@AllArgsConstructor
public class MatchRepositoryImplementation implements MatchRepository {
    private final DataSource dataSource;


    /**
     * @Description Create all matches for a specific season including all clubs
     * @param seasonYear/ the season which host the championship
     * @return the list of match created
     */
    @Override
    public List<Match> matchMaker(Integer seasonYear) {
        return List.of();
    }

    /**
     * @Description Create all matches for a specific season including all clubs
     * @param seasonYear/ the season which host the championship
     * @return the list of match created
     */
    @Override
    public List<Match> getMatches(Integer seasonYear) {
        return List.of();
    }

    /**
     * @Description Change a specific match status
     * @param id the id of the match
     * @param status the pretending status
     * @return
     */

    @Override
    public Match updateMatchStatus(String id, MatchStatus status) {
        return null;
    }

    /**
     *
     * @param matchId the indentifier of the match to add goals
     * @param goals the list of goal: a goal is looking like that:{
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
    @Override
    public Match addGoals(String matchId, List<AddGoal> goals) {
        return null;
    }
}
