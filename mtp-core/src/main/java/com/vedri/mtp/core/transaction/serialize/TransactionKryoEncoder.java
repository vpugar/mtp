package com.vedri.mtp.core.transaction.serialize;

import com.vedri.mtp.core.transaction.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.esotericsoftware.kryo.Kryo;
import com.vedri.mtp.core.support.kyro.KryoEncoder;

@Component
public class TransactionKryoEncoder extends KryoEncoder<Transaction> {

	@Autowired
	public TransactionKryoEncoder(Kryo kyro) {
		super(kyro);
	}
}
