package com.vedri.mtp.processor.country.dao;

import static com.vedri.mtp.processor.country.Country.Fields.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.datastax.driver.core.*;
import com.vedri.mtp.processor.country.Country;

@Component
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
	public Set<Country> loadAll() {

		if (log.isDebugEnabled()) {
			log.debug("Loading countries");
		}

		final BoundStatement bs = loadAllStatement.bind();

		final ResultSet resultSet = session.execute(bs);
		final List<Row> rows = resultSet.all();

		return rows
				.stream()
				.map(this::mapRowToCountry)
				.collect(Collectors.toSet());
	}

	private void initStatements() {
		loadAllStatement = session.prepare(LOAD_ALL_STATEMENT);
	}

	private Country mapRowToCountry(final Row row) {
		return new Country(row.getString(cca2.F.underscore()), row.getString(cca3.F.underscore()),
				row.getString(commonName.F.underscore()), row.getString(officialName.F.underscore()),
				row.getSet(currencies.F.underscore(), String.class));
	}
}
