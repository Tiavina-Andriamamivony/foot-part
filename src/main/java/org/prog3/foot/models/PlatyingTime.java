package org.prog3.foot.models;

import lombok.Data;

import java.io.Serializable;

@Data
public class PlatyingTime implements Serializable {
    private Integer value;
    private DurationUnit durationUnit;
}
