package com.vedri.mtp.processor.support.spark;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "mtp.core.spark", ignoreUnknownFields = false)
@Getter
public class CoreSparkProperties {

    private Spark spark;

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
}
