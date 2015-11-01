package com.vedri.mtp.consumption;

public interface MtpConsumptionConstants {

    // configs
    String CONFIG_PREFIX = "mtp.consumption";

    String SERVER_BIND_HOST = "${mtp.consumption.server.bind.host}";
    String SERVER_BIND_PORT = "${mtp.consumption.server.bind.port}";

    String SERVER_PUBLIC_PROTOCOL = "${mtp.consumption.server.public.protocol}";
    String SERVER_PUBLIC_HOST = "${mtp.consumption.server.public.host}";
    String SERVER_PUBLIC_PORT = "${mtp.consumption.server.public.port}";

    String KAFKA_BOOTSTRAP_SERVERS = "${mtp.consumption.kafka.bootstrap.servers}";
    String KAFKA_KEY_SERIALIZER = "${mtp.consumption.kafka.key.serializer}";
    String KAFKA_VALUE_SERIALIZER = "${mtp.consumption.kafka.value.serializer}";
    String KAFKA_ACKS = "${mtp.consumption.kafka.acks}";
    String KAFKA_BATCH_SIZE = "${mtp.consumption.kafka.batch.size}";
    String KAFKA_TOPIC_NAME = "${mtp.consumption.kafka.topic.name}";
    String KAFKA_RECONNECT_BACKOFF_MS = "${mtp.consumption.kafka.reconnect.backoff.ms}";

    String BALANCING_POOL_INSTANCES = "${mtp.consumption.balancing.pool.instances}";
}
