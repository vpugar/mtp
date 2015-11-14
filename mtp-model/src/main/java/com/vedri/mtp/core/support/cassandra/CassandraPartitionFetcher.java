package com.vedri.mtp.core.support.cassandra;

import com.datastax.driver.core.*;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CassandraPartitionFetcher {

    private PreparedStatement loadAllStatement;
    private PreparedStatement loadAllStatementBoundary;

    private Session session;
    private String indexByValue;
    private int fetchSize;
    private DateTime from;
    private DateTime to;
    private String currentPartition;
    private UUID lastToken;
    private int partitionMoveCount;
    private String terminalPartition;
    private float okPartitionDiff;

    private static final int MAX_PARTITION_MOVE_COUNT = 8800;

    public CassandraPartitionFetcher(Session session, String indexByValue,
                                     PreparedStatement loadAllStatement,
                                     PreparedStatement loadAllStatementBoundary,
                                     PreparedStatement loadAllStatementInvert,
                                     PreparedStatement loadAllStatementBoundaryInvert,
                                     int fetchSize, DateTime from, DateTime to) {
        this.session = session;

        this.indexByValue = indexByValue;

        if (from == null || to == null) {
            from = new DateTime(DateTimeZone.UTC);
            to = new DateTime(DateTimeZone.UTC).minusHours(1);
        }

        this.fetchSize = fetchSize;

        this.from = from;
        this.to = to;

        this.currentPartition = CassandraPartitionForHourUtils.datePartition(from);
        this.terminalPartition = CassandraPartitionForHourUtils.datePartition(to);

        this.lastToken = null;

        this.partitionMoveCount = 0;

        if (!isInvert()) {
            this.loadAllStatement = loadAllStatement;
            this.loadAllStatementBoundary = loadAllStatementBoundary;

            okPartitionDiff = -1f;
        } else {
            this.loadAllStatement = loadAllStatementInvert;
            this.loadAllStatementBoundary = loadAllStatementBoundaryInvert;

            this.okPartitionDiff = 1f;
        }
    }

    public boolean isInvert() {
        return !from.isBefore(to);
    }

    public DateTime getStart() {
        if (isInvert()) {
            return to;
        } else {
            return from;
        }
    }

    public DateTime getEnd() {
        if (isInvert()) {
            return from;
        } else {
            return to;
        }
    }

    public List<Row> getNextPage() {

        List<Row> resultRows = new ArrayList<>();

        boolean bindWithPrevious = false;

        do {
            final BoundStatement bs =
                    lastToken == null ?
                            loadAllStatement.bind(currentPartition) :
                            loadAllStatementBoundary.bind(currentPartition, lastToken);

            List<Row> result = session.execute(bs).all();

            if (result.size() == fetchSize) {
                if (bindWithPrevious) {
                    resultRows.addAll(result.subList(0, fetchSize - resultRows.size()));
                } else {
                    resultRows = result;
                }

                lastToken = resultRows.get(resultRows.size() - 1).getUUID(indexByValue);

            } else if (result.size() == 0) {
                moveToNextPartition();

            } else if (result.size() < fetchSize) {
                moveToNextPartition();
                lastToken = null;

                if (bindWithPrevious) {
                    resultRows.addAll(result.subList(0, Math.min(result.size(), fetchSize - resultRows.size())));
                } else {
                    resultRows = result;
                }

                bindWithPrevious = true;
            }

            final float compareResult = Math.signum(currentPartition.compareTo(terminalPartition));

            if (resultRows.size() == fetchSize
                    || partitionMoveCount >= MAX_PARTITION_MOVE_COUNT
                    || (compareResult != okPartitionDiff && compareResult != 0)) {
                break;
            }

        } while (true);

        return resultRows;
    }

    private void moveToNextPartition() {
        partitionMoveCount++;

        currentPartition =
                CassandraPartitionForHourUtils.offsetPartitionIntoHistoryBy(currentPartition, from.isBefore(to) ? -1 : 1);
    }
}

