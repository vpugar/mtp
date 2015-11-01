package com.vedri.mtp.core.support.cassandra;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PreDestroy;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.vedri.mtp.core.CoreProperties;

@Configuration
@Slf4j
public class CassandraConfiguration {

	@Autowired
	private CoreProperties.Cassandra cassandra;

	private Cluster cluster;

	@PreDestroy
	public void destroy() {
		cluster.close();
	}

	@Bean
	public Session session() {
		List<InetSocketAddress> addresses = new ArrayList<>();

		for (String contactPoint : cassandra.getHosts()) {
			InetSocketAddress address = new InetSocketAddress(contactPoint, cassandra.getPort());
			addresses.add(address);
		}

		this.cluster = Cluster.builder().addContactPointsWithPorts(addresses).build();

		return cluster.connect(cassandra.getKeyspace());
	}

}
