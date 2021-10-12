package org.interview.model.DTO;

import lombok.Getter;

import java.util.Date;

@Getter
public class StatisticDTO {

    private final Date executionDate;
    private final Integer results;
    private final Integer time;

    public StatisticDTO(Integer results, Integer time) {
        this.executionDate = new Date();
        this.results = results;
        this.time = time;
    }
}
