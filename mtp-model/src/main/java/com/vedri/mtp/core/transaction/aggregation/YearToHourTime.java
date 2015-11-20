package com.vedri.mtp.core.transaction.aggregation;

import lombok.*;
import org.joda.time.DateTime;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class YearToHourTime implements Serializable {

    private Integer year;
    private Integer month;
    private Integer day;
    private Integer hour;

    public YearToHourTime(DateTime dateTime) {
        year = dateTime.getYear();
        month = dateTime.getMonthOfYear();
        day = dateTime.getDayOfMonth();
        hour = dateTime.getHourOfDay();
    }
}
