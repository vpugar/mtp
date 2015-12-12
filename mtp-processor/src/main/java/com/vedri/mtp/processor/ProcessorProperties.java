package com.vedri.mtp.processor;

import com.vedri.mtp.core.CoreProperties;
import com.vedri.mtp.processor.support.spark.CoreSparkProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = MtpProcessorConstants.CONFIG_PREFIX, ignoreUnknownFields = false)
@Getter
public class ProcessorProperties {

    private final Zookeeper zookeeper = new Zookeeper();
    private final KafkaServer kafkaServer = new KafkaServer();
    private final CoreSparkProperties.Spark spark = new CoreSparkProperties.Spark();
    private final CoreProperties.Cluster cluster = new CoreProperties.Cluster();
    private final CoreProperties.Akka akka = new CoreProperties.Akka();
    private final CoreProperties.Cassandra cassandra = new CoreProperties.Cassandra();
    private final CoreProperties.CfRate cfRate = new CoreProperties.CfRate();

    @Getter
    @Setter
    public static class Zookeeper {
        private String connect;
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
