package com.vedri.mtp.frontend.config;

import java.time.*;

import com.fasterxml.jackson.datatype.joda.JodaModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import com.vedri.mtp.frontend.support.datetime.JSR310DateTimeSerializer;
import com.vedri.mtp.frontend.support.datetime.JSR310LocalDateDeserializer;

@Configuration
public class JacksonConfiguration {

	@Bean
	Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder() {
		JodaModule module = new JodaModule();
		module.addSerializer(OffsetDateTime.class, JSR310DateTimeSerializer.INSTANCE);
		module.addSerializer(ZonedDateTime.class, JSR310DateTimeSerializer.INSTANCE);
		module.addSerializer(LocalDateTime.class, JSR310DateTimeSerializer.INSTANCE);
		module.addSerializer(Instant.class, JSR310DateTimeSerializer.INSTANCE);
		module.addDeserializer(LocalDate.class, JSR310LocalDateDeserializer.INSTANCE);
		return new Jackson2ObjectMapperBuilder()
				.findModulesViaServiceLoader(true)
				.modulesToInstall(module);
	}
}
