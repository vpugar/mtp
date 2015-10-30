package com.vedri.mtp.processor;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@ComponentScan(basePackages = {
		"com.vedri.mtp.processor.country",
		"com.vedri.mtp.processor.streaming",
		"com.vedri.mtp.processor.transaction"
})
@Configuration
public class ProcessorConfig {
}
