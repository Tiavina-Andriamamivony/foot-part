package org.prog3.foot.models;

import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = true)
@Data

/**
 * Utilisation: GET/players, retourne tout les joueurs et leurs club d'appartenance
 *
 */
public class Player extends PlayerMinimumInfo {
    private PlayerPosition playerPosition;
    private String nationality;
    private Integer age;
}
