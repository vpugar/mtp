import com.datastax.spark.connector._
import org.apache.spark.sql.SQLContext
import org.apache.spark.{SparkContext, SparkConf}

// 10.1.6.217
val conf = new SparkConf(true)
conf.set("spark.cassandra.connection.host", "10.1.6.217")
val sc = new SparkContext(conf)
val sqlContext = new SQLContext(sc)

val countries = sqlContext.read.json("./*.json")
val countryData = countries.selectExpr("name.common as common_name", "name.official as official_name", "cca2", "cca3", "currency as currencies")
countryData.printSchema()

val filteredCountryData = countryData.filter("cca2 is not null")
filteredCountryData.write.format("org.apache.spark.sql.cassandra").options(Map("keyspace" -> "mtp", "table" -> "country")).save()

//countryData.filter("cca2 is null").foreach(println)



