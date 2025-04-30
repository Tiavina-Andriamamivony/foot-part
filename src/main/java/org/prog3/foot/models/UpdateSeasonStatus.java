package org.prog3.foot.models;

import lombok.Data;

import java.io.Serializable;

@Data
public class UpdateSeasonStatus implements Serializable {
    private SeasonStatus  status;
}
