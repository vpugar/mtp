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
		rate.setKey(new Rate.Key(currencyFrom, currencyTo, LocalDate.now()));
		rate.setCfRate(new BigDecimal(cfRate));
		rate.setBankRate(new BigDecimal(bankRate));

		return rate;
	}

	public static CfRate fromRate(Rate rate) {
		final CfRate cfRate = new CfRate();
		cfRate.setCurrencyFrom(rate.getKey().getCurrencyFrom());
		cfRate.setCurrencyTo(rate.getKey().getCurrencyTo());
		cfRate.setCfRate(rate.getCfRate().toString());
		cfRate.setBankRate(rate.getBankRate().toString());
		return cfRate;
	}
}
