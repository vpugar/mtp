package com.vedri.mtp.core.support.json;

import java.io.IOException;
import java.util.TimeZone;

import com.vedri.mtp.core.MtpConstants;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;

public class CustomDateDateTimeDeserializer extends StdScalarDeserializer<DateTime> {

	private DateTimeFormatter dateTimeFormatter;

	@SuppressWarnings("unchecked")
	public static JsonDeserializer<DateTime> create(final String format) {
		return new CustomDateDateTimeDeserializer(format);
	}

	public CustomDateDateTimeDeserializer(final String format) {
		super(DateTime.class);
		dateTimeFormatter = DateTimeFormat.forPattern(format);
	}

	@SuppressWarnings("deprecation")
	@Override
	public DateTime deserialize(JsonParser jp, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		JsonToken t = jp.getCurrentToken();
		TimeZone tz = ctxt.getTimeZone();
		DateTimeZone dtz = (tz == null) ? MtpConstants.DEFAULT_TIME_ZONE : DateTimeZone.forTimeZone(tz);

		if (t == JsonToken.VALUE_NUMBER_INT) {
			return new DateTime(jp.getLongValue(), dtz);
		}
		if (t == JsonToken.VALUE_STRING) {
			String str = jp.getText().trim();
			if (str.length() == 0) { // [JACKSON-360]
				return null;
			}
			if (ctxt.isEnabled(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE))
				return dateTimeFormatter.parseDateTime(str).withZoneRetainFields(dtz);
			else
				return dateTimeFormatter.parseDateTime(str);
		}
		// TODO: in 2.4, use 'handledType()'
		throw ctxt.mappingException(getValueClass());
	}
}
