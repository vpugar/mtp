package com.vedri.mtp.processor;

import com.vedri.mtp.core.support.cassandra.CassandraConfiguration;
import com.vedri.mtp.core.support.json.JacksonConfiguration;
import com.vedri.mtp.processor.support.kafka.KafkaConfiguration;
import com.vedri.mtp.processor.support.spark.SparkConfiguration;
import org.springframework.context.ApplicationContext;

import com.vedri.mtp.core.CoreConfig;
import com.vedri.mtp.core.support.spring.AbstractApplication;

public class ProcessorApplication extends AbstractApplication {

	@Override
	protected Class[] getConfigs() {
		return new Class[] {
				CoreConfig.class, ProcessorConfig.class, JacksonConfiguration.class, CassandraConfiguration.class,
				KafkaConfiguration.class, SparkConfiguration.class
		};
	}

	@Override
	protected void doStart(ApplicationContext context) throws Exception {

	}

	public static void main(String[] args) throws Exception {
		new ProcessorApplication().startApplication(args);
	}
}
