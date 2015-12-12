package com.vedri.mtp.processor.streaming.handler;

import static com.vedri.mtp.core.transaction.Transaction.Fields.*;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.spark.streaming.api.java.JavaDStream;

import com.datastax.spark.connector.japi.CassandraJavaUtil;
import com.datastax.spark.connector.japi.CassandraStreamingJavaUtil;
import com.vedri.mtp.core.transaction.TableName;
import com.vedri.mtp.core.transaction.Transaction;

@AllArgsConstructor
@NoArgsConstructor
public class StoreTransactionStatusBuilder extends StreamBuilder<JavaDStream<Transaction>, NoIO> {

	private String keyspace;

	public StoreTransactionStatusBuilder(StreamBuilder<?, JavaDStream<Transaction>> prevBuilder, String keyspace) {
		super(prevBuilder);
		this.keyspace = keyspace;
	}

	@Override
	public NoIO doBuild(JavaDStream<Transaction> transactionWithStatusStream) {

		CassandraStreamingJavaUtil
				.javaFunctions(transactionWithStatusStream)
				.writerBuilder(keyspace, TableName.TRANSACTION, CassandraJavaUtil.mapToRow(Transaction.class,
						Pair.of(partition.F.cammelCase(), partition.F.underscore()),
						Pair.of(transactionId.F.cammelCase(), transactionId.F.underscore()),
						Pair.of(amountPoints.F.cammelCase(), amountPoints.F.underscore()),
						Pair.of(validationStatus.F.cammelCase(), validationStatus.F.underscore())))
				.withColumnSelector(CassandraJavaUtil.someColumns(
						partition.F.underscore(), transactionId.F.underscore(),
						amountPoints.F.underscore(), validationStatus.F.underscore()))
				.saveToCassandra();

//		transactionWithStatusStream.print(1);

		return null;
	}
}
