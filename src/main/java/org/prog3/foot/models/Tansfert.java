package org.prog3.foot.models;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Tansfert {
    private Player player;
    private Club club;
    private LocalDate date;
}
