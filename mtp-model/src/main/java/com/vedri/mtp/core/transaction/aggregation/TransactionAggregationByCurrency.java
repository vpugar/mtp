package com.vedri.mtp.core.transaction.aggregation;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import com.vedri.mtp.core.MtpConstants;
import com.vedri.mtp.core.support.cassandra.ColumnUtils;

@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
public class TransactionAggregationByCurrency extends TimeAggregation implements Serializable {

	private String currency;
	private long transactionCountFrom;
	private BigDecimal amountFrom;
	private long transactionCountTo;
	private BigDecimal amountTo;

	public TransactionAggregationByCurrency(String currency, Integer year, Integer month, Integer day, Integer hour,
			long transactionCountFrom, long transactionCountTo, BigDecimal amountFrom, BigDecimal amountTo) {
		super(new YearToHourTime(year, month, day, hour));
		this.currency = currency;
		this.transactionCountFrom = transactionCountFrom;
		this.transactionCountTo = transactionCountTo;
		this.amountFrom = amountFrom;
		this.amountTo = amountTo;
	}

	public TransactionAggregationByCurrency(String currency, Integer year, Integer month, Integer day, Integer hour,
			long transactionCountFrom, long transactionCountTo, long amountFromUnscaled, long amountToUnscaled) {
		super(new YearToHourTime(year, month, day, hour));
		this.currency = currency;
		this.transactionCountFrom = transactionCountFrom;
		this.transactionCountTo = transactionCountTo;
		this.setAmountFromUnscaled(amountFromUnscaled);
		this.setAmountToUnscaled(amountToUnscaled);
	}

	public enum Fields {
		currency, transactionCountFrom, amountFromUnscaled, transactionCountTo, amountToUnscaled;

		public final ColumnUtils.Field<Fields> F = ColumnUtils.createField(this);
	}

	@JsonIgnore
	public long getAmountFromUnscaled() {
		return amountFrom.unscaledValue().longValue();
	}

	public void setAmountFromUnscaled(long value) {
		this.amountFrom = new BigDecimal(BigInteger.valueOf(value), MtpConstants.CURRENCY_POINTS_SCALE);
	}

	@JsonIgnore
	public long getAmountToUnscaled() {
		return amountTo.unscaledValue().longValue();
	}

	public void setAmountToUnscaled(long value) {
		this.amountTo = new BigDecimal(BigInteger.valueOf(value), MtpConstants.CURRENCY_POINTS_SCALE);
	}
}
