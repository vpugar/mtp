package com.vedri.mtp.core.support.json;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

@Configuration
public class JacksonConfiguration {

	@Bean
	public JodaModule jodaModule() {
		final JodaModule jodaModule = new JodaModule();
		return jodaModule;
	}

	@Bean
	public ObjectMapper objectMapper() {
		final ObjectMapper objectMapper = new ObjectMapper().enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY);
		objectMapper.registerModule(jodaModule());
		return objectMapper;
	}
}
