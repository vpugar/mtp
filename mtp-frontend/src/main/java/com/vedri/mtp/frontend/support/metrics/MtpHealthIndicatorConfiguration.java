package com.vedri.mtp.frontend.support.metrics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.datastax.driver.core.Session;

@Configuration
public class MtpHealthIndicatorConfiguration {

	@Autowired
	private Session session;

	@Bean
	public HealthIndicator cassandraHealthIndicator() {
		return new CassandraHealthIndicator(session);
	}
}
