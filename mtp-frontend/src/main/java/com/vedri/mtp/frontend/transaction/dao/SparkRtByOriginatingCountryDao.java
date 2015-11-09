package com.vedri.mtp.frontend.transaction.dao;

import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class SparkRtByOriginatingCountryDao {

    private final JavaStreamingContext streamingContext;

    @Autowired
    public SparkRtByOriginatingCountryDao(JavaStreamingContext streamingContext) {
        this.streamingContext = streamingContext;
    }



}
