package com.vedri.mtp.processor.transaction;

import java.io.Serializable;

import com.vedri.mtp.core.support.cassandra.ColumnUtils;
import lombok.*;

import com.google.common.base.CaseFormat;
import com.vedri.mtp.core.transaction.TransactionValidationStatus;

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
		super(year, month, day, hour);
		this.validationStatus = validationStatus;
		this.transactionCount = transactionCount;
	}

	public enum Fields {
		validationStatus, transactionCount;

		public final ColumnUtils.Field<Fields> F = ColumnUtils.createField(this);
	}
}
