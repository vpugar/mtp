package com.vedri.mtp.core.transaction.aggregation;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vedri.mtp.core.MtpConstants;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import com.vedri.mtp.core.support.cassandra.ColumnUtils;

@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
public class TransactionAggregationByUser extends TimeAggregation implements Serializable {

	private String userId;
	private long transactionCount;
	private BigDecimal amountPoints;

	public TransactionAggregationByUser(String userId, int year, int month, int day, int hour,
										long transactionCount, BigDecimal amountPoints) {
		super(new YearToHourTime(year, month, day, hour));
		this.userId = userId;
		this.transactionCount = transactionCount;
		this.amountPoints = amountPoints;
	}

	public TransactionAggregationByUser(String userId, int year, int month, int day, int hour,
										long transactionCount, long amountPointsUnscaled) {
		super(new YearToHourTime(year, month, day, hour));
		this.userId = userId;
		this.transactionCount = transactionCount;
		this.setAmountPointsUnscaled(amountPointsUnscaled);
	}

	public enum Fields {
		userId, transactionCount, amountPointsUnscaled;

		public final ColumnUtils.Field<Fields> F = ColumnUtils.createField(this);
	}

	@JsonIgnore
	public long getAmountPointsUnscaled() {
		return amountPoints.unscaledValue().longValue();
	}

	public void setAmountPointsUnscaled(long value) {
		this.amountPoints = new BigDecimal(BigInteger.valueOf(value), MtpConstants.CURRENCY_POINTS_SCALE);
	}
}
