package org.prog3.foot.models;

import lombok.Data;

import java.io.Serializable;

@Data
public class AddGoal implements Serializable {
    private String clubId;
    private String scoreIdentifier;
    private Integer minuteOfGoal; //TODO: Create the methode which check if the value is between 1 and 90

}
