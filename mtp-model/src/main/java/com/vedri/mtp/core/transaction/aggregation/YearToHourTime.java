package com.vedri.mtp.core.transaction.aggregation;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class YearToHourTime {
    private Integer year;
    private Integer month;
    private Integer day;
    private Integer hour;
}
