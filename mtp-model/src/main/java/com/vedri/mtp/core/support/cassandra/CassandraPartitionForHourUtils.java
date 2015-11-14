package com.vedri.mtp.core.support.cassandra;

import java.text.ParseException;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

import org.apache.commons.lang3.time.FastDateFormat;
import org.joda.time.DateTime;

public class CassandraPartitionForHourUtils {

	private static final FastDateFormat dateFormatHour = FastDateFormat.getInstance(
			"yyyy-MM-dd_HH", TimeZone.getTimeZone("UTC"));

	public static String getPartitionIdFromMillis(long millis) {
		return dateFormatHour.format(millis);
	}

	public static String getPartitionIdFromTimeUUID(String timeUUID) {
		return dateFormatHour.format(CassandraUtils.getDateTime(timeUUID).toDate());
	}

	public static String getPartitionIdFromTimeUUID(UUID timeUUID) {
		return dateFormatHour.format(CassandraUtils.getTimeFromUUID(timeUUID));
	}

	public static String offsetPartitionIntoHistoryBy(String partition, int hours) {
		try {
			Date partitionStartDate = dateFormatHour.parse(partition);
			DateTime partitionStart = new DateTime(partitionStartDate);

			return dateFormatHour.format(partitionStart.minusHours(hours).toDate());
		}
		catch (ParseException e) {
			throw new IllegalArgumentException("Error calculating offsetPartitionIntoHistoryBy", e);
		}
	}

	public static String datePartition(DateTime date) {
		return dateFormatHour.format(date.toDate());
	}
}
