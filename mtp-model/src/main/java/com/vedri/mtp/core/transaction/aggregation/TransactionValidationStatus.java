package com.vedri.mtp.core.transaction.aggregation;

public enum TransactionValidationStatus {
    OK, InvalidAmount, InvalidRate, InvalidCountry, InvalidFromCurrency, InvalidToCurrency,
}
