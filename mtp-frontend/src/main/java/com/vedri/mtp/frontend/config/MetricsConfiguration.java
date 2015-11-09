package com.vedri.mtp.frontend.config;

import java.lang.management.ManagementFactory;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.codahale.metrics.jvm.*;
import com.ryantenney.metrics.spring.config.annotation.EnableMetrics;
import com.ryantenney.metrics.spring.config.annotation.MetricsConfigurerAdapter;
import com.vedri.mtp.frontend.FrontendProperties;
import fr.ippon.spark.metrics.SparkReporter;

@Slf4j
@Configuration
@EnableMetrics(proxyTargetClass = true)
public class MetricsConfiguration extends MetricsConfigurerAdapter {

	private static final String PROP_METRIC_REG_JVM_MEMORY = "jvm.memory";
	private static final String PROP_METRIC_REG_JVM_GARBAGE = "jvm.garbage";
	private static final String PROP_METRIC_REG_JVM_THREADS = "jvm.threads";
	private static final String PROP_METRIC_REG_JVM_FILES = "jvm.files";
	private static final String PROP_METRIC_REG_JVM_BUFFERS = "jvm.buffers";

	private MetricRegistry metricRegistry = new MetricRegistry();

	private HealthCheckRegistry healthCheckRegistry = new HealthCheckRegistry();

	@Autowired
	private FrontendProperties frontendProperties;

	@Override
	@Bean
	public MetricRegistry getMetricRegistry() {
		return metricRegistry;
	}

	@Override
	@Bean
	public HealthCheckRegistry getHealthCheckRegistry() {
		return healthCheckRegistry;
	}

	@PostConstruct
	public void init() {
		log.debug("Registering JVM gauges");
		metricRegistry.register(PROP_METRIC_REG_JVM_MEMORY, new MemoryUsageGaugeSet());
		metricRegistry.register(PROP_METRIC_REG_JVM_GARBAGE, new GarbageCollectorMetricSet());
		metricRegistry.register(PROP_METRIC_REG_JVM_THREADS, new ThreadStatesGaugeSet());
		metricRegistry.register(PROP_METRIC_REG_JVM_FILES, new FileDescriptorRatioGauge());
		// FIXME
//		metricRegistry.register(PROP_METRIC_REG_JVM_BUFFERS,
//				new BufferPoolMetricSet(ManagementFactory.getPlatformMBeanServer()));
		if (frontendProperties.getMetrics().getJmx().isEnabled()) {
			log.debug("Initializing Metrics JMX reporting");
			JmxReporter jmxReporter = JmxReporter.forRegistry(metricRegistry).build();
			jmxReporter.start();
		}
	}

	@Slf4j
	@Configuration
	@ConditionalOnClass(Graphite.class)
	public static class GraphiteRegistry {

		@Autowired
		private MetricRegistry metricRegistry;

		@Autowired
		private FrontendProperties frontendProperties;

		@PostConstruct
		private void init() {
			if (frontendProperties.getMetrics().getGraphite().isEnabled()) {
				log.info("Initializing Metrics Graphite reporting");
				String graphiteHost = frontendProperties.getMetrics().getGraphite().getHost();
				Integer graphitePort = frontendProperties.getMetrics().getGraphite().getPort();
				String graphitePrefix = frontendProperties.getMetrics().getGraphite().getPrefix();
				Graphite graphite = new Graphite(new InetSocketAddress(graphiteHost, graphitePort));
				GraphiteReporter graphiteReporter = GraphiteReporter.forRegistry(metricRegistry)
						.convertRatesTo(TimeUnit.SECONDS)
						.convertDurationsTo(TimeUnit.MILLISECONDS)
						.prefixedWith(graphitePrefix)
						.build(graphite);
				graphiteReporter.start(1, TimeUnit.MINUTES);
			}
		}
	}

	@Slf4j
	@Configuration
	@ConditionalOnClass(SparkReporter.class)
	public static class SparkRegistry {

		@Autowired
		private MetricRegistry metricRegistry;

		@Autowired
		private FrontendProperties frontendProperties;

		@PostConstruct
		private void init() {
			if (frontendProperties.getMetrics().getSpark().isEnabled()) {
				log.info("Initializing Metrics Spark reporting");
				String sparkHost = frontendProperties.getMetrics().getSpark().getHost();
				Integer sparkPort = frontendProperties.getMetrics().getSpark().getPort();
				SparkReporter sparkReporter = SparkReporter.forRegistry(metricRegistry)
						.convertRatesTo(TimeUnit.SECONDS)
						.convertDurationsTo(TimeUnit.MILLISECONDS)
						.build(sparkHost, sparkPort);
				sparkReporter.start(1, TimeUnit.MINUTES);
			}
		}
	}
}
