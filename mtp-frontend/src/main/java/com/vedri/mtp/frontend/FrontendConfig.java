package com.vedri.mtp.frontend;

import com.vedri.mtp.core.CoreProperties;
import com.vedri.mtp.processor.support.spark.CoreSparkProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.MetricFilterAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.MetricRepositoryAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.cassandra.CassandraDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.cassandra.CassandraRepositoriesAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = {
        "com.vedri.mtp.core.transaction",
        "com.vedri.mtp.core.country",
        "com.vedri.mtp.core.currency",
        "com.vedri.mtp.frontend.config",
        "com.vedri.mtp.frontend.support",
        "com.vedri.mtp.frontend.user",
        "com.vedri.mtp.frontend.web",
        "com.vedri.mtp.frontend.transaction"
})
@EnableAutoConfiguration(exclude = { MetricFilterAutoConfiguration.class, MetricRepositoryAutoConfiguration.class,
        CassandraDataAutoConfiguration.class, CassandraRepositoriesAutoConfiguration.class})
@EnableConfigurationProperties({ FrontendProperties.class })
public class FrontendConfig {

    @Autowired
    private FrontendProperties frontendProperties;

    @Bean
    public CoreProperties.Akka akka() {
        return frontendProperties.getAkka();
    }

    @Bean
    public CoreProperties.Cluster clusterProperties() {
        return frontendProperties.getCluster();
    }

    @Bean
    public CoreSparkProperties.Spark spark() {
        return frontendProperties.getSpark();
    }

    @Bean
    public CoreProperties.Cassandra cassandraProperties() {
        return frontendProperties.getCassandra();
    }
}
