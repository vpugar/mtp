package com.vedri.mtp.processor.streaming.handler;

import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaPairInputDStream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vedri.mtp.core.transaction.Transaction;

public class CreateTransactionBuilder
		extends StreamBuilder<JavaPairInputDStream<String, String>, JavaPairDStream<String, Transaction>> {

	private static ObjectMapper objectMapper;

	public CreateTransactionBuilder(StreamBuilder<?, JavaPairInputDStream<String, String>> prevBuilder,
									ObjectMapper objectMapper) {
		super(prevBuilder);
		this.objectMapper = objectMapper;
	}

	@Override
	protected JavaPairDStream<String, Transaction> doBuild(JavaPairInputDStream<String, String> stream) {

		// create transaction
		return stream.mapValues(json -> objectMapper.readValue(json, Transaction.class));
	}

}
