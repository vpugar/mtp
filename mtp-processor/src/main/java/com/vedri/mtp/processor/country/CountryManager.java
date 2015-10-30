package com.vedri.mtp.processor.country;

import java.util.Set;

public interface CountryManager {

    Country getCountryFromCca2(String cca2);

    Set<Country> getCountriesFromCurrency(String country);

}
