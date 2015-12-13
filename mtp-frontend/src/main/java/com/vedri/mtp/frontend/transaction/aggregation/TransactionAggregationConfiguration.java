package com.vedri.mtp.frontend.transaction.aggregation;

import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import akka.actor.ActorSystem;

import com.vedri.mtp.core.CoreProperties;
import com.vedri.mtp.core.transaction.TableName;
import com.vedri.mtp.frontend.transaction.aggregation.dao.SparkAggregationByCurrencyDao;
import com.vedri.mtp.frontend.transaction.aggregation.dao.SparkAggregationByOriginatingCountryDao;
import com.vedri.mtp.frontend.transaction.aggregation.dao.SparkAggregationByStatusDao;
import com.vedri.mtp.frontend.transaction.aggregation.dao.SparkAggregationByUserDao;

@Configuration
public class TransactionAggregationConfiguration {

	@Bean
	public SparkAggregationByCurrencyDao rtAggregationByCurrencyDao(
			JavaStreamingContext streamingContext, CoreProperties.Cassandra cassandra, ActorSystem actorSystem) {
		return new SparkAggregationByCurrencyDao(
				TableName.RT_AGGREGATION_BY_CURRENCY, TableName.RT_DAY_AGGREGATION_BY_CURRENCY,
				streamingContext, cassandra, actorSystem);
	}

	@Bean
	public SparkAggregationByCurrencyDao ptAggregationByCurrencyDao(
			JavaStreamingContext streamingContext, CoreProperties.Cassandra cassandra, ActorSystem actorSystem) {
		return new SparkAggregationByCurrencyDao(
				TableName.PT_AGGREGATION_BY_CURRENCY, TableName.PT_DAY_AGGREGATION_BY_CURRENCY,
				streamingContext, cassandra, actorSystem);
	}

	@Bean
	public SparkAggregationByOriginatingCountryDao rtAggregationByOriginatingCountryDao(
			JavaStreamingContext streamingContext, CoreProperties.Cassandra cassandra, ActorSystem actorSystem) {
		return new SparkAggregationByOriginatingCountryDao(
				TableName.RT_AGGREGATION_BY_ORIGINATING_COUNTRY, TableName.RT_DAY_AGGREGATION_BY_ORIGINATING_COUNTRY,
				streamingContext, cassandra, actorSystem);
	}

	@Bean
	public SparkAggregationByOriginatingCountryDao ptAggregationByOriginatingCountryDao(
			JavaStreamingContext streamingContext, CoreProperties.Cassandra cassandra, ActorSystem actorSystem) {
		return new SparkAggregationByOriginatingCountryDao(
				TableName.PT_AGGREGATION_BY_ORIGINATING_COUNTRY, TableName.PT_DAY_AGGREGATION_BY_ORIGINATING_COUNTRY,
				streamingContext, cassandra, actorSystem);
	}

	@Bean
	public SparkAggregationByStatusDao rtAggregationByStatusDao(
			JavaStreamingContext streamingContext, CoreProperties.Cassandra cassandra, ActorSystem actorSystem) {
		return new SparkAggregationByStatusDao(TableName.RT_AGGREGATION_BY_VALIDATION_STATUS,
				streamingContext, cassandra, actorSystem);
	}

	@Bean
	public SparkAggregationByUserDao rtAggregationByUserDao(
			JavaStreamingContext streamingContext, CoreProperties.Cassandra cassandra, ActorSystem actorSystem) {
		return new SparkAggregationByUserDao(TableName.RT_AGGREGATION_BY_USER,
				streamingContext, cassandra, actorSystem);
	}

	@Bean
	public SparkAggregationByUserDao ptAggregationByUserDao(
			JavaStreamingContext streamingContext, CoreProperties.Cassandra cassandra, ActorSystem actorSystem) {
		return new SparkAggregationByUserDao(TableName.PT_AGGREGATION_BY_USER,
				streamingContext, cassandra, actorSystem);
	}
}
