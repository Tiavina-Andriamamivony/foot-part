package org.prog3.foot.models;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Match {
    private MatchClub clubPlayingHome;
    private MatchClub clubPlayingAway;
    private String stadium;
    private LocalDate matchDateTime;
    private MatchStatus actualStatus;

    private boolean transitionIsOkay(MatchStatus pretendingStatus) {
        return pretendingStatus.ordinal() - this.actualStatus.ordinal() == 1 || pretendingStatus.ordinal() - this.actualStatus.ordinal() == -1;
    }

    public boolean transitionStatus(MatchStatus pretendingStatus) {
        if (transitionIsOkay(pretendingStatus)) {
            setActualStatus(pretendingStatus);
            return true;
        }
        return false;
    }
}
