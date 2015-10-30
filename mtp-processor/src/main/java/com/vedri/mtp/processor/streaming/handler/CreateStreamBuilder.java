package com.vedri.mtp.processor.streaming.handler;

import java.util.Map;

import kafka.serializer.StringDecoder;

import org.apache.spark.storage.StorageLevel;
import org.apache.spark.streaming.api.java.JavaPairInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.KafkaUtils;

import com.google.common.collect.Maps;

public class CreateStreamBuilder extends StreamBuilder<NoIO, JavaPairInputDStream<String, String>> {

	private static JavaStreamingContext streamingContext;
	private String topicName;
	private Map<String, String> kafkaParams;

	public CreateStreamBuilder(String topicName,
			JavaStreamingContext streamingContext, Map<String, String> kafkaParams) {
		super(null);
		this.streamingContext = streamingContext;
		this.topicName = topicName;
		this.kafkaParams = kafkaParams;
	}

	@Override
	protected JavaPairInputDStream<String, String> doBuild(NoIO input) {

		final Map<String, Integer> topics = Maps.newHashMap();
		topics.put(topicName, 1);

		// String, String, StringDecoder, StringDecoder
		return KafkaUtils.createStream(streamingContext,
				String.class, String.class, StringDecoder.class, StringDecoder.class,
				kafkaParams, topics, StorageLevel.MEMORY_AND_DISK());
	}
}
