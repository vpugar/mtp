package com.vedri.mtp.processor.streaming.handler;

import kafka.serializer.Decoder;

import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaPairInputDStream;

import com.vedri.mtp.core.transaction.Transaction;

public class CreateTransactionBuilder
		extends StreamBuilder<JavaPairInputDStream<String, byte[]>, JavaPairDStream<String, Transaction>> {

	private static Decoder<Transaction> decoder;

	public CreateTransactionBuilder(StreamBuilder<?, JavaPairInputDStream<String, byte[]>> prevBuilder,
			final Decoder<Transaction> decoder) {
		super(prevBuilder);
		this.decoder = decoder;
	}

	@Override
	protected JavaPairDStream<String, Transaction> doBuild(JavaPairInputDStream<String, byte[]> stream) {

		// create transaction
		return stream.mapValues(data -> decoder.fromBytes(data));
	}

}
