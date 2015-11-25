package com.vedri.mtp.core;

import org.joda.time.DateTimeZone;

import java.math.BigDecimal;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public interface MtpConstants {

	String TRANSACTION_DATE_FORMAT = "ddMMMyy HH:mm:ss";

	// profiles
	String SPRING_PROFILE_DEVELOPMENT = "dev";
	String SPRING_PROFILE_PRODUCTION = "prod";

	// configs
	String CONFIG_PREFIX = "mtp.core.";

	// points
	String CURRENCY_POINTS = "EUR";
	// Max counter value: 9223372036854.775807
	int CURRENCY_SCALE = 2;
	int RATE_SCALE = 4;
	int CURRENCY_POINTS_SCALE = 6;
	int CURRENCY_ROUNDING = BigDecimal.ROUND_HALF_UP;

	// number format
	String CURRENCY_FORMAT = "#.##";
	String RATE_FORMAT = "#.####";
	DecimalFormatSymbols DEFAULT_FORMAT_SYMBOLS = DecimalFormatSymbols.getInstance(Locale.ENGLISH);

	// date
	DateTimeZone DEFAULT_TIME_ZONE = DateTimeZone.UTC;
}
