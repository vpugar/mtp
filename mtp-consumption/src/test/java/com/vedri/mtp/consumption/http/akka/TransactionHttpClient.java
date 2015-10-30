package com.vedri.mtp.consumption.http.akka;

import java.util.concurrent.TimeUnit;

import akka.actor.ActorSystem;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import akka.http.javadsl.model.ContentType;
import akka.http.javadsl.model.HttpCharsets;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.model.MediaTypes;

import com.vedri.mtp.core.CoreConfig;
import com.vedri.mtp.core.support.akka.AkkaConfiguration;
import com.vedri.mtp.core.support.spring.AbstractApplication;

public class TransactionHttpClient extends AbstractApplication {

	@Configuration
	public static class ClientSenderConfiguration extends AkkaConfiguration {
	}

	private HttpClient1 httpClient;

	@Override
	protected Class[] getConfigs() {
		return new Class[] {
				CoreConfig.class, ClientSenderConfiguration.class
		};
	}

	@Override
	protected void doStart(ApplicationContext context) throws Exception {

		final ActorSystem actorSystem = context.getBean(ActorSystem.class);

		httpClient = new HttpClient1(actorSystem);
		httpClient.init();

		try {
			send(httpClient);
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
		} finally {
			httpClient.destroy();
			stopApplication(context);
		}
	}

	public void send(HttpClient1 httpClient) throws Exception {
		final String request = "{\n" +
				"     \"userId\": \"134256\",\n" +
				"     \"currencyFrom\": \"EUR\",\n" +
				"     \"currencyTo\": \"GBP\",\n" +
				"     \"amountSell\": 1000,\n" +
				"     \"amountBuy\": 747.10,\n" +
				"     \"rate\": 0.7471,\n" +
				"     \"timePlaced\" : \"24JAN15 10:27:44\",\n" +
				"     \"originatingCountry\": \"FR\"\n" +
				"}";
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
