package com.vedri.mtp.core.support.cassandra;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PreDestroy;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.vedri.mtp.core.MtpConstants;

@Configuration
@Slf4j
public class CassandraConfiguration {

	@Value(MtpConstants.CASSANDRA_CONTACT_POINTS)
	private String[] splitContactPoints;
	@Value(MtpConstants.CASSANDRA_PORT)
	private int port;
	@Value(MtpConstants.CASSANDRA_KEYSPACE)
	private String keyspace;

	private Cluster cluster;

	@PreDestroy
	public void destroy() {
		cluster.close();
	}

	@Bean
	public Session session() {
		List<InetSocketAddress> addresses = new ArrayList<>();

		for (String contactPoint : splitContactPoints) {
			InetSocketAddress address = new InetSocketAddress(contactPoint, port);
			addresses.add(address);
		}

		this.cluster = Cluster.builder().addContactPointsWithPorts(addresses).build();

		return cluster.connect(keyspace);
	}

}
