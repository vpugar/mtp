package com.vedri.mtp.core.transaction.serialize;

import java.io.IOException;

import com.vedri.mtp.core.transaction.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class TransactionJacksonDecoder implements kafka.serializer.Decoder<Transaction> {

	private final ObjectMapper objectMapper;

	@Autowired
	public TransactionJacksonDecoder(@Qualifier("objectMapper") ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@Override
	public Transaction fromBytes(byte[] bytes) {
		try {
			return objectMapper.readValue(bytes, Transaction.class);
		}
		catch (IOException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}
}
