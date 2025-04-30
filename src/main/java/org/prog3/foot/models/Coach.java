package org.prog3.foot.models;

import lombok.Data;

import java.io.Serializable;

@Data
//Utilisation de name et nationality en tant que clés primaire même si j'aurais utiliser un id
public class Coach implements Serializable {
    private String name;
    private String nationality;
}
