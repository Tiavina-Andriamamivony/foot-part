package org.prog3.foot.models;

import lombok.Data;

import java.time.Year;

@Data
public class Club {
  private Integer yearCreation;
  private String stadium;
  private Coach coach;
  private String id;
  private String name;
  private String acronym;

}
