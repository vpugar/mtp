package com.vedri.mtp.consumption.transaction.dao;

import static com.vedri.mtp.core.transaction.Transaction.Fields.*;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.datastax.driver.core.*;
import com.vedri.mtp.core.support.cassandra.CassandraPartitionUtils;
import com.vedri.mtp.core.support.cassandra.CassandraUtils;
import com.vedri.mtp.core.transaction.Transaction;

@Component
@Slf4j
public class CassandraTransactionDao implements TransactionDao {

	private static final String INSERT_STATEMENT = "INSERT INTO transaction (partition, transaction_id, user_id, " +
			"currency_from, currency_to, amount_sell, amount_buy, rate, placed_time, originating_country, " +
			"received_time, node_name) " +
			"VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
	private static final String LOAD_STATEMENT = "SELECT user_id, currency_from, currency_to, amount_sell, amount_buy, "
			+
			"rate, placed_time, originating_country, received_time, node_name " +
			"FROM transaction " +
			"WHERE partition = ? AND transaction_id = ?";

	private final Session session;

	private PreparedStatement insertStatement;
	private PreparedStatement loadStatement;

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

		final long currentTimeMillis = System.currentTimeMillis();
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

		return mapRowToTransaction(partitionForMinute, timeUUID.toString(), row);
	}

	private void initStatements() {
		insertStatement = session.prepare(INSERT_STATEMENT);
		loadStatement = session.prepare(LOAD_STATEMENT);
	}

	private Transaction mapRowToTransaction(final String partition, final String transactionId, final Row row) {
		Transaction transaction = new Transaction(partition, transactionId);
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
		return transaction;
	}
}
