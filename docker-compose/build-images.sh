#!/bin/bash

CURRENT_PATH=`pwd -P`
SCRIPT=`realpath -s $0`
SCRIPTPATH=`dirname $SCRIPT`

cd $SCRIPTPATH

echo "Working in $SCRIPTPATH"

echo "Building mtp-build"
#docker build --force-rm -f ../Dockerfile_mtp-build -t mtp-build:0.5 ..

echo "Building mtp-data - 1st part"
docker build --force-rm -f ../Dockerfile_mtp-data -t mtp-data:0.5  ..
ID="$(docker run -v mtp-data:/var/lib/cassandra/data -d --name mtp-data mtp-data:0.5)"
#ID="$(docker ps -f name=mtp-data -q | head -1)"
echo "Started casandra mtp-data with ID $ID"

echo "Building mtp-data - 2nd part - importing data"
docker exec $ID dockerize -wait tcp://localhost:9042 -timeout 60s cqlsh -f /code/mtp/cql/create_keyspace.cql
docker exec $ID cqlsh -f /code/mtp/cql/create_tables.cql
docker exec $ID cqlsh -f /code/mtp/cql/create_data.cql
docker exec $ID sh -c "cd /code/mtp/countries; /opt/spark/spark-1.5.2-bin-hadoop2.6/bin/spark-shell --packages com.datastax.spark:spark-cassandra-connector_2.10:1.5.0-M2 --conf spark.cassandra.connection.host=localhost < import_countries.script.scala"
docker commit $ID mtp-data:0.5
docker stop $ID

echo "Successfully finished build. Start all with 'start-compose.sh' or 'docker-compose up --build -d'"

cd $CURRENT_PATH
