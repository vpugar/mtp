package com.vedri.mtp.processor.country.dao;

import java.util.Set;

import com.vedri.mtp.processor.country.Country;

public interface CountryDao {

	Set<Country> loadAll();
}
