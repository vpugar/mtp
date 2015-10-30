package com.vedri.mtp.consumption.support.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class KafkaMessageEnvelope<K, V> {
	private String topic;
	private K key;
	private V message;
}
