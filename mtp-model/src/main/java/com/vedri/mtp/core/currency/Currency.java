package com.vedri.mtp.core.currency;

import lombok.*;

import com.google.common.base.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Currency {

	private String code;

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Currency currency = (Currency) o;
		return Objects.equal(code, currency.code);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(code);
	}
}
