package com.vedri.mtp.frontend;

import com.vedri.mtp.processor.support.spark.SparkConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

import com.vedri.mtp.core.CoreConfig;
import com.vedri.mtp.core.support.cassandra.CassandraConfiguration;
import com.vedri.mtp.core.support.spring.AbstractApplication;
import org.springframework.core.env.Environment;

import java.net.InetAddress;

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

		log.info("Access URLs:\n----------------------------------------------------------\n\t" +
						"Local: \t\thttp://127.0.0.1:{}\n\t" +
						"External: \thttp://{}:{}\n----------------------------------------------------------",
				env.getProperty("server.port"),
				InetAddress.getLocalHost().getHostAddress(),
				env.getProperty("server.port"));
	}

	public static void main(String[] args) throws Exception {
		new FrontendApplication().startApplication(args);
	}
}
