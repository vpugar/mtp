package com.vedri.mtp.consumption.support.cassandra;

import com.datastax.driver.core.Session;
import org.cassandraunit.CQLDataLoader;
import org.cassandraunit.dataset.cql.ClassPathCQLDataSet;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

public class EmbeddedCassandraSupport {

    private final Session session;
    private String cqlInitScript = "cql/create_tables.cql";

    @Autowired
    public EmbeddedCassandraSupport(Session session) {
        this.session = session;
    }

    @PostConstruct
    public void create() {
        ClassPathCQLDataSet dataSet = new ClassPathCQLDataSet(cqlInitScript, session.getLoggedKeyspace());
        CQLDataLoader dataLoader = new CQLDataLoader(session);
        dataLoader.load(dataSet);
    }

    public void truncate(String columnFamily) {
        session.execute("truncate " + columnFamily);
    }

    public void setCqlInitScript(String cqlInitScript) {
        this.cqlInitScript = cqlInitScript;
    }
}
