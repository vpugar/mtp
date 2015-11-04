package com.vedri.mtp.core.transaction.serialize;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vedri.mtp.core.transaction.Transaction;

@Component
public class TransactionJacksonEncoder implements kafka.serializer.Encoder<Transaction> {

	private final ObjectMapper objectMapper;

	@Autowired
	public TransactionJacksonEncoder(@Qualifier("objectMapper") ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@Override
	public byte[] toBytes(Transaction transaction) {
		try {
			return objectMapper.writeValueAsBytes(transaction);
		}
		catch (JsonProcessingException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}
}
