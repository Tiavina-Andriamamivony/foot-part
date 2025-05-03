package org.prog3.foot;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.prog3.foot.configuration.DataSource;
import org.prog3.foot.models.Season;
import org.prog3.foot.models.SeasonStatus;
import org.prog3.foot.models.UpdateSeasonStatus;
import org.prog3.foot.repository.implementation.SeasonRepositoryImplementation;

import static org.prog3.foot.models.SeasonStatus.NOT_STARTED;
import static org.prog3.foot.models.SeasonStatus.STARTED;

public class SeasonMethodTest {
    private DataSource dataSource=new DataSource();
    private Season TestSeason=new Season();
    private SeasonRepositoryImplementation repo = new SeasonRepositoryImplementation(dataSource);
    @Test
    public void IsTransitionOk(){
        TestSeason.setStatus(NOT_STARTED);
        Assertions.assertFalse(TestSeason.transitionStatus(SeasonStatus.FINISHED));
        Assertions.assertTrue(TestSeason.transitionStatus(STARTED));
        Assertions.assertTrue(TestSeason.transitionStatus(NOT_STARTED));
    }
    @Test
    public void IsTransitionInsideRepoIsOkay(){
        UpdateSeasonStatus sasa = new UpdateSeasonStatus();
        sasa.setStatus(STARTED);


    Assertions.assertNotNull(repo.changeSeasonStatus(sasa,2023));
        System.out.println(repo.GetSeasons());

    }



    @Test
    public void IsGetStatusOk(){
        System.out.println(repo.GetSeasons());
    }

}
