package com.vedri.mtp.core;

import lombok.Getter;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "mtp.core", ignoreUnknownFields = false)
@Getter
public class CoreProperties {

	private final Cluster cluster = new Cluster();
	private final Akka akka = new Akka();
	private final Cassandra cassandra = new Cassandra();

	@Getter
	@Setter
	public static class Cluster {
		private String nodeName;
	}

	@Getter
	@Setter
	public static class Akka {
		private String akkaSystemName;
		private boolean logConfiguration;
		private boolean logClusterMetrics;
	}

	@Getter
	@Setter
	public static class Cassandra {
		private String[] hosts;
		private int port;
		private String keyspace;
	}
}
