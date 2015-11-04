package com.vedri.mtp.processor.support.spark;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import lombok.extern.slf4j.Slf4j;

import org.apache.spark.SparkConf;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.StreamingContext;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.vedri.mtp.core.transaction.Transaction;
import com.vedri.mtp.processor.MtpProcessorConstants;
import com.vedri.mtp.processor.ProcessorProperties;

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
				.set("spark.cleaner.ttl", String.valueOf(spark.getCleanerTtl()))
				.set("spark.serializer", org.apache.spark.serializer.KryoSerializer.class.getName())
				.set("spark.kryoserializer.buffer.mb", String.valueOf(spark.getKryoserializer().getBufferMb()))
				.set("spark.kryo.classesToRegister", Transaction.class.getName())
				.set("spark.kryo.registrator", SparkKryoRegistrator.class.getName());

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
