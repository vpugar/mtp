
FROM cassandra:2.1.16

ENV LOCAL_SRC_HOME .
ENV SRC_HOME /code/mtp
ENV SPARK_HOME /opt/spark

WORKDIR $SRC_HOME

RUN mkdir $SRC_HOME/cql
RUN mkdir $SRC_HOME/countries

RUN apt-get update

RUN apt-get -y install wget

ADD $LOCAL_SRC_HOME/mtp-model/src/main/cql/*.cql $SRC_HOME/cql/


WORKDIR /usr/local/bin
ENV DOCKERIZE_VERSION v0.3.0
RUN wget https://github.com/jwilder/dockerize/releases/download/$DOCKERIZE_VERSION/dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz \
    && tar -C /usr/local/bin -xzvf dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz \
    && rm dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz


#RUN dockerize -wait tcp://localhost:9042 cqlsh --version


WORKDIR $SRC_HOME/cql

#RUN EXTERNAL_HOST="$(ip route show 0.0.0.0/0 | grep -Eo 'via \S+' | awk '{ print $2 }')"; \
#    cqlsh -f create_keyspace.cql $EXTERNAL_HOST 19042
#RUN EXTERNAL_HOST="$(ip route show 0.0.0.0/0 | grep -Eo 'via \S+' | awk '{ print $2 }')"; \
#    cqlsh -k mtp -f create_tables.cql $EXTERNAL_HOST 19042
#RUN EXTERNAL_HOST="$(ip route show 0.0.0.0/0 | grep -Eo 'via \S+' | awk '{ print $2 }')"; \
#    cqlsh -k mtp -f create_data.cql $EXTERNAL_HOST 19042

WORKDIR $SPARK_HOME
#COPY spark-1.5.2-bin-hadoop2.6.tgz .
RUN wget http://archive.apache.org/dist/spark/spark-1.5.2/spark-1.5.2-bin-hadoop2.6.tgz
RUN tar xzf spark-1.5.2-bin-hadoop2.6.tgz

#RUN tail -100 /var/log/cassandra/system.log

WORKDIR $SRC_HOME/countries/
ADD $LOCAL_SRC_HOME/mtp-model/src/main/spark/countries/* $SRC_HOME/countries/
#RUN EXTERNAL_HOST="$(ip route show 0.0.0.0/0 | grep -Eo 'via \S+' | awk '{ print $2 }')"; \
#    $SPARK_HOME/spark-1.5.2-bin-hadoop2.6/bin/spark-shell \
#    --packages com.datastax.spark:spark-cassandra-connector_2.10:1.5.0-M2 \
#    --conf spark.cassandra.connection.host=$EXTERNAL_HOST \
#    --conf spark.cassandra.connection.port=19042 < import_countries.script.scala
