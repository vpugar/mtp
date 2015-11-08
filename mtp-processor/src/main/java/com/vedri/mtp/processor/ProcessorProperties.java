package com.vedri.mtp.processor;

import com.vedri.mtp.core.CoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = MtpProcessorConstants.CONFIG_PREFIX, ignoreUnknownFields = false)
@Getter
public class ProcessorProperties {

    private final Zookeeper zookeeper = new Zookeeper();
    private final Spark spark = new Spark();
    private final KafkaServer kafkaServer = new KafkaServer();
    private final CoreProperties.Cluster cluster = new CoreProperties.Cluster();
    private final CoreProperties.Akka akka = new CoreProperties.Akka();
    private final CoreProperties.Cassandra cassandra = new CoreProperties.Cassandra();

    @Getter
    @Setter
    public static class Zookeeper {
        private String connect;
    }

    @Getter
    @Setter
    public static class Spark {
        private String master;
        private String cassandraHosts;
        private String cleanerTtl;
        private long batchInterval;
        private String checkpointDir;
        private Kryoserializer kryoserializer = new Kryoserializer();

        @Getter
        @Setter
        public static class Kryoserializer {
            private int buffer = 24;
        }
    }

    @Getter
    @Setter
    public static class KafkaServer {
        private String host;
        private Integer port;
        private String advertisedHost;
        private Integer advertisedPort;
        private String groupId;
        private Topic topic = new Topic();

        @Getter
        @Setter
        public static class Topic {
            private String name;
            private int numPartitions;
            private int replicationFactor;
        }
    }
}
