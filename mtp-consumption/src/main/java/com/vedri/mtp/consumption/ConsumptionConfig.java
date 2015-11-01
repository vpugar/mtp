package com.vedri.mtp.consumption;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.vedri.mtp.core.CoreProperties;

@ComponentScan(basePackages = {
		"com.vedri.mtp.consumption.http",
		"com.vedri.mtp.core.support.json",
		"com.vedri.mtp.consumption.support.kafka",
		"com.vedri.mtp.consumption.transaction"
})
@Configuration
@EnableConfigurationProperties({ ConsumptionProperties.class })
public class ConsumptionConfig {

	@Autowired
	private ConsumptionProperties consumptionProperties;

	@Bean
	public CoreProperties.Cluster clusterProperties() {
		return consumptionProperties.getCluster();
	}

	@Bean
	public CoreProperties.Cassandra cassandraProperties() {
		return consumptionProperties.getCassandra();
	}

	@Bean
	public CoreProperties.Akka akkaProperties() {
		return consumptionProperties.getAkka();
	}

}
