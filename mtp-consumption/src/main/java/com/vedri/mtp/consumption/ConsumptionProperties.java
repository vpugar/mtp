package com.vedri.mtp.consumption;

import com.vedri.mtp.core.CoreProperties;
import lombok.Getter;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = MtpConsumptionConstants.CONFIG_PREFIX, ignoreUnknownFields = false)
@Getter
public class ConsumptionProperties {

	private final HttpServer httpServer = new HttpServer();
    private final KafkaClient kafkaClient = new KafkaClient();
    private final BalancingPool balancingPool = new BalancingPool();
    private final CoreProperties.Cluster cluster = new CoreProperties.Cluster();
    private final CoreProperties.Cassandra cassandra = new CoreProperties.Cassandra();
    private final CoreProperties.Akka akka = new CoreProperties.Akka();

	@Getter
	@Setter
	public static class HttpServer {
		private String bindHost;
		private int bindPort;
        private String publicProtocol;
        private String publicHost;
        private int publicPort;
	}

    @Getter
    @Setter
    public static class KafkaClient {
        private String bootstrapServers;
        private String keySerializer;
        private String valueSerializer;
        private String acks;
        private String batchSize;
        private String topicName;
        private String reconnectBackoffMs;
    }

    @Getter
    @Setter
    public static class BalancingPool {
        private int instances;
    }
}
