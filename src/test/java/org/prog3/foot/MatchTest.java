package org.prog3.foot;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.prog3.foot.configuration.DataSource;
import org.prog3.foot.models.AddGoal;
import org.prog3.foot.models.Match;
import org.prog3.foot.models.MatchStatus;
import org.prog3.foot.repository.implementation.ClubRepositoryImplementation;
import org.prog3.foot.repository.implementation.MatchRepositoryImplementation;
import org.prog3.foot.repository.implementation.SeasonRepositoryImplementation;

import java.util.ArrayList;
import java.util.List;

public class MatchTest {
    private DataSource dataSource = new DataSource();
    private ClubRepositoryImplementation clubRepo = new ClubRepositoryImplementation(dataSource);
    private SeasonRepositoryImplementation seasonRepo = new SeasonRepositoryImplementation(dataSource);
    private MatchRepositoryImplementation repo = new MatchRepositoryImplementation(dataSource, clubRepo, seasonRepo);

    @Test
    void matchMaker() {
        // Test creating matches for season 2023 (existing in data.sql)
        List<Match> matches = repo.matchMaker(2023);
        
        // Verify matches were created
        Assertions.assertFalse(matches.isEmpty());
        
        // Verify home and away fixtures were created
        Match firstMatch = matches.get(0);
        Assertions.assertNotNull(firstMatch.getClubPlayingHome());
        Assertions.assertNotNull(firstMatch.getClubPlayingAway());
    }

    @Test
    void getMatches() {
        // Test getting matches for season 2023
        List<Match> matches = repo.getMatches(2023);
        
        // Verify matches are retrieved
        Assertions.assertFalse(matches.isEmpty());
        
        // Verify match details
        Match match = matches.get(0);
        Assertions.assertNotNull(match.getId());
        Assertions.assertNotNull(match.getStadium());
        Assertions.assertNotNull(match.getMatchDateTime());
    }

    @Test
    void updateMatchStatus() {
        // Using existing match 'lyon-om' from data.sql
        Match updatedMatch = repo.updateMatchStatus("lyon-om", MatchStatus.STARTED);
        
        // Verify status was updated
        Assertions.assertEquals(MatchStatus.STARTED, updatedMatch.getActualStatus());
    }

    @Test
    void addGoals() {
        // Create a test goal
        AddGoal goal = new AddGoal();
        goal.setClubId("lyon");
        goal.setScorerIdentifier("lacazette");
        goal.setMinuteOfGoal(75);

        List<AddGoal> goals = new ArrayList<>();
        goals.add(goal);

        // Add goal to existing match 'lyon-om'
        Match matchWithGoal = repo.addGoals("lyon-om", goals);
        
        // Verify goal was added
        Assertions.assertNotNull(matchWithGoal);
        Assertions.assertTrue(matchWithGoal.getClubPlayingHome().getScore() > 0 || 
                            matchWithGoal.getClubPlayingAway().getScore() > 0);
    }
}
