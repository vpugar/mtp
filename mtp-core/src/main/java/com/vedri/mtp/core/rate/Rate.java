package com.vedri.mtp.core.rate;

import java.math.BigDecimal;

import lombok.*;

import com.google.common.base.Objects;
import com.vedri.mtp.core.support.cassandra.ColumnUtils;
import org.joda.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class Rate {

	@NoArgsConstructor
	@AllArgsConstructor
	@Setter
	@Getter
	@ToString
	public static class Key {
		private String currencyFrom;
		private String currencyTo;
		private LocalDate rateDate;

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;
			Key key = (Key) o;
			return Objects.equal(currencyFrom, key.currencyFrom) &&
					Objects.equal(currencyTo, key.currencyTo) &&
					Objects.equal(rateDate, key.rateDate);
		}

		@Override
		public int hashCode() {
			return Objects.hashCode(currencyFrom, currencyTo, rateDate);
		}
	}

	private Key key;
	private BigDecimal cfRate;
	private BigDecimal bankRate;

	public enum Fields {
		currencyFrom, currencyTo, rateDate, cfRate, bankRate;

		public final ColumnUtils.Field<Fields> F = ColumnUtils.createField(this);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Rate rate = (Rate) o;
		return Objects.equal(key, rate.key);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(key);
	}
}
