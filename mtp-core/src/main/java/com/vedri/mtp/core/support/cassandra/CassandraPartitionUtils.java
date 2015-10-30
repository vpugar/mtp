package com.vedri.mtp.core.support.cassandra;

import java.text.ParseException;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

import org.apache.commons.lang3.time.FastDateFormat;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public class CassandraPartitionUtils {

	private static final FastDateFormat dateFormatMinute = FastDateFormat.getInstance(
			"yyyy-MM-dd_HH:mm", TimeZone.getTimeZone("UTC"));

	private static final FastDateFormat dateFormatHour = FastDateFormat.getInstance(
			"yyyy-MM-dd_HH", TimeZone.getTimeZone("UTC"));

	public static String getPartitionIdFromMillisForMinute(long millis) {
		return dateFormatMinute.format(millis);
	}

	public static String getPartitionIdFromTimeUUIDForMinute(String timeUUID) {
		return dateFormatMinute.format(CassandraUtils.getDateTime(timeUUID).toDate());
	}

	public static String getPartitionIdFromTimeUUIDForMinute(UUID timeUUID) {
		return dateFormatMinute.format(CassandraUtils.getTimeFromUUID(timeUUID));
	}

	public static String getPartitionIdFromTimeUUIDForHour(String timeUUID) {
		return dateFormatHour.format(CassandraUtils.getDateTime(timeUUID).toDate());
	}

	public static String getCurrentHourPartition() {
		return dateFormatHour.format(new DateTime(DateTimeZone.UTC).toDate());
	}

	public static String getCurrentMinutePartition() {
		return dateFormatHour.format(new DateTime(DateTimeZone.UTC).toDate());
	}

	public static String offsetHourPartitionIntoHistoryBy(String partition, int hours) {
		try {
			Date partitionStartDate = dateFormatHour.parse(partition);
			DateTime partitionStart = new DateTime(partitionStartDate);

			return dateFormatHour.format(partitionStart.minusHours(hours).toDate());
		}
		catch (ParseException e) {
			throw new IllegalArgumentException("Error calculating offsetPartitionIntoHistoryBy", e);
		}
	}

	public static String offsetMinutePartitionIntoHistoryBy(String partition, int minutes) {
		try {
			Date partitionStartDate = dateFormatMinute.parse(partition);
			DateTime partitionStart = new DateTime(partitionStartDate);

			return dateFormatMinute.format(partitionStart.minusMinutes(minutes).toDate());
		}
		catch (ParseException e) {
			throw new IllegalArgumentException("Error calculating offsetPartitionIntoHistoryBy", e);
		}
	}

	public static String dateToHourPartition(DateTime date) {
		return dateFormatHour.format(date.toDate());
	}
}
