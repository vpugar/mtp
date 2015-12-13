package com.vedri.mtp.core.country.dao;

import static com.vedri.mtp.core.country.Country.Fields.*;

import java.util.List;
import java.util.stream.Collectors;

import com.vedri.mtp.core.currency.Currency;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.datastax.driver.core.*;
import com.vedri.mtp.core.country.Country;

@Repository
@Slf4j
public class CassandraCountryDao implements CountryDao {

	private static final String LOAD_ALL_STATEMENT = "SELECT cca2, cca3, common_name, official_name, currencies " +
			"FROM country";

	private final Session session;

	private PreparedStatement loadAllStatement;

	@Autowired
	public CassandraCountryDao(Session session) {
		this.session = session;
		initStatements();
	}

	@Override
	public List<Country> loadAll() {

		if (log.isDebugEnabled()) {
			log.debug("Loading countries");
		}

		final BoundStatement bs = loadAllStatement.bind();

		final ResultSet resultSet = session.execute(bs);
		final List<Row> rows = resultSet.all();

		return rows
				.stream()
				.map(this::mapRowToCountry)
				.collect(Collectors.toList());
	}

	private void initStatements() {
		loadAllStatement = session.prepare(LOAD_ALL_STATEMENT);
	}

	private Country mapRowToCountry(final Row row) {
		final Country country = new Country(row.getString(cca2.F.underscore()), row.getString(cca3.F.underscore()),
				row.getString(commonName.F.underscore()), row.getString(officialName.F.underscore()));
		country.setupCurrencies(row.getSet(currencies.F.underscore(), String.class));
		return country;
	}
}
