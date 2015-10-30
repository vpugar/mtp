package com.vedri.mtp.processor.support.kafka;

import java.io.File;
import java.io.IOException;
import java.util.*;

import kafka.admin.AdminUtils;
import kafka.api.PartitionStateInfo;
import kafka.server.KafkaConfig;
import kafka.server.KafkaServer;
import kafka.utils.SystemTime$;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.SystemUtils;

import scala.collection.JavaConversions;
import scala.collection.Seq;

import com.datastax.spark.connector.embedded.EmbeddedZookeeper;
import com.datastax.spark.connector.embedded.package$;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import scala.runtime.AbstractFunction1;

/**
 * Custom implementation, original EmbeddedKafka is not possible currently to configure.
 */
@Slf4j
public class EmbeddedKafka {

	private final Set<String> shutdownDeletePaths = new HashSet<>();

	private final KafkaServer server;
	private final EmbeddedZookeeper zookeeper;

	public EmbeddedKafka(Map<String, String> customProps) throws Exception {
		super();
		Map<String, String> defaultProps = Maps.newHashMap();
		defaultProps.put("broker.id", "0");
		defaultProps.put("host.name", "127.0.0.1");
		defaultProps.put("port", "9092");
		defaultProps.put("advertised.host.name", "127.0.0.1");
		defaultProps.put("advertised.port", "9092");
		defaultProps.put("log.dir", createTempDir().getAbsolutePath());
		defaultProps.put("zookeeper.connect", package$.MODULE$.ZookeeperConnectionString());
		defaultProps.put("replica.high.watermark.checkpoint.interval.ms", "5000");
		defaultProps.put("log.flush.interval.messages", "1");
		defaultProps.put("replica.socket.timeout.ms", "500");
		defaultProps.put("controlled.shutdown.enable", "false");
		defaultProps.put("auto.leader.rebalance.enable", "false");

		Properties props = new Properties();
		props.putAll(defaultProps);
		props.putAll(customProps);

		final KafkaConfig kafkaConfig = new KafkaConfig(props);

		zookeeper = new EmbeddedZookeeper((String) props.get("zookeeper.connect"));
		awaitCond(aVoid -> zookeeper.isRunning(), 3000, 100);

		server = new KafkaServer(kafkaConfig, SystemTime$.MODULE$);
		Thread.sleep(2000);

		log.info("Starting the Kafka server at {}", kafkaConfig.zkConnect());
		server.startup();
		Thread.sleep(2000);
	}

	public void createTopic(String topic, int numPartitions, int replicationFactor, Properties topicConfig) {
		if (!AdminUtils.topicExists(server.zkClient(), topic)) {
			AdminUtils.createTopic(server.zkClient(), topic, numPartitions, replicationFactor, topicConfig);
			awaitPropagation(topic, 0, 2000l);
		}
	}

	public void shutdown() throws Exception {
		log.info("Shutting down Kafka server");
		// https://issues.apache.org/jira/browse/KAFKA-1887
		server.kafkaController().shutdown();
		server.shutdown();
		server.awaitShutdown();
		final Seq<String> logDirs = server.config().logDirs();
		for (String f : JavaConversions.asJavaCollection(logDirs)) {
			try {
				deleteRecursively(new File(f));
			}
			catch (IOException e) {
				log.warn("Cannot delete file: " + f, e.getMessage());
			}
		}
		;
		zookeeper.shutdown();
		awaitCond(aVoid -> !zookeeper.isRunning(), 2000, 100);
		log.info("ZooKeeper server shut down.");
		Thread.sleep(2000);
	}

	private void awaitPropagation(String topic, int partition, long timeout) {
		awaitCond(aVoid -> server.apis().metadataCache().getPartitionInfo(topic, partition)
				.exists(new AbstractFunction1<PartitionStateInfo, Object>() {
					@Override
					public Object apply(PartitionStateInfo info) {
						return info.leaderIsrAndControllerEpoch().leaderAndIsr().leader() >= 0;
					}
				}),
				timeout, 100);
	}

	private File createTempDir() throws IOException {
		File dir = mkdir(new File(Files.createTempDir(), "spark-tmp-" + UUID.randomUUID().toString()));
		registerShutdownDeleteDir(dir);

		Runtime.getRuntime().addShutdownHook(new Thread("delete Spark temp dir " + dir) {
			public void run() {
				if (!hasRootAsShutdownDeleteDir(dir)) {
					try {
						deleteRecursively(dir);
					}
					catch (IOException e) {
						log.warn("Cannot delete dir: " + dir, e.getMessage());
					}
				}
			}
		});
		return dir;
	}

	private File mkdir(File dir) throws IOException {
		if (!dir.mkdir()) {
			throw new IOException("Could not create dir " + dir);
		}
		return dir;
	}

	private void registerShutdownDeleteDir(File file) {
		synchronized (shutdownDeletePaths) {
			shutdownDeletePaths.add(file.getAbsolutePath());
		}
	}

	private Boolean hasRootAsShutdownDeleteDir(File file) {
		String absolutePath = file.getAbsolutePath();
		synchronized (shutdownDeletePaths) {
			return shutdownDeletePaths.stream()
					.anyMatch(path -> !absolutePath.equals(path) && absolutePath.startsWith(path));
		}
	}

	private void deleteRecursively(File file) throws IOException {
		if (file != null) {
			if (file.isDirectory() && !isSymlink(file)) {
				final Collection<File> files = listFilesSafely(file);
				for (File child : files) {
					deleteRecursively(child);
				}
			}
			if (!file.delete()) {
				if (file.exists()) {
					throw new IOException("Failed to delete: " + file.getAbsolutePath());
				}
			}
		}
	}

	private Boolean isSymlink(File file) throws IOException {
		if (file == null) {
			throw new NullPointerException("File must not be null");
		}
		if (SystemUtils.IS_OS_WINDOWS) {
			return false;
		}
		File fcd;
		if (file.getParent() == null)
			fcd = file;
		else
			fcd = new File(file.getParentFile().getCanonicalFile(), file.getName());
		if (fcd.getCanonicalFile().equals(fcd.getAbsoluteFile())) {
			return false;
		}
		else {
			return true;
		}
	}

	private Collection<File> listFilesSafely(File file) throws IOException {
		File[] files = file.listFiles();
		if (files == null) {
			throw new IOException("Failed to list files for dir: " + file);
		}
		return Arrays.asList(files);
	}

	private void awaitCond(java.util.function.Predicate<Void> predicate, long duration, long steps) {
		if (!predicate.test(null)) {
			final long currentTimeMillis = System.currentTimeMillis();
			while (!predicate.test(null) && System.currentTimeMillis() - currentTimeMillis > duration) {
				try {
					Thread.sleep(steps);
				}
				catch (InterruptedException e) {
					// ignore
				}
			}
		}
	}

}
