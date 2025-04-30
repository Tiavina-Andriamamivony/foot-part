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


}
