package com.vedri.mtp.core.support.cassandra;

import java.text.ParseException;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

import org.apache.commons.lang3.time.FastDateFormat;
import org.joda.time.DateTime;

public class CassandraPartitionForMinuteUtils {

	private static final FastDateFormat dateFormatMinute = FastDateFormat.getInstance(
			"yyyy-MM-dd_HH:mm", TimeZone.getTimeZone("UTC"));

	public static String getPartitionIdFromMillis(long millis) {
		return dateFormatMinute.format(millis);
	}

	public static String getPartitionIdFromTimeUUID(String timeUUID) {
		return dateFormatMinute.format(CassandraUtils.getDateTime(timeUUID).toDate());
	}

	public static String getPartitionIdFromTimeUUID(UUID timeUUID) {
		return dateFormatMinute.format(CassandraUtils.getTimeFromUUID(timeUUID));
	}

	public static String offsetPartitionIntoHistoryBy(String partition, int minutes) {
		try {
			Date partitionStartDate = dateFormatMinute.parse(partition);
			DateTime partitionStart = new DateTime(partitionStartDate);

			return dateFormatMinute.format(partitionStart.minusMinutes(minutes).toDate());
		}
		catch (ParseException e) {
			throw new IllegalArgumentException("Error calculating offsetPartitionIntoHistoryBy", e);
		}
	}

	public static String datePartition(DateTime date) {
		return dateFormatMinute.format(date.toDate());
	}
}
