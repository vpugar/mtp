
-- NOTE: uses replication factor 1
CREATE KEYSPACE IF NOT EXISTS mtp
  WITH REPLICATION = {
    'class' : 'SimpleStrategy',
    'replication_factor': 1
  };