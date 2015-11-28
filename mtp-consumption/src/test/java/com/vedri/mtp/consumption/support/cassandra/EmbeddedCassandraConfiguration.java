package com.vedri.mtp.consumption.support.cassandra;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.vedri.mtp.core.support.cassandra.CassandraConfiguration;

@Configuration
@ComponentScan(basePackages = { "com.vedri.mtp.consumption.support.cassandra" })
public class EmbeddedCassandraConfiguration extends CassandraConfiguration {

	@Autowired
	private EmbeddedCassandra embeddedCassandra;

	@Autowired
	private EmbeddedCassandraSupport embeddedCassandraSupport;

}
