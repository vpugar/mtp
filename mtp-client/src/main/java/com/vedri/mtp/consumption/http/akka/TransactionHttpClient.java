package com.vedri.mtp.consumption.http.akka;

import java.util.concurrent.TimeUnit;

import com.vedri.mtp.core.CoreProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import akka.actor.ActorSystem;
import akka.http.javadsl.model.ContentType;
import akka.http.javadsl.model.HttpCharsets;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.model.MediaTypes;

import com.vedri.mtp.core.CoreConfig;
import com.vedri.mtp.core.support.akka.AkkaConfiguration;
import com.vedri.mtp.core.support.http.AkkaHttpClient1;
import com.vedri.mtp.core.support.spring.AbstractApplication;

@Slf4j
public class TransactionHttpClient extends AbstractApplication {

	@Configuration
	public static class TransactionHttpClientConfiguration {

		@Autowired
		protected CoreProperties coreProperties;

		@Bean
		CoreProperties.Akka akka() {
			return coreProperties.getAkka();
		}

		@Bean
		CoreProperties.Cluster cluster() {
			return coreProperties.getCluster();
		}
	}

	@Configuration
	public static class ClientAkkConfiguration extends AkkaConfiguration {
	}

	private AkkaHttpClient1 httpClient;

	@Override
	protected Class[] getConfigs() {
		return new Class[] {
				CoreConfig.class, ClientAkkConfiguration.class, TransactionHttpClientConfiguration.class
		};
	}

	@Override
	protected void doStart(ApplicationContext context) throws Exception {

		final ActorSystem actorSystem = context.getBean(ActorSystem.class);

		httpClient = new AkkaHttpClient1(actorSystem);
		httpClient.init();

		try {
			send(httpClient);
		}
		catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		finally {
			httpClient.destroy();
			stopApplication(context);
		}
	}

	protected String doCreateRequest() throws Exception {
		return "{\n" +
				"     \"userId\": \"134256\",\n" +
				"     \"currencyFrom\": \"EUR\",\n" +
				"     \"currencyTo\": \"GBP\",\n" +
				"     \"amountSell\": 1000,\n" +
				"     \"amountBuy\": 747.10,\n" +
				"     \"rate\": 0.7471,\n" +
				"     \"timePlaced\" : \"24JAN15 10:27:44\",\n" +
				"     \"originatingCountry\": \"FR\"\n" +
				"}";
	}

	public void send(AkkaHttpClient1 httpClient) throws Exception {

		final String request = doCreateRequest();
		final ContentType contentType = ContentType.create(MediaTypes.APPLICATION_JSON, HttpCharsets.UTF_8);
		final Future<HttpResponse> post = httpClient.post("http://localhost:9090/transactions",
				contentType, request);

		final HttpResponse result = Await.result(post, Duration.apply(10, TimeUnit.SECONDS));
		System.out.println("Response: " + result);
	}

	public static void main(String[] args) throws Exception {
		new TransactionHttpClient().startApplication(args, "nodeClient0");
	}

}
