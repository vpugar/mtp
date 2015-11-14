package com.vedri.mtp.processor.transaction;

import java.math.BigDecimal;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vedri.mtp.core.country.Country;
import com.vedri.mtp.core.country.CountryManager;
import com.vedri.mtp.core.transaction.Transaction;
import com.vedri.mtp.core.transaction.aggregation.TransactionValidationStatus;

@Component
public class TransactionValidator {

	private final CountryManager countryManager;

	@Autowired
	public TransactionValidator(CountryManager countryManager) {
		this.countryManager = countryManager;
	}

	public TransactionValidationStatus validate(Transaction transaction) {

		if (!checkNegativeAmount(transaction)) {
			return TransactionValidationStatus.InvalidAmount;
		}
		if (!checkAmountSanity(transaction)) {
			return TransactionValidationStatus.InvalidRate;
		}

		return checkOriginatingCountry(transaction);
	}

	private boolean checkNegativeAmount(Transaction transaction) {
		return transaction.getAmountBuy().compareTo(BigDecimal.ZERO) > 0
				&& transaction.getAmountSell().compareTo(BigDecimal.ZERO) > 0;
	}

	private boolean checkAmountSanity(Transaction transaction) {
		final double amountBuy = transaction.getAmountBuy().doubleValue();
		final double amountSell = transaction.getAmountSell().doubleValue();
		transaction.getAmountBuy().multiply(transaction.getRate());
		final double amountBuy2 = amountSell * transaction.getRate().doubleValue();
		return amountBuy2 + 0.00001 >= amountBuy && amountBuy2 - 0.00001 <= amountBuy;
	}

	private TransactionValidationStatus checkOriginatingCountry(Transaction transaction) {
		final String originatingCountry = transaction.getOriginatingCountry();
		final Country country = countryManager.getCountryFromCca2(originatingCountry);

		if (country == null) {
			return TransactionValidationStatus.InvalidCountry;
		}
		else {
			final Set<Country> fromCurrency = countryManager.getCountriesFromCurrency(transaction.getCurrencyFrom());

			if (fromCurrency == null) {
				return TransactionValidationStatus.InvalidFromCurrency;
			}
			// TODO check if this is valid check
//			if (!fromCurrency.contains(country)) {
//				return TransactionValidationStatus.InvalidFromCurrency;
//			}

			final Set<Country> toCurrency = countryManager.getCountriesFromCurrency(transaction.getCurrencyTo());

			if (toCurrency == null) {
				return TransactionValidationStatus.InvalidToCurrency;
			}
		}
		return TransactionValidationStatus.OK;
	}

}
