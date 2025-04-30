package org.prog3.foot.models;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;


@EqualsAndHashCode(callSuper = true)
@Data

/**
 * Utilisation: GET/players, retourne tout les joueurs et leurs club d'appartenance
 *
 */
public class Player extends PlayerMinimumInfo implements Serializable {
    private PlayerPosition playerPosition;
    private String nationality;
    private Integer age;
}
