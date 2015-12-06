package com.vedri.mtp.consumption.support.cassandra;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.vedri.mtp.core.CoreProperties;
import org.cassandraunit.CQLDataLoader;
import org.cassandraunit.dataset.cql.ClassPathCQLDataSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class EmbeddedCassandraSupport {

    @Autowired
    private CoreProperties.Cassandra cassandra;

    @Autowired
    private EmbeddedCassandra embeddedCassandra;
    private Session session;
    private String cqlKeyspaceScript = "create_keyspace.cql";
    private String cqlTableScript = "create_tables.cql";

    @PostConstruct
    public void create() {
        Cluster cluster = new Cluster.Builder().addContactPoints("127.0.0.1").withPort(9142).build();
        session = cluster.connect();
        CQLDataLoader dataLoader = new CQLDataLoader(session);

        // remove this, already created by CQLDataLoader
//        ClassPathCQLDataSet dataSet1 = new ClassPathCQLDataSet(cqlKeyspaceScript, cassandra.getKeyspace());
//        dataLoader.load(dataSet1);

        ClassPathCQLDataSet dataSet2 = new ClassPathCQLDataSet(cqlTableScript, cassandra.getKeyspace());
        dataLoader.load(dataSet2);
    }

    public void truncate(String columnFamily) {
        session.execute("truncate " + columnFamily);
    }
}
