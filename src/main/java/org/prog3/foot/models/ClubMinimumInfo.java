package org.prog3.foot.models;

import lombok.Data;

import java.io.Serializable;

@Data
public class ClubMinimumInfo implements Serializable {
    private String id;
    private String name;
    private String acronym;
}
