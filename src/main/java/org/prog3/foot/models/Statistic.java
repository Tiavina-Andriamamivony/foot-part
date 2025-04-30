package org.prog3.foot.models;

import lombok.Data;

import java.io.Serializable;

@Data
/**
 * Utilisation: GET /players/{id}/statistics/{seasonYear}
 * Description: Obtenir les statistiques d'un joueur spécifique a une date donnée
 */
public class Statistic implements Serializable {
    private String id;
    private Integer scoreGoals;
    private PlayingTime playingTime;

}
