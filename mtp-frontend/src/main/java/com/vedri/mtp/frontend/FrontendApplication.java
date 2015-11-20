package com.vedri.mtp.frontend;

import java.net.InetAddress;

import lombok.extern.slf4j.Slf4j;

import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import com.vedri.mtp.core.CoreConfig;
import com.vedri.mtp.core.support.cassandra.CassandraConfiguration;
import com.vedri.mtp.core.support.spring.AbstractApplication;
import com.vedri.mtp.processor.support.spark.SparkConfiguration;

@Slf4j
public class FrontendApplication extends AbstractApplication {

	@Override
	protected Class[] getConfigs() {
		return new Class[] {
				CoreConfig.class, FrontendConfig.class, CassandraConfiguration.class, SparkConfiguration.class
		};
	}

	@Override
	protected void doStart(ApplicationContext context) throws Exception {
		log.info("Starting web application");
		final Environment env = context.getEnvironment();

		log.info("Access URLs:\n" +
				"----------------------------------------------------------\n" +
				"\tLocal: \t\thttp://127.0.0.1:{}\n" +
				"\tExternal: \thttp://{}:{}\n" +
				"----------------------------------------------------------",
				env.getProperty("server.port"),
				InetAddress.getLocalHost().getHostAddress(),
				env.getProperty("server.port"));
	}

	public static void main(String[] args) throws Exception {
		new FrontendApplication().startApplication(args);
	}
}
