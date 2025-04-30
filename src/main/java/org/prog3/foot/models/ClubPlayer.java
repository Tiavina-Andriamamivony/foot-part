package org.prog3.foot.models;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ClubPlayer extends Player {
    private Club club;
}
