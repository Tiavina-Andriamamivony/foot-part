package org.prog3.foot.models;

import lombok.Data;

import java.util.List;

@Data
public class ClubScore {
    private Integer score;
    private List<Scorer> scorers;
}
