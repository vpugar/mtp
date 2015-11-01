package com.vedri.mtp.core.rate.cf;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.*;

import org.joda.time.LocalDate;

import com.vedri.mtp.core.rate.Rate;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class CfRate {

	private String currencyFrom;
	private String currencyTo;
	private String cfTransferFee;
	private String bankTransferFee;
	private String youExchangeCf;
	private String youExchangeBank;
	private String youSave;
	private String cfRate;
	private String bankRate;
	private String cfBuyAmount;
	private String bankBuyAmount;

	public Rate toRate() {
		final Rate rate = new Rate();
		rate.setKey(new Rate.Key(currencyFrom, currencyTo, new LocalDate()));
		rate.setCfRate(new BigDecimal(cfRate));
		rate.setBankRate(new BigDecimal(bankRate));

		return rate;
	}
}
