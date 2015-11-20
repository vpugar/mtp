package com.vedri.mtp.core.currency;

import java.util.List;

public interface CurrencyManager {

	List<Currency> getCurrencies();

	Currency getCurrencyFromCode(String code);
}
