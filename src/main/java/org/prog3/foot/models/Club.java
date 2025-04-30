package org.prog3.foot.models;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.Year;

@EqualsAndHashCode(callSuper = true)
@Data
public class Club extends ClubMinimumInfo implements Serializable {
  private Integer yearCreation;
  private String stadium;
  private Coach coach;
}
