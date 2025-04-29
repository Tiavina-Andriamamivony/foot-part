package org.prog3.foot.models.base;

import lombok.Data;
import org.prog3.foot.models.Position;

@Data
/**
 * Il désigne le player de base sans le club notemment
 *
 * Il sera utiliser dans PUT/players
 */
public class BasePlayer {
    private String id;
    private String name;
    private Integer age;
    private String nationality;
    private Position position;
    private Integer number;

}
