package org.prog3.foot.models;

import lombok.Data;

/**
 * For example, if season.year = 2024, alias would be "S2024-2025".
 * If season.year = 2025, alias wold be "S2025-2026".
 */
@Data
public class CreateSeason {
        private Integer year;
        private String alias=GenerateSeasonAlias();



        public String GenerateSeasonAlias(){
            setAlias("S"+year+"-"+year+1);
            return alias;
        }
}
