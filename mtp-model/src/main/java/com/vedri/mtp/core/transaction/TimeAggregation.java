package com.vedri.mtp.core.transaction;

import lombok.*;

import com.vedri.mtp.core.support.cassandra.ColumnUtils;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public abstract class TimeAggregation {

    private int year;
    private int month;
    private int day;
    private int hour;

    public enum TimeFields {
        year, month, day, hour;

        public final ColumnUtils.Field<TimeFields> F = ColumnUtils.createField(this);
    }
}
