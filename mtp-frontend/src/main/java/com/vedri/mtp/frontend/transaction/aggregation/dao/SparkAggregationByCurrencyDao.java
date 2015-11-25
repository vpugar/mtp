package com.vedri.mtp.frontend.transaction.aggregation.dao;

import static com.vedri.mtp.core.transaction.aggregation.TimeAggregation.TimeFields.*;
import static com.vedri.mtp.core.transaction.aggregation.TransactionAggregationByCurrency.Fields.*;

import java.util.ArrayList;

import org.apache.spark.FutureAction;
import org.apache.spark.rdd.RDD;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

import scala.collection.Seq;
import scala.reflect.ClassTag$;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.pattern.Patterns;

import com.datastax.spark.connector.japi.CassandraStreamingJavaUtil;
import com.google.common.collect.Lists;
import com.vedri.mtp.core.CoreProperties;
import com.vedri.mtp.core.currency.Currency;
import com.vedri.mtp.core.transaction.aggregation.TransactionAggregationByCurrency;
import com.vedri.mtp.core.transaction.aggregation.TransactionAggregationByUser;
import com.vedri.mtp.core.transaction.aggregation.YearToHourTime;
import com.vedri.mtp.core.transaction.aggregation.dao.YearToHourTimeUtil;

public class SparkAggregationByCurrencyDao {

	private final String tableName;
	private final JavaStreamingContext streamingContext;
	private final CoreProperties.Cassandra cassandra;
	private final ActorSystem actorSystem;

	public SparkAggregationByCurrencyDao(final String tableName,
			final JavaStreamingContext streamingContext,
			final CoreProperties.Cassandra cassandra, final ActorSystem actorSystem) {
		this.tableName = tableName;
		this.streamingContext = streamingContext;
		this.cassandra = cassandra;
		this.actorSystem = actorSystem;
	}

	public void load(final Currency currency, final YearToHourTime yearToHourTime, final ActorRef requester) {

		final ArrayList<Object> queryArgs = Lists.newArrayList(currency);
		final String query = YearToHourTimeUtil.composeQuery(yearToHourTime, "user_id = ?", queryArgs);

		final RDD<TransactionAggregationByCurrency> rdd = CassandraStreamingJavaUtil
				.javaFunctions(streamingContext)
				.cassandraTable(cassandra.getKeyspace(), tableName)
				.where(query, queryArgs.toArray())
				.map(row -> new TransactionAggregationByCurrency(
						currency.getCode(), row.getInt(year.F.underscore()), row.getInt(month.F.underscore()),
						row.getInt(day.F.underscore()), row.getInt(hour.F.underscore()),
						row.getLong(transactionCountFrom.F.underscore()),
						row.getLong(transactionCountTo.F.underscore()),
						row.getLong(amountFromUnscaled.F.underscore()),
						row.getLong(amountToUnscaled.F.underscore())))
				.rdd();

		final FutureAction<Seq<TransactionAggregationByCurrency>> futureAction = RDD
				.rddToAsyncRDDActions(rdd, ClassTag$.MODULE$.apply(TransactionAggregationByUser.class))
				.collectAsync();

		Patterns.pipe(futureAction, actorSystem.dispatcher()).to(requester);
	}

}
