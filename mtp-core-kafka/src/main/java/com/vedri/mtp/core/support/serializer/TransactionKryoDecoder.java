package com.vedri.mtp.core.support.serializer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.esotericsoftware.kryo.Kryo;
import com.vedri.mtp.core.support.kyro.KryoDecoder;
import com.vedri.mtp.core.support.kyro.KryoThreadLocal;
import com.vedri.mtp.core.support.kyro.ThreadLocalKryoDecoder;
import com.vedri.mtp.core.transaction.Transaction;

@Component
public class TransactionKryoDecoder extends ThreadLocalKryoDecoder<Transaction> {

	@Component
	static class InternalTransactionKryoDecoder extends KryoDecoder<Transaction> {

		@Autowired
		public InternalTransactionKryoDecoder(Kryo kryo) {
			super(kryo);
		}
	};

	@Autowired
	public TransactionKryoDecoder(KryoThreadLocal kryoThreadLocal,
			InternalTransactionKryoDecoder internalTransactionKryoDecoder) {
		super(kryoThreadLocal, internalTransactionKryoDecoder);
	}
}
