package org.prog3.foot.models;

import lombok.Data;

import java.io.Serializable;

@Data
public class UpdateMatchStatus implements Serializable {
    private MatchStatus status;
}
