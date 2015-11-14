package com.vedri.mtp.core.transaction.dao;

import static com.vedri.mtp.core.transaction.Transaction.Fields.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.vedri.mtp.core.transaction.aggregation.TransactionValidationStatus;
import lombok.extern.slf4j.Slf4j;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.datastax.driver.core.*;
import com.vedri.mtp.core.support.cassandra.CassandraPartitionFetcher;
import com.vedri.mtp.core.support.cassandra.CassandraPartitionUtils;
import com.vedri.mtp.core.support.cassandra.CassandraUtils;
import com.vedri.mtp.core.transaction.Transaction;

@Repository
@Slf4j
public class CassandraTransactionDao implements TransactionDao {

	private static final int RAW_FETCH_SIZE = 1000;

	private static final String INSERT_STATEMENT = "INSERT INTO transaction (partition, transaction_id, user_id, " +
			"currency_from, currency_to, amount_sell, amount_buy, rate, placed_time, originating_country, " +
			"received_time, node_name) " +
			"VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
	private static final String LOAD_STATEMENT = "SELECT user_id, currency_from, currency_to, amount_sell, " +
			"amount_buy, rate, placed_time, originating_country, received_time, validation_status, node_name " +
			"FROM transaction " +
			"WHERE partition = ? AND transaction_id = ?";
	private static final String LOAD_ALL_STATEMENT = "SELECT partition, transaction_id, user_id, currency_from, currency_to, amount_sell, " +
			"amount_buy, rate, placed_time, originating_country, received_time, validation_status, node_name " +
			"FROM transaction " +
			"WHERE partition = ? " +
			"ORDER BY transaction_id ASC LIMIT " + RAW_FETCH_SIZE;
	private static final String LOAD_ALL_STATEMENT_BOUNDARY = "SELECT partition, transaction_id, user_id, currency_from, currency_to, amount_sell, "
			+
			"amount_buy, rate, placed_time, originating_country, received_time, validation_status, node_name " +
			"FROM transaction " +
			"WHERE partition = ? AND transaction_id > ? " +
			"ORDER BY transaction_id ASC LIMIT " + RAW_FETCH_SIZE;
	private static final String LOAD_ALL_STATEMENT_INVERT = "SELECT partition, transaction_id, user_id, currency_from, currency_to, amount_sell, "
			+
			"amount_buy, rate, placed_time, originating_country, received_time, validation_status, node_name " +
			"FROM transaction " +
			"WHERE partition = ? " +
			"ORDER BY transaction_id DESC LIMIT " + RAW_FETCH_SIZE;
	private static final String LOAD_ALL_STATEMENT_BOUNDARY_INVERT = "SELECT partition, transaction_id, user_id, currency_from, currency_to, amount_sell, "
			+
			"amount_buy, rate, placed_time, originating_country, received_time, validation_status, node_name " +
			"FROM transaction " +
			"WHERE partition = ? AND transaction_id < ? " +
			"ORDER BY transaction_id DESC LIMIT " + RAW_FETCH_SIZE;

	private final Session session;

	private PreparedStatement insertStatement;
	private PreparedStatement loadStatement;
	private PreparedStatement loadAllStatement;
	private PreparedStatement loadAllStatementBoundary;
	private PreparedStatement loadAllStatementInvert;
	private PreparedStatement loadAllStatementBoundaryInvert;

	@Autowired
	public CassandraTransactionDao(final Session session) {
		this.session = session;
		initStatements();
	}

	@Override
	public Transaction save(final Transaction transaction) {

		if (log.isDebugEnabled()) {
			log.debug("Saving transaction {}", transaction);
		}

		final BoundStatement bs = insertStatement.bind();

		final long currentTimeMillis = transaction.getReceivedTime().getMillis();
		final UUID timeUUID = CassandraUtils.createUUIDFromMillis(currentTimeMillis);
		final String partitionForMinute = CassandraPartitionUtils.getPartitionIdFromMillisForMinute(currentTimeMillis);
		transaction.setTransactionId(timeUUID.toString());
		transaction.setPartition(partitionForMinute);

		bs.setString(partition.F.underscore(), partitionForMinute);
		bs.setUUID(transactionId.F.underscore(), timeUUID);
		bs.setString(userId.F.underscore(), transaction.getUserId());
		bs.setString(currencyFrom.F.underscore(), transaction.getCurrencyFrom());
		bs.setString(currencyTo.F.underscore(), transaction.getCurrencyTo());
		bs.setString(amountSell.F.underscore(), transaction.getAmountSell().toString());
		bs.setString(amountBuy.F.underscore(), transaction.getAmountBuy().toString());
		bs.setString(rate.F.underscore(), transaction.getRate().toString());
		bs.setTimestamp(placedTime.F.underscore(), transaction.getPlacedTime().toDate());
		bs.setString(originatingCountry.F.underscore(), transaction.getOriginatingCountry());
		bs.setTimestamp(receivedTime.F.underscore(), transaction.getReceivedTime().toDate());
		bs.setString(nodeName.F.underscore(), transaction.getNodeName());

		session.execute(bs);

		if (log.isInfoEnabled()) {
			log.info("Saved transaction {}", transaction);
		}

		return transaction;
	}

	@Override
	public Transaction load(final String transactionId) {

		if (log.isDebugEnabled()) {
			log.debug("loading transaction for transactionId: {}", transactionId);
		}

		final UUID timeUUID = UUID.fromString(transactionId);

		return load(timeUUID);
	}

	@Override
	public Transaction load(final UUID timeUUID) {

		if (log.isDebugEnabled()) {
			log.debug("Loading transaction for transactionId: {}", timeUUID);
		}

		final String partitionForMinute = CassandraPartitionUtils.getPartitionIdFromTimeUUIDForMinute(timeUUID);

		final BoundStatement bs = loadStatement.bind();
		bs.bind(partitionForMinute, timeUUID);

		final ResultSet resultSet = session.execute(bs);
		final Row row = resultSet.one();

		if (row == null) {
			return null;
		}

		return mapRowToTransaction(partitionForMinute, timeUUID, row);
	}

	@Override
	public List<Transaction> loadAll(DateTime timeReceivedFrom, DateTime timeReceivedTo,
			String filterUserId, String filterCurrencyFrom, String filterCurrencyTo, String filterOriginatingCountry,
			int filterOffset, int filterPageSize) {
		log.debug("loadAll {}, {}, {}, {}, {}, {}", timeReceivedFrom, timeReceivedTo, userId, currencyFrom, currencyTo,
				originatingCountry);

		CassandraPartitionFetcher transactionRaw = new CassandraPartitionFetcher(
				session, transactionId.F.underscore(),
				loadAllStatement, loadAllStatementBoundary,
				loadAllStatementInvert, loadAllStatementBoundaryInvert,
				RAW_FETCH_SIZE, timeReceivedFrom, timeReceivedTo);

		List<Transaction> transactions = new ArrayList<>();

		int offset = 0;
		final DateTime start = transactionRaw.getStart();
		final DateTime end = transactionRaw.getEnd();

		List<Row> rawTransactions;

		boolean fetchRest = false;

		do {
			rawTransactions = transactionRaw.getNextPage();

			List<Transaction> filteredList = rawTransactions.stream()
					.filter(row -> filterUserId == null
							|| row.getString(userId.F.underscore()).equals(filterUserId))
					.filter(row -> filterCurrencyFrom == null
							|| row.getString(currencyFrom.F.underscore()).equals(filterCurrencyFrom))
					.filter(row -> filterCurrencyTo == null
							|| row.getString(currencyTo.F.underscore()).equals(filterCurrencyTo))
					.filter(row -> filterOriginatingCountry == null
							|| row.getString(originatingCountry.F.underscore()).equals(filterOriginatingCountry))
					.map(row -> mapRowToTransaction(
							row.getString(partition.F.underscore()), row.getUUID(transactionId.F.underscore()), row))
					.filter(transaction -> {
						final DateTime receivedTime = transaction.getReceivedTime();
						return receivedTime.isAfter(start) && receivedTime.isBefore(end);
					})
					.collect(Collectors.toList());

			if (filterOffset < offset + filteredList.size()) {
				if (!fetchRest) {
					int subListOffset = filterOffset - offset;
					transactions = filteredList.subList(
							subListOffset, Math.min(filteredList.size(), subListOffset + filterPageSize));

					if (transactions.size() < filterPageSize) {
						fetchRest = true;
					}
				}
				else {
					transactions.addAll(filteredList.subList(
							0,
							Math.min(filteredList.size(), filterPageSize - transactions.size())));
				}
			}
			else {
				offset += filteredList.size();
			}
		} while (offset <= filterOffset && transactions.size() < filterPageSize && rawTransactions.size() != 0);

		return transactions;
	}

	private void initStatements() {
		insertStatement = session.prepare(INSERT_STATEMENT);
		loadStatement = session.prepare(LOAD_STATEMENT);
		loadAllStatement = session.prepare(LOAD_ALL_STATEMENT);
		loadAllStatementBoundary = session.prepare(LOAD_ALL_STATEMENT_BOUNDARY);
		loadAllStatementInvert = session.prepare(LOAD_ALL_STATEMENT_INVERT);
		loadAllStatementBoundaryInvert = session.prepare(LOAD_ALL_STATEMENT_BOUNDARY_INVERT);
	}

	private Transaction mapRowToTransaction(final String partition, final UUID transactionId, final Row row) {
		Transaction transaction = new Transaction(partition, transactionId.toString());
		transaction.setUserId(row.getString(userId.F.underscore()));
		transaction.setCurrencyFrom(row.getString(currencyFrom.F.underscore()));
		transaction.setCurrencyTo(row.getString(currencyTo.F.underscore()));
		transaction.setAmountSell(new BigDecimal(row.getString(amountSell.F.underscore())));
		transaction.setAmountBuy(new BigDecimal(row.getString(amountBuy.F.underscore())));
		transaction.setRate(new BigDecimal(row.getString(rate.F.underscore())));
		transaction.setPlacedTime(new DateTime(row.getTimestamp(placedTime.F.underscore())));
		transaction.setOriginatingCountry(row.getString(originatingCountry.F.underscore()));
		transaction.setReceivedTime(new DateTime(row.getTimestamp(receivedTime.F.underscore())));
		transaction.setNodeName(row.getString(nodeName.F.underscore()));
		final String validationStatusStr = row.getString(validationStatus.F.underscore());
		if (validationStatusStr != null) {
			transaction.setValidationStatus(TransactionValidationStatus.valueOf(validationStatusStr));
		}
		return transaction;
	}
}
