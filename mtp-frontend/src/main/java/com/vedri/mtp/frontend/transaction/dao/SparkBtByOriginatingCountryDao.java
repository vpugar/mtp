package com.vedri.mtp.frontend.transaction.dao;

import com.datastax.spark.connector.japi.CassandraStreamingJavaUtil;
import com.vedri.mtp.core.CoreProperties;
import com.vedri.mtp.core.transaction.TableName;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class SparkBtByOriginatingCountryDao {

    private final JavaStreamingContext streamingContext;
    private final CoreProperties.Cassandra cassandra;

    @Autowired
    public SparkBtByOriginatingCountryDao(JavaStreamingContext streamingContext, CoreProperties.Cassandra cassandra) {
        this.streamingContext = streamingContext;
        this.cassandra = cassandra;
    }

    public void data() {
//        CassandraStreamingJavaUtil
//                .javaFunctions(streamingContext)
//                .cassandraTable(cassandra.getKeyspace(), TableName.PT_AGGREGATION_BY_ORIGINATING_COUNTRY)
//                .where()
    }
}
