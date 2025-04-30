package org.prog3.foot.models;

import lombok.Data;

import java.util.UUID;

@Data

/**
 *Cette classe désigne les informations minimum d'un joueur
 */
public class PlayerMinimumInfo {
    private String id;
    private String name;
    private Integer number;

}
