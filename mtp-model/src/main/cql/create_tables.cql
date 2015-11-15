use mtp;

CREATE TABLE IF NOT EXISTS country (
  cca2 text,
  cca3 text,
  common_name text,
  official_name text,
  currencies set<text>,
  PRIMARY KEY (cca2)
);

CREATE TABLE IF NOT EXISTS rate (
  currency_from text,
  currency_to text,
  rate_date timestamp,
  cf_rate text,
  bank_rate text,
  PRIMARY KEY ((currency_from, currency_to), rate_date)
) WITH CLUSTERING ORDER BY (rate_date DESC);

CREATE TABLE IF NOT EXISTS transaction (
  partition text,
  transaction_id timeuuid,
  user_id text,
  currency_from text,
  currency_to text,
  amount_sell text,
  amount_buy text,
  amount_points text,
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
  amount_points_unscaled counter,
   PRIMARY KEY ((validation_status), year, month, day, hour)
) WITH CLUSTERING ORDER BY (year DESC, month DESC, day DESC, hour DESC);

CREATE TABLE IF NOT EXISTS rt_aggregation_by_originating_country (
   originating_country text,
   year int,
   month int,
   day int,
   hour int,
   transaction_count counter,
  amount_points_unscaled counter,
   PRIMARY KEY ((originating_country), year, month, day, hour)
) WITH CLUSTERING ORDER BY (year DESC, month DESC, day DESC, hour DESC);

CREATE TABLE IF NOT EXISTS rt_aggregation_by_user (
   user_id text,
   year int,
   month int,
   day int,
   hour int,
   transaction_count counter,
  amount_points_unscaled counter,
   PRIMARY KEY ((user_id), year, month, day, hour)
) WITH CLUSTERING ORDER BY (year DESC, month DESC, day DESC, hour DESC);

CREATE TABLE IF NOT EXISTS rt_aggregation_by_currency (
   currency text,
   year int,
   month int,
   day int,
   hour int,
   transaction_count_from counter,
   amount_from_unscaled counter,
   transaction_count_to counter,
   amount_to_unscaled counter,
   PRIMARY KEY ((currency), year, month, day, hour)
) WITH CLUSTERING ORDER BY (year DESC, month DESC, day DESC, hour DESC);

CREATE TABLE IF NOT EXISTS pt_aggregation_by_originating_country (
   originating_country text,
   year int,
   month int,
   day int,
   hour int,
   transaction_count counter,
  amount_points_unscaled counter,
   PRIMARY KEY ((originating_country), year, month, day, hour)
) WITH CLUSTERING ORDER BY (year DESC, month DESC, day DESC, hour DESC);

CREATE TABLE IF NOT EXISTS pt_aggregation_by_user (
   user_id text,
   year int,
   month int,
   day int,
   hour int,
   transaction_count counter,
  amount_points_unscaled counter,
   PRIMARY KEY ((user_id), year, month, day, hour)
) WITH CLUSTERING ORDER BY (year DESC, month DESC, day DESC, hour DESC);

CREATE TABLE IF NOT EXISTS pt_aggregation_by_currency (
   currency text,
   year int,
   month int,
   day int,
   hour int,
   transaction_count_from counter,
   amount_from_unscaled counter,
   transaction_count_to counter,
   amount_to_unscaled counter,
   PRIMARY KEY ((currency), year, month, day, hour)
) WITH CLUSTERING ORDER BY (year DESC, month DESC, day DESC, hour DESC);


-- user

CREATE TABLE IF NOT EXISTS user (
    id text,
    login text,
    password text,
    firstname text,
    lastname text,
    email text,
    activated boolean,
    lang_key text,
    activation_key text,
    reset_key text,
    reset_date timestamp,
    authorities set<text>,
    PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS user_by_login (
    login text,
    id text,
    PRIMARY KEY(login, id)
);

CREATE TABLE IF NOT EXISTS user_by_email (
    email text,
    id text,
    PRIMARY KEY(email, id)
);

CREATE TABLE IF NOT EXISTS user_by_activation_key (
    activation_key text,
    id text,
    PRIMARY KEY(activation_key, id)
);

CREATE TABLE IF NOT EXISTS user_by_reset_key (
    reset_key text,
    id text,
    PRIMARY KEY(reset_key, id)
);

CREATE TABLE IF NOT EXISTS activation_key_by_creation_date (
    creation_date timeuuid,
    activation_key text,
    PRIMARY KEY(creation_date, activation_key)
);

-- TODO token

CREATE TABLE IF NOT EXISTS persistent_token (
    series text,
    token_date timestamp,
    user_agent text,
    token_value text,
    login text,
    user_id text,
    ip_address text,
    PRIMARY KEY(series)
);

CREATE TABLE IF NOT EXISTS persistent_token_by_user (
    user_id text,
    persistent_token_series text,
    PRIMARY KEY(user_id)
);