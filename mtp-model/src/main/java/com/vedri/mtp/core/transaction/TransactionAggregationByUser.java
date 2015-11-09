package com.vedri.mtp.core.transaction;

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
public class TransactionAggregationByUser extends TimeAggregation implements Serializable {

	private String userId;
	private long transactionCount;

	public TransactionAggregationByUser(String userId, int year, int month, int day, int hour, long transactionCount) {
		super(year, month, day, hour);
		this.userId = userId;
		this.transactionCount = transactionCount;
	}

	public enum Fields {
		userId, transactionCount;

		public final ColumnUtils.Field<Fields> F = ColumnUtils.createField(this);
	}
}
