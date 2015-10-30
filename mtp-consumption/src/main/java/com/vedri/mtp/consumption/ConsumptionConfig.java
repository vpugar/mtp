package com.vedri.mtp.consumption;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@ComponentScan(basePackages = {
		"com.vedri.mtp.consumption.http",
		"com.vedri.mtp.consumption.support.json",
		"com.vedri.mtp.consumption.support.kafka",
		"com.vedri.mtp.consumption.transaction"
})
@Configuration
public class ConsumptionConfig {
}
