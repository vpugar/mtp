package com.vedri.mtp.processor.support.spark;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import com.vedri.mtp.processor.ProcessorProperties;
import lombok.extern.slf4j.Slf4j;

import org.apache.spark.SparkConf;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.StreamingContext;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.vedri.mtp.processor.MtpProcessorConstants;

@Configuration
@Slf4j
public class SparkConfiguration {

	@Autowired
	private ProcessorProperties processorProperties;

	@Bean
	StreamingContext sparkStreamingContext() {

		final ProcessorProperties.Spark spark = processorProperties.getSpark();

		SparkConf sparkConf = new SparkConf().setAppName(MtpProcessorConstants.NAME)
				.setMaster(spark.getMaster())
				.set("spark.cassandra.connection.host", spark.getCassandraHosts())
				.set("spark.cleaner.ttl", String.valueOf(spark.getCleanerTtl()));


		// Spark Streaming context
		return new StreamingContext(sparkConf, Duration.apply(spark.getBatchInterval()));
	}

	@Bean
	JavaStreamingContext javaStreamingContext() {
		return new JavaStreamingContext(sparkStreamingContext());
	}

	@PostConstruct
	public void init() {
		log.info("Init spark");
	}

	@PreDestroy
	public void destroy() {
		log.info("Destroy spark");
		sparkStreamingContext().stop(true, true);
	}
}
