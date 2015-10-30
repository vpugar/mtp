package com.vedri.mtp.processor.support.spark;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import lombok.extern.slf4j.Slf4j;

import org.apache.spark.SparkConf;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.StreamingContext;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.vedri.mtp.processor.MtpProcessorConstants;

@Configuration
@Slf4j
public class SparkConfiguration {

	@Value(MtpProcessorConstants.SPARK_MASTER)
	private String sparkMaster;

	@Value(MtpProcessorConstants.SPARK_CASSANDRA_HOSTS)
	private String cassandraHosts;

	@Value(MtpProcessorConstants.SPARK_CLEANER_TTL)
	private int ttl;

	@Value(MtpProcessorConstants.SPARK_BATCH_INTERVAL)
	private long sparkStreamingBatchInterval;

	@Bean
	StreamingContext sparkStreamingContext() {

		SparkConf sparkConf = new SparkConf().setAppName(MtpProcessorConstants.NAME)
				.setMaster(sparkMaster)
				.set("spark.cassandra.connection.host", cassandraHosts)
				.set("spark.cleaner.ttl", String.valueOf(ttl));


		// Spark Streaming context
		return new StreamingContext(sparkConf, Duration.apply(sparkStreamingBatchInterval));
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
