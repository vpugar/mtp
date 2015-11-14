package com.vedri.mtp.frontend.transaction.aggregation.dao;

import static com.vedri.mtp.core.transaction.aggregation.TransactionAggregationByCountry.Fields.transactionCount;

import org.apache.spark.FutureAction;
import org.apache.spark.rdd.RDD;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import scala.collection.Seq;
import scala.reflect.ClassTag$;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.pattern.Patterns;

import com.datastax.spark.connector.japi.CassandraStreamingJavaUtil;
import com.vedri.mtp.core.CoreProperties;
import com.vedri.mtp.core.transaction.TableName;
import com.vedri.mtp.core.transaction.aggregation.TransactionAggregationByCountry;
import com.vedri.mtp.core.transaction.aggregation.YearToHourTime;

@Repository
public class SparkRtByOriginatingCountryDao {

	private final JavaStreamingContext streamingContext;
	private final CoreProperties.Cassandra cassandra;
	private final ActorSystem actorSystem;

	@Autowired
	public SparkRtByOriginatingCountryDao(final JavaStreamingContext streamingContext,
			final CoreProperties.Cassandra cassandra, ActorSystem actorSystem) {
		this.streamingContext = streamingContext;
		this.cassandra = cassandra;
		this.actorSystem = actorSystem;
	}

	public void load(final String originatingCountry, final YearToHourTime yearToHourTime, ActorRef requester) {
		final RDD<TransactionAggregationByCountry> rdd = CassandraStreamingJavaUtil
				.javaFunctions(streamingContext)
				.cassandraTable(cassandra.getKeyspace(), TableName.PT_AGGREGATION_BY_ORIGINATING_COUNTRY)
				.where("originating_country = ? and year = ? and month = ? and day = ? and hour = ?",
						originatingCountry, yearToHourTime.getYear(), yearToHourTime.getMonth(),
						yearToHourTime.getDay(), yearToHourTime.getHour())
				.map(row -> new TransactionAggregationByCountry(
						originatingCountry, yearToHourTime, row.getLong(transactionCount.F.underscore())))
				.rdd();

		final FutureAction<Seq<TransactionAggregationByCountry>> futureAction = RDD
				.rddToAsyncRDDActions(rdd, ClassTag$.MODULE$.apply(TransactionAggregationByCountry.class))
				.collectAsync();

		Patterns.pipe(futureAction, actorSystem.dispatcher()).to(requester);
	}

}
