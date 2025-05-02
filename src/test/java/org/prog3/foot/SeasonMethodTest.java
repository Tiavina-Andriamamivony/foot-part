package org.prog3.foot;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.prog3.foot.configuration.DataSource;
import org.prog3.foot.models.Season;
import org.prog3.foot.models.SeasonStatus;
import org.prog3.foot.repository.implementation.SeasonRepositoryImplementation;

public class SeasonMethodTest {
    private DataSource dataSource=new DataSource();
    private Season TestSeason=new Season();
    private SeasonRepositoryImplementation repo = new SeasonRepositoryImplementation(dataSource);
    @Test
    public void IsTransitionOk(){
        TestSeason.setStatus(SeasonStatus.NOT_STARTED);
        Assertions.assertEquals("Transition NOT done, error in order of transition", TestSeason.transitionStatus(SeasonStatus.FINISHED));
        Assertions.assertEquals("Transition OK and done",TestSeason.transitionStatus(SeasonStatus.STARTED));
        Assertions.assertEquals("Transition NOT done, error in order of transition", TestSeason.transitionStatus(SeasonStatus.NOT_STARTED));
    }

    @Test
    public void IsGetStatusOk(){
        System.out.println(repo.GetSeasons());
    }

}
