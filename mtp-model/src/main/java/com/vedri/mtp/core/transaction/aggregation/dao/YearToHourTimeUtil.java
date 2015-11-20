package com.vedri.mtp.core.transaction.aggregation.dao;

import java.util.List;

import com.vedri.mtp.core.transaction.aggregation.YearToHourTime;

public class YearToHourTimeUtil {

	private static final String YEAR_PART = " and year = ?";
	private static final String MONTH_PART = " and month = ?";
	private static final String DAY_PART = " and day = ?";
	private static final String HOUR_PART = " and hour = ?";

	public static String composeQuery(YearToHourTime yearToHourTime, String prefixQuery, List<Object> args) {

		StringBuilder stringBuilder = new StringBuilder(prefixQuery);

		if (yearToHourTime.getYear() != null) {
			stringBuilder.append(YEAR_PART);
			args.add(yearToHourTime.getYear());
		}
		if (yearToHourTime.getMonth() != null) {
			stringBuilder.append(MONTH_PART);
			args.add(yearToHourTime.getMonth());
		}
		if (yearToHourTime.getDay() != null) {
			stringBuilder.append(DAY_PART);
			args.add(yearToHourTime.getDay());
		}
		if (yearToHourTime.getHour() != null) {
			stringBuilder.append(HOUR_PART);
			args.add(yearToHourTime.getHour());
		}

        return stringBuilder.toString();
	}

}
