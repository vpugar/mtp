use mtp;

CREATE TABLE IF NOT EXISTS country (
  cca2 text,
  cca3 text,
  common_name text,
  official_name text,
  currencies set<text>,
  PRIMARY KEY (cca2)
);

CREATE TABLE IF NOT EXISTS transaction (
  partition text,
  transaction_id timeuuid,
  user_id text,
  currency_from text,
  currency_to text,
  amount_sell text,
  amount_buy text,
  rate text,
  placed_time timestamp,
  originating_country text,
  received_time timestamp,
  node_name text,
  validation_status text,
  PRIMARY KEY (partition, transaction_id)
) WITH CLUSTERING ORDER BY (transaction_id DESC);

CREATE TABLE IF NOT EXISTS rt_aggregation_by_validation_status (
   validation_status text,
   year int,
   month int,
   day int,
   hour int,
   transaction_count counter,
   PRIMARY KEY ((validation_status), year, month, day, hour)
) WITH CLUSTERING ORDER BY (year DESC, month DESC, day DESC, hour DESC);

CREATE TABLE IF NOT EXISTS rt_aggregation_by_originating_country (
   originating_country text,
   year int,
   month int,
   day int,
   hour int,
   transaction_count counter,
   PRIMARY KEY ((originating_country), year, month, day, hour)
) WITH CLUSTERING ORDER BY (year DESC, month DESC, day DESC, hour DESC);

CREATE TABLE IF NOT EXISTS rt_aggregation_by_user (
   user_id text,
   year int,
   month int,
   day int,
   hour int,
   transaction_count counter,
   PRIMARY KEY ((user_id), year, month, day, hour)
) WITH CLUSTERING ORDER BY (year DESC, month DESC, day DESC, hour DESC);

CREATE TABLE IF NOT EXISTS rt_aggregation_by_currency (
   currency text,
   year int,
   month int,
   day int,
   hour int,
   transaction_count_from counter,
   transaction_count_to counter,
   PRIMARY KEY ((currency), year, month, day, hour)
) WITH CLUSTERING ORDER BY (year DESC, month DESC, day DESC, hour DESC);

CREATE TABLE IF NOT EXISTS pt_aggregation_by_originating_country (
   originating_country text,
   year int,
   month int,
   day int,
   hour int,
   transaction_count counter,
   PRIMARY KEY ((originating_country), year, month, day, hour)
) WITH CLUSTERING ORDER BY (year DESC, month DESC, day DESC, hour DESC);

CREATE TABLE IF NOT EXISTS pt_aggregation_by_user (
   user_id text,
   year int,
   month int,
   day int,
   hour int,
   transaction_count counter,
   PRIMARY KEY ((user_id), year, month, day, hour)
) WITH CLUSTERING ORDER BY (year DESC, month DESC, day DESC, hour DESC);

CREATE TABLE IF NOT EXISTS pt_aggregation_by_currency (
   currency text,
   year int,
   month int,
   day int,
   hour int,
   transaction_count_from counter,
   transaction_count_to counter,
   PRIMARY KEY ((currency), year, month, day, hour)
) WITH CLUSTERING ORDER BY (year DESC, month DESC, day DESC, hour DESC);
