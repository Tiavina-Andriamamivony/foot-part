package org.prog3.foot.models.post;

import lombok.Data;

@Data
/**
 * Utilisation: POST/seasons
 *
 * Une version simplifié de season pour faciliter le post de ressource
 */
public class PostSeason {
    private Integer year;
    private String alias;
}
