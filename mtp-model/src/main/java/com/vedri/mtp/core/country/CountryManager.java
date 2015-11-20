package com.vedri.mtp.core.country;

import java.util.List;
import java.util.Set;

public interface CountryManager {

	Country getCountryFromCca2(String cca2);

	Set<Country> getCountriesFromCurrency(String currency);

	List<Country> getCountries();
}
