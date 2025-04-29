package org.prog3.foot.models;

import lombok.Data;


@Data

/**
 * Utilisation: GET/players, retourne tout les joueurs et leurs club d'appartenance
 *
 */
public class Player {
    private Club club;
    private Position position;
    private String nationality;
    private Integer age;
    private String id;
    private String name;
    private Integer number;

}
