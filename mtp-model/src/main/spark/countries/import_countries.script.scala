
// 10.1.6.217
//val conf = new SparkConf(true)
//conf.set("spark.cassandra.connection.host", "10.1.6.217")
//val sc = new SparkContext(conf)
//val sqlContext = new SQLContext(sc)


/**
  * run command line and then copy paste in console: /opt/programs/spark-1.5.1-bin-hadoop2.6/bin/spark-shell --packages com.datastax.spark:spark-cassandra-connector_2.10:1.5.0-M2 --conf spark.cassandra.connection.host=10.1.6.217
  */

val countries = sqlContext.read.json("./*.json")
val countryData = countries.selectExpr("name.common as common_name", "name.official as official_name", "cca2", "cca3", "currency as currencies")
countryData.printSchema()

val filteredCountryData = countryData.filter("cca2 is not null")
filteredCountryData.write.format("org.apache.spark.sql.cassandra").options(Map("keyspace" -> "mtp", "table" -> "country")).save()

//countryData.filter("cca2 is null").foreach(println)



