package org.prog3.foot.models;

import lombok.Data;

@Data
//Utilisation de name et nationality en tant que clés primaire même si j'aurais utiliser un id
public class Coach {
    private String name;
    private String nationality;
}
