package org.prog3.foot.models;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class ClubPlayer extends Player implements Serializable {
    private Club club;
}
