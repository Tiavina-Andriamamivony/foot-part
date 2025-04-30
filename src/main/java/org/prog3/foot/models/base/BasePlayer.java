package org.prog3.foot.models.base;

import lombok.Data;
import org.prog3.foot.models.PlayerPosition;

@Data
/**
 * Il désigne le player de base sans le club notemment
 *
 * Il sera utiliser dans PUT/players, GET/club/{id}/players
 */
public class BasePlayer {
    private String id;
    private String name;
    private Integer age;
    private String nationality;
    private PlayerPosition playerPosition;
    private Integer number;
}
