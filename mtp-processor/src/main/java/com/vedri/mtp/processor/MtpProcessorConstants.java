package com.vedri.mtp.processor;

public interface MtpProcessorConstants {

	String NAME = "MtpProcessor";

	// configs
	String CONFIG_PREFIX = "mtp.processor";

	String SPARK_MASTER = "${mtp.processor.spark.master}";
	String SPARK_CASSANDRA_HOSTS = "${mtp.processor.spark.cassandra.hosts}";
	String SPARK_CLEANER_TTL = "${mtp.processor.spark.cleaner.ttl}";
	String SPARK_BATCH_INTERVAL = "${mtp.processor.spark.batch.interval}";
	String SPARK_CHECKPOINT_DIR = "${mtp.processor.spark.checkpoint.dir}";

	String KAFKA_HOST_NAME = "${mtp.processor.kafka.host.name}";
	String KAFKA_PORT = "${mtp.processor.kafka.port}";
	String KAFKA_GROUP_ID = "${mtp.processor.kafka.group.id}";
	String KAFKA_TOPIC_NAME = "${mtp.processor.kafka.topic.name}";
	String KAFKA_TOPIC_NUM_PARTITIONS = "${mtp.processor.kafka.topic.numPartitions}";
	String KAFKA_TOPIC_REPLICATION_FACTOR = "${mtp.processor.kafka.topic.replicationFactor}";

	String ZOOKEEPER_CONNECT = "${mtp.processor.zookeeper.connect}";
}
