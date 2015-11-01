package com.vedri.mtp.core.rate.dao;

import static com.vedri.mtp.core.rate.Rate.Fields.*;

import java.math.BigDecimal;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.datastax.driver.core.*;
import com.vedri.mtp.core.rate.Rate;

@Repository
@Slf4j
public class CassandraRateDao implements RateDao {

	private static final String INSERT_STATEMENT = "INSERT INTO rate (currency_from, currency_to, rate_date, cf_rate, bank_rate) "
			+ "VALUES (?,?,?,?,?)";
	private static final String LOAD_STATEMENT = "SELECT currency_from, currency_to, rate_date, cf_rate, bank_rate " +
			"FROM rate WHERE currency_from = ? AND currency_to = ? and rate_date = ?";

	private final Session session;

	private PreparedStatement insertStatement;
	private PreparedStatement loadStatement;

	@Autowired
	public CassandraRateDao(Session session) {
		this.session = session;
		initStatements();
	}

	private void initStatements() {
		insertStatement = session.prepare(INSERT_STATEMENT);
		loadStatement = session.prepare(LOAD_STATEMENT);
	}

	@Override
	public Rate save(final Rate rate) {

		if (log.isDebugEnabled()) {
			log.debug("Saving rate {}", rate);
		}

		final Rate.Key key = rate.getKey();
		final org.joda.time.LocalDate rateDate = key.getRateDate();

		final BoundStatement bs = insertStatement.bind();

		bs.setString(currencyFrom.F.underscore(), key.getCurrencyFrom());
		bs.setString(currencyTo.F.underscore(), key.getCurrencyTo());
		bs.setTimestamp(Rate.Fields.rateDate.F.underscore(), key.getRateDate().toDate());
		bs.setString(cfRate.F.underscore(), rate.getCfRate().toString());
		bs.setString(bankRate.F.underscore(), rate.getBankRate().toString());

		session.execute(bs);

		if (log.isInfoEnabled()) {
			log.info("Saved rate {}", rate);
		}

		return rate;
	}

	@Override
	public Rate load(final Rate.Key key) {

		if (log.isDebugEnabled()) {
			log.debug("Loading rate for currencyFrom: {}, currencyTo: {}", key.getCurrencyFrom(), key.getCurrencyTo());
		}

		final org.joda.time.LocalDate rateLocalDate = key.getRateDate();

		final BoundStatement bs = loadStatement.bind();
		bs.setString(currencyFrom.F.underscore(), key.getCurrencyFrom());
		bs.setString(currencyTo.F.underscore(), key.getCurrencyTo());
		bs.setTimestamp(rateDate.F.underscore(), rateLocalDate.toDate());

		final ResultSet resultSet = session.execute(bs);
		final Row row = resultSet.one();

		if (row == null) {
			return null;
		}

		return mapRowToRate(row);
	}

	private Rate mapRowToRate(final Row row) {
		return new Rate(
				new Rate.Key(row.getString(currencyFrom.F.underscore()), row.getString(currencyTo.F.underscore()),
						new org.joda.time.LocalDate(row.getDate(rateDate.F.underscore()).getMillisSinceEpoch())),
				new BigDecimal(row.getString(cfRate.F.underscore())),
				new BigDecimal(row.getString(bankRate.F.underscore())));
	}
}
