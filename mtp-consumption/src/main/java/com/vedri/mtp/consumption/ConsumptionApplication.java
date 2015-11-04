package com.vedri.mtp.consumption;

import com.vedri.mtp.core.support.cassandra.CassandraConfiguration;
import com.vedri.mtp.core.support.json.JacksonConfiguration;
import com.vedri.mtp.core.support.kyro.KryoConfiguration;
import org.springframework.context.ApplicationContext;

import com.vedri.mtp.core.CoreConfig;
import com.vedri.mtp.core.support.spring.AbstractApplication;

public class ConsumptionApplication extends AbstractApplication {

	@Override
	protected Class[] getConfigs() {
		return new Class[] {
				CoreConfig.class, ConsumptionConfig.class, JacksonConfiguration.class, CassandraConfiguration.class,
				KryoConfiguration.class
		};
	}

	@Override
	protected void doStart(ApplicationContext context) throws Exception {

	}

	public static void main(String[] args) throws Exception {
		new ConsumptionApplication().startApplication(args);
	}
}
