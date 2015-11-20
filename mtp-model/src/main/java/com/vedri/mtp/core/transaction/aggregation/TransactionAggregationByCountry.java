package com.vedri.mtp.core.transaction.aggregation;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import com.vedri.mtp.core.MtpConstants;
import com.vedri.mtp.core.support.cassandra.ColumnUtils;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class TransactionAggregationByCountry extends TimeAggregation implements Serializable {

	private String originatingCountry;
	private long transactionCount;
	private BigDecimal amountPoints;

	public TransactionAggregationByCountry(String originatingCountry,
			int year, int month, int day, int hour, long transactionCount, BigDecimal amountPoints) {
		this(originatingCountry, new YearToHourTime(year, month, day, hour), transactionCount, amountPoints);
	}

	public TransactionAggregationByCountry(String originatingCountry,
			int year, int month, int day, int hour, long transactionCount, long amountPointsUnscaled) {
		super(new YearToHourTime(year, month, day, hour));
		this.originatingCountry = originatingCountry;
		this.transactionCount = transactionCount;
		setAmountPointsUnscaled(amountPointsUnscaled);
	}

	public TransactionAggregationByCountry(String originatingCountry,
			YearToHourTime yearToHourTime, long transactionCount, BigDecimal amountPoints) {
		super(yearToHourTime);
		this.originatingCountry = originatingCountry;
		this.transactionCount = transactionCount;
		this.amountPoints = amountPoints;
	}

	public TransactionAggregationByCountry(String originatingCountry,
			YearToHourTime yearToHourTime, long transactionCount, long amountPointsUnscaled) {
		super(yearToHourTime);
		this.originatingCountry = originatingCountry;
		this.transactionCount = transactionCount;
		setAmountPointsUnscaled(amountPointsUnscaled);
	}

	public enum Fields {
		originatingCountry, transactionCount, amountPointsUnscaled;

		public final ColumnUtils.Field<Fields> F = ColumnUtils.createField(this);
	}

	public long getAmountPointsUnscaled() {
		return amountPoints.unscaledValue().longValue();
	}

	public void setAmountPointsUnscaled(long value) {
		this.amountPoints = new BigDecimal(BigInteger.valueOf(value), MtpConstants.CURRENCY_POINTS_SCALE);
	}
}
