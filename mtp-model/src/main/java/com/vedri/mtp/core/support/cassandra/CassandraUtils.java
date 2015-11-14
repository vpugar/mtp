package com.vedri.mtp.core.support.cassandra;

import java.security.SecureRandom;
import java.util.UUID;

import org.joda.time.DateTime;

public class CassandraUtils {

	private static final long NUM_100NS_INTERVALS_SINCE_UUID_EPOCH = 0x01b21dd213814000L;

	private static SecureRandom random = new SecureRandom();

	public static UUID createUUIDFromMillis(long millis) {
		return new UUID(makeMSB(millis), random.nextLong());
	}

	public static DateTime getDateTime(String uuidString) {
		UUID uuid = UUID.fromString(uuidString);
		return new DateTime(getTimeFromUUID(uuid));
	}

	public static long getTimeFromUUID(UUID uuid) {
		return (uuid.timestamp() - NUM_100NS_INTERVALS_SINCE_UUID_EPOCH) / 10000;
	}

	private static long makeMSB(long currentTime) {
		long time;

		// UTC time
		long timeToUse = (currentTime * 10000) + NUM_100NS_INTERVALS_SINCE_UUID_EPOCH;

		// time low
		time = timeToUse << 32;

		// time mid
		time |= (timeToUse & 0xFFFF00000000L) >> 16;

		// time hi and version
		time |= 0x1000 | ((timeToUse >> 48) & 0x0FFF); // version 1
		return time;
	}
}
