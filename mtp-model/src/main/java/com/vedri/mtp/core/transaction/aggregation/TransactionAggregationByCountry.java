package com.vedri.mtp.core.transaction.aggregation;

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
public class TransactionAggregationByCountry extends TimeAggregation implements Serializable {

	private String originatingCountry;
	private long transactionCount;

	public TransactionAggregationByCountry(String originatingCountry,
			int year, int month, int day, int hour,
			long transactionCount) {
		this(originatingCountry, new YearToHourTime(year, month, day, hour), transactionCount);
	}

	public TransactionAggregationByCountry(String originatingCountry,
			YearToHourTime yearToHourTime,
			long transactionCount) {
		super(yearToHourTime);
		this.originatingCountry = originatingCountry;
		this.transactionCount = transactionCount;
	}

	public enum Fields {
		originatingCountry, transactionCount;

		public final ColumnUtils.Field<Fields> F = ColumnUtils.createField(this);
	}
}
