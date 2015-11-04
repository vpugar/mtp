
-- NOTE: uses replication factor 3
CREATE KEYSPACE IF NOT EXISTS mtp
  WITH REPLICATION = {
    'class' : 'SimpleStrategy',
    'replication_factor': 3
  };