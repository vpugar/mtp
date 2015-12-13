package com.vedri.mtp.processor.transaction;

import java.math.BigDecimal;
import java.util.Set;

import com.vedri.mtp.core.MtpConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vedri.mtp.core.country.Country;
import com.vedri.mtp.core.country.CountryManager;
import com.vedri.mtp.core.transaction.Transaction;
import com.vedri.mtp.core.transaction.aggregation.TransactionValidationStatus;

@Component
@Slf4j
public class TransactionValidator {

	private final CountryManager countryManager;

	@Autowired
	public TransactionValidator(CountryManager countryManager) {
		this.countryManager = countryManager;
	}

	public TransactionValidationStatus validate(Transaction transaction) {

		if (!checkNegativeAmount(transaction)) {
			log.warn("Invalid amount for {}", transaction.getTransactionId());
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
		final BigDecimal amountBuy2 = transaction.getAmountSell().multiply(transaction.getRate());
		final BigDecimal scaledValue = amountBuy2.setScale(MtpConstants.CURRENCY_SCALE, MtpConstants.CURRENCY_ROUNDING);
		final boolean equals = scaledValue.equals(transaction.getAmountBuy());
		if (!equals) {
			log.warn("Incorrect rate for {} - amountBuy: {}, calculatedValue: {}",
					transaction.getTransactionId(), transaction.getAmountBuy(), scaledValue);
		}
		return equals;
	}

	private TransactionValidationStatus checkOriginatingCountry(Transaction transaction) {
		final String originatingCountry = transaction.getOriginatingCountry();
		final Country country = countryManager.getCountryFromCca2(originatingCountry);

		if (country == null) {
			log.warn("Invalid country for {}", transaction.getTransactionId());
			return TransactionValidationStatus.InvalidCountry;
		}
		else {
			final Set<Country> fromCurrency = countryManager.getCountriesFromCurrency(transaction.getCurrencyFrom());

			if (fromCurrency.size() == 0) {
				log.warn("Invalid from currency for {}", transaction.getTransactionId());
				return TransactionValidationStatus.InvalidFromCurrency;
			}
			// TODO check if this is valid check
//			if (!fromCurrency.contains(country)) {
//				return TransactionValidationStatus.InvalidFromCurrency;
//			}

			final Set<Country> toCurrency = countryManager.getCountriesFromCurrency(transaction.getCurrencyTo());

			if (toCurrency.size() == 0) {
				log.warn("Invalid to currency for {}", transaction.getTransactionId());
				return TransactionValidationStatus.InvalidToCurrency;
			}
		}
		return TransactionValidationStatus.OK;
	}

}
