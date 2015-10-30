package com.vedri.mtp.core;

public interface MtpConstants {

	// profiles
	String SPRING_PROFILE_DEVELOPMENT = "dev";
	String SPRING_PROFILE_PRODUCTION = "prod";

	// configs
	String CONFIG_PREFIX = "mtp.core.";
	String NODE_NAME = "${mtp.core.cluster.nodeName}";

	String AKKA_CONFIGURATION_NAME = "${mtp.core.akka.configurationName}";
	String AKKA_LOG_CONFIGURATION = "${mtp.core.akka.logConfiguration}";
	String AKKA_LOG_CLUSTER_METRICS = "${mtp.core.akka.logClusterMetrics}";

	String CASSANDRA_CONTACT_POINTS = "${mtp.core.cassandra.contactPoints}";
	String CASSANDRA_PORT = "${mtp.core.cassandra.port}";
	String CASSANDRA_KEYSPACE = "${mtp.core.cassandra.keyspace}";
}
