package com.vedri.mtp.processor.streaming.handler;

import com.vedri.mtp.core.MtpConstants;
import com.vedri.mtp.core.transaction.TableName;
import com.vedri.mtp.core.transaction.Transaction;
import com.vedri.mtp.core.transaction.aggregation.TransactionAggregationByCountry;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.joda.time.DateTime;

public class ReceivedDayTransactionAggregationByCountryBuilder
        extends TimeTransactionAggregationByCountryBuilderTemplate {

    public ReceivedDayTransactionAggregationByCountryBuilder(
            StreamBuilder<?, JavaDStream<Transaction>> prevBuilder, String keyspace) {
        super(prevBuilder, keyspace, TableName.RT_DAY_AGGREGATION_BY_ORIGINATING_COUNTRY);
    }

    @Override
    protected Function<Transaction, TransactionAggregationByCountry> mapFunction() {
        return transaction -> {
            final DateTime time = transaction
                    .getReceivedTime()
                    .withZone(MtpConstants.DEFAULT_TIME_ZONE);
            return new TransactionAggregationByCountry(transaction.getOriginatingCountry(),
                    time.getYear(), time.getMonthOfYear(),
                    time.getDayOfMonth(), null, 1, transaction.getAmountPoints());
        };
    }

}
