package com.vedri.mtp.frontend.transaction.aggregation.dao;

import static com.vedri.mtp.core.transaction.aggregation.TimeAggregation.TimeFields.*;
import static com.vedri.mtp.core.transaction.aggregation.TransactionAggregationByCountry.Fields.*;

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
import com.datastax.spark.connector.rdd.ReadConf;
import com.google.common.collect.Lists;
import com.vedri.mtp.core.CoreProperties;
import com.vedri.mtp.core.transaction.aggregation.TransactionAggregationByCountry;
import com.vedri.mtp.core.transaction.aggregation.YearToHourTime;
import com.vedri.mtp.core.transaction.aggregation.dao.YearToHourTimeUtil;

public class SparkAggregationByOriginatingCountryDao {

	private final String hourTableName;
	private final String dayTableName;
	private final JavaStreamingContext streamingContext;
	private final CoreProperties.Cassandra cassandra;
	private final ActorSystem actorSystem;

	public SparkAggregationByOriginatingCountryDao(final String hourTableName, final String dayTableName,
			final JavaStreamingContext streamingContext,
			final CoreProperties.Cassandra cassandra, final ActorSystem actorSystem) {
		this.hourTableName = hourTableName;
		this.dayTableName = dayTableName;
		this.streamingContext = streamingContext;
		this.cassandra = cassandra;
		this.actorSystem = actorSystem;
	}

	public void load(final String originatingCountry, final YearToHourTime yearToHourTime, final ActorRef requester) {

		final ArrayList<Object> queryArgs = Lists.newArrayList(originatingCountry);
		final String query = YearToHourTimeUtil.composeQuery(yearToHourTime, "originating_country = ?", queryArgs);

		final RDD<TransactionAggregationByCountry> rdd = CassandraStreamingJavaUtil
				.javaFunctions(streamingContext)
				.cassandraTable(cassandra.getKeyspace(), hourTableName)
				.where(query, queryArgs.toArray())
				.map(row -> new TransactionAggregationByCountry(
						originatingCountry, row.getInt(year.F.underscore()), row.getInt(month.F.underscore()),
						row.getInt(day.F.underscore()), row.getInt(hour.F.underscore()),
						row.getLong(transactionCount.F.underscore()),
						row.getLong(amountPointsUnscaled.F.underscore())))
				.rdd();

		final FutureAction<Seq<TransactionAggregationByCountry>> futureAction = RDD
				.rddToAsyncRDDActions(rdd, ClassTag$.MODULE$.apply(TransactionAggregationByCountry.class))
				.collectAsync();

		Patterns.pipe(futureAction, actorSystem.dispatcher()).to(requester);
	}

	public void loadAll(final int totalCount, final YearToHourTime yearToHourTime, final ActorRef requester) {

		final ReadConf readConf = new ReadConf(scala.Option.apply(1), ReadConf.DefaultSplitSizeInMB(), totalCount,
				ReadConf.DefaultConsistencyLevel(), ReadConf.DefaultReadTaskMetricsEnabled());

		final ArrayList<Object> queryArgs = Lists.newArrayList();
		final String query = YearToHourTimeUtil.composeQuery(yearToHourTime, "", queryArgs);

		final RDD<TransactionAggregationByCountry> rdd = CassandraStreamingJavaUtil
				.javaFunctions(streamingContext)
				.cassandraTable(cassandra.getKeyspace(), dayTableName)
				.withReadConf(readConf)
				.where(query, queryArgs.toArray())
				.limit((long) totalCount)
				.map(row -> new TransactionAggregationByCountry(
						row.getString(originatingCountry.F.underscore()),
						row.getInt(year.F.underscore()), row.getInt(month.F.underscore()),
						row.getInt(day.F.underscore()), null,
						row.getLong(transactionCount.F.underscore()),
						row.getLong(amountPointsUnscaled.F.underscore())))
				.rdd();

		final FutureAction<Seq<TransactionAggregationByCountry>> futureAction = RDD
				.rddToAsyncRDDActions(rdd, ClassTag$.MODULE$.apply(TransactionAggregationByCountry.class))
				.collectAsync();

		Patterns.pipe(futureAction, actorSystem.dispatcher()).to(requester);
	}

}
