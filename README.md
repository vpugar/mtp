# MTP (My Test Project)

## Wiki
* [Description](https://github.com/vpugar/mtp/wiki)
* [Build and Installation](https://github.com/vpugar/mtp/wiki/Build-and-Installation) 
* [Status](https://github.com/vpugar/mtp/wiki/Status)

## Components
* mtp-client - Akka HTTP client, sends HTTP requests to the mtp-consumption 
* mtp-consumption - Akka HTTP server, receives requests from mtp-consumption and sends data over Kafka Client to the mtp-processor   
* mtp-processor - Embedded Kafka server with Spark streaming, receives data from  
* mtp-frontend (in progress) - AngularJS, Bootstrap and WebSocket 
* mtp-core - utility components

## Library Highlights
### Java VM
* Java 8
* Scala 2.11         
* AkkaHttp 1.0
* Akka 2.3.12
* Apache Kafka 0.8.2.2        
* Apache Spark 1.5.1 
* CassandraDriver 2.2
* Guava 16.0.1 
* Jackson 2.4.4
* Kryo 3.0.3
* SparkCassandraConnector 1.5.0-M2  
* Spring 4.2.2
* SpringBoot 1.3.0
* SpringSecurity 4.0.2
* TestNG 6.9.4

### Web
* Bootstrap 3.3.5
* AngularJS 1.4.5
* ...
