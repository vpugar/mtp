package com.vedri.mtp.core.transaction.aggregation;

import com.vedri.mtp.core.MtpConstants;
import lombok.*;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

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
        dateTime = dateTime.withZone(MtpConstants.DEFAULT_TIME_ZONE);
        year = dateTime.getYear();
        month = dateTime.getMonthOfYear();
        day = dateTime.getDayOfMonth();
        hour = dateTime.getHourOfDay();
    }
}
