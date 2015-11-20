package com.vedri.mtp.core.currency;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vedri.mtp.core.country.CountryManager;

@Service
@Slf4j
public class CacheCurrencyManager implements CurrencyManager {

	private final CountryManager countryManager;
	private volatile List<Currency> currencies = Collections.emptyList();

	@Autowired
	public CacheCurrencyManager(CountryManager countryManager) {
		this.countryManager = countryManager;
	}

	@PostConstruct
	public void init() {

		log.debug("Loading currencies");
		currencies = countryManager.getCountries()
				.stream()
				.flatMap(country -> country.getCurrencies().stream())
				.distinct()
				.collect(Collectors.toList());

		log.info("Loaded {} currencies", currencies.size());
	}

	@Override
	public List<Currency> getCurrencies() {
		return currencies;
	}

	@Override
	public Currency getCurrencyFromCode(String code) {
		return new Currency(code);
	}
}
