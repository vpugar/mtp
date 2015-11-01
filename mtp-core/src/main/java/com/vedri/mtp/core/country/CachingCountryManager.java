package com.vedri.mtp.core.country;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import scala.Tuple2;

import com.vedri.mtp.core.country.dao.CountryDao;

@Service
@Slf4j
public class CachingCountryManager implements CountryManager {

	private final CountryDao countryDao;

	private volatile List<Country> countries = Collections.emptyList();
	private volatile List<String> currencies = Collections.emptyList();
	private volatile Map<String, Country> cca2ToCountry = Collections.emptyMap();
	private volatile Map<String, Set<Country>> currencyToCountry = Collections.emptyMap();

	@Autowired
	public CachingCountryManager(CountryDao countryDao) {
		this.countryDao = countryDao;
	}

	@PostConstruct
	public void init() {

		log.debug("Loading countries and currencies");

		countries = countryDao.loadAll();
		cca2ToCountry = countries
				.stream()
				.collect(Collectors.<Country, String, Country> toMap(Country::getCca2, country -> country));
		final Stream<Tuple2<String, Country>> currencyAndCountryStream = countries
				.stream()
				.flatMap(country -> country.getCurrencies().stream().map(currency -> new Tuple2<>(currency, country)));
		currencyToCountry = currencyAndCountryStream
				.collect(/* group by currency */ Collectors.groupingBy(Tuple2::_1,
						/* map collector: set of tuples to set of countries */
						Collectors.mapping(Tuple2::_2, Collectors.toSet())));
		currencies = countries
				.stream()
				.flatMap(country -> country.getCurrencies().stream())
				.distinct()
				.collect(Collectors.toList());

		log.info("Loaded {} countries and {} currencies", countries.size(), currencies.size());
	}

	public Country getCountryFromCca2(String cca2) {
		return cca2ToCountry.get(cca2);
	}

	public Set<Country> getCountriesFromCurrency(String country) {
		return currencyToCountry.get(country);
	}

	@Override
	public List<Country> getCountries() {
		return countries;
	}

	@Override
	public List<String> getCurrencies() {
		return currencies;
	}
}
