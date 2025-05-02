package org.prog3.foot.models;

import lombok.Data;

import java.io.Serializable;
@Data
public class PlayingTime implements Serializable  {
    private Integer value;
    private DurationUnit durationUnit;
}
