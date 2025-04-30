package org.prog3.foot.models;

import lombok.Data;

import java.io.Serializable;

@Data
public class ClubStatistics extends Club implements Serializable {
    private Integer rankingPoints;
    private Integer scoreGoals;
    private Integer concededGoals;
    private Integer differenceGoals;
    private Integer cleanSheetNumber;

}
