package org.prog3.foot.models;

import lombok.Data;

@Data
public class MatchScore {
    private ClubScore home;
    private ClubScore away;
}
