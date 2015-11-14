package com.vedri.mtp.core.transaction.aggregation;

import lombok.*;

import com.vedri.mtp.core.support.cassandra.ColumnUtils;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public abstract class TimeAggregation {

    private YearToHourTime yearToHourTime;

    public enum TimeFields {
        year, month, day, hour;

        public final ColumnUtils.Field<TimeFields> F = ColumnUtils.createField(this);
    }

    public Integer getYear() {
        return yearToHourTime.getYear();
    }

    public Integer getMonth() {
        return yearToHourTime.getMonth();
    }

    public Integer getDay() {
        return yearToHourTime.getDay();
    }

    public Integer getHour() {
        return yearToHourTime.getHour();
    }

    public void setYear(Integer year) {
        yearToHourTime.setYear(year);
    }

    public void setMonth(Integer month) {
        yearToHourTime.setMonth(month);
    }

    public void setDay(Integer day) {
        yearToHourTime.setDay(day);
    }

    public void setHour(Integer hour) {
        yearToHourTime.setHour(hour);
    }
}
