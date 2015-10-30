package com.vedri.mtp.processor.transaction;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import com.vedri.mtp.core.support.cassandra.ColumnUtils;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class TransactionAggregationByCurrency extends TimeAggregation implements Serializable {

	private String currency;
	private long transactionCountFrom;
	private long transactionCountTo;

	public TransactionAggregationByCurrency(String currency, int year, int month, int day, int hour,
			long transactionCountFrom, long transactionCountTo) {
		super(year, month, day, hour);
		this.currency = currency;
		this.transactionCountFrom = transactionCountFrom;
		this.transactionCountTo = transactionCountTo;
	}

	public enum Fields {
		currency, transactionCountFrom, transactionCountTo;

		public final ColumnUtils.Field<Fields> F = ColumnUtils.createField(this);
	}
}
