package com.vedri.mtp.core.transaction;

import java.io.Serializable;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.vedri.mtp.core.support.cassandra.ColumnUtils;
import lombok.*;

import org.joda.time.DateTime;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class Transaction implements Serializable {

	// storage extra
	private String partition;
	private String transactionId;

	// transaction data
	private String userId;
	private String currencyFrom;
	private String currencyTo;
	private BigDecimal amountSell;
	private BigDecimal amountBuy;
	private BigDecimal rate;
	@JsonProperty("timePlaced")
	private DateTime placedTime;
	private String originatingCountry;

	// extra info
	private DateTime receivedTime;
	private String nodeName;
	private TransactionValidationStatus validationStatus;

	public enum Fields {
		partition, transactionId, userId, currencyFrom, currencyTo, amountSell, amountBuy, rate, placedTime,
		originatingCountry, receivedTime, nodeName, validationStatus;

		public final ColumnUtils.Field<Fields> F = ColumnUtils.createField(this);
	}

	public Transaction(final String partition, final String transactionId) {
		this.partition = partition;
		this.transactionId = transactionId;
	}

	public Transaction(final String[] data) {

	}

	public Transaction(final String userId, final String currencyFrom, final String currencyTo,
			final BigDecimal amountSell, final BigDecimal amountBuy, final BigDecimal rate,
			final DateTime placedTime, final String originatingCountry) {
		this.userId = userId;
		this.currencyFrom = currencyFrom;
		this.currencyTo = currencyTo;
		this.amountSell = amountSell;
		this.amountBuy = amountBuy;
		this.rate = rate;
		this.placedTime = placedTime;
		this.originatingCountry = originatingCountry;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		Transaction that = (Transaction) o;

		return transactionId.equals(that.transactionId);
	}

	@Override
	public int hashCode() {
		return transactionId.hashCode();
	}

}
