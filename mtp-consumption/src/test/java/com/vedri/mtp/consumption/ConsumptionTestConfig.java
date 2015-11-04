package com.vedri.mtp.consumption;

import com.vedri.mtp.core.CoreProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({ ConsumptionProperties.class })
public class ConsumptionTestConfig {

    @Autowired
    private ConsumptionProperties consumptionProperties;

    @Bean
    public CoreProperties.Akka akkaProperties() {
        return consumptionProperties.getAkka();
    }

    @Bean
    public CoreProperties.Cluster clusterProperties() {
        return consumptionProperties.getCluster();
    }

}
