package com.vedri.mtp.core.transaction.aggregation;

import java.io.Serializable;

import com.vedri.mtp.core.support.cassandra.ColumnUtils;
import lombok.*;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class TransactionAggregationByStatus extends TimeAggregation implements Serializable {

	private TransactionValidationStatus validationStatus;
	private long transactionCount;

	public TransactionAggregationByStatus(TransactionValidationStatus validationStatus,
										  int year, int month, int day, int hour,
										  long transactionCount) {
		super(new YearToHourTime(year, month, day, hour));
		this.validationStatus = validationStatus;
		this.transactionCount = transactionCount;
	}

	public enum Fields {
		validationStatus, transactionCount;

		public final ColumnUtils.Field<Fields> F = ColumnUtils.createField(this);
	}
}
