package org.prog3.foot;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.prog3.foot.models.Season;
import org.prog3.foot.models.SeasonStatus;

public class SeasonMethodTest {
    private Season TestSeason=new Season();
    @Test
    public void IsTransitionOk(){
        TestSeason.setStatus(SeasonStatus.NOT_STARTED);
        Assertions.assertEquals("Transition NOT done, error in order of transition", TestSeason.transitionStatus(SeasonStatus.FINISHED));
        Assertions.assertEquals("Transition OK and done",TestSeason.transitionStatus(SeasonStatus.STARTED));
        Assertions.assertEquals("Transition NOT done, error in order of transition", TestSeason.transitionStatus(SeasonStatus.NOT_STARTED));

    }

}
