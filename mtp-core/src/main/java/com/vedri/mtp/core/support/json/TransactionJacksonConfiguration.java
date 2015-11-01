package com.vedri.mtp.core.support.json;

import org.joda.time.DateTime;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.vedri.mtp.core.MtpConstants;

@Configuration
public class TransactionJacksonConfiguration {

	@Bean
	public JodaModule transactionJodaModule() {
		final JodaModule jodaModule = new JodaModule();
		jodaModule.addDeserializer(DateTime.class,
				new CustomDateDateTimeDeserializer(MtpConstants.TRANSACTION_DATE_FORMAT));
		return jodaModule;
	}

	@Bean
	public ObjectMapper transactionObjectMapper() {
		final ObjectMapper objectMapper = new ObjectMapper().enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY);
		objectMapper.registerModule(transactionJodaModule());
		return objectMapper;
	}
}
