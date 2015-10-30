package com.vedri.mtp.consumption.http.akka;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import com.vedri.mtp.core.transaction.TransactionValidationStatus;
import com.vedri.mtp.core.support.json.JacksonConfiguration;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.kubek2k.springockito.annotations.ReplaceWithMock;
import org.kubek2k.springockito.annotations.SpringockitoAnnotatedContextLoader;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import akka.actor.ActorSystem;
import akka.http.javadsl.model.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vedri.mtp.consumption.http.ConsumptionAkkaConfiguration;
import com.vedri.mtp.consumption.support.json.TransactionJacksonConfiguration;
import com.vedri.mtp.consumption.transaction.TransactionManager;
import com.vedri.mtp.core.CoreConfig;
import com.vedri.mtp.core.transaction.Transaction;

@ContextConfiguration(loader = SpringockitoAnnotatedContextLoader.class, classes = {
		AkkaHttpServer.class, ConsumptionAkkaConfiguration.class, TransactionJacksonConfiguration.class,
		JacksonConfiguration.class, CoreConfig.class, AkkaHttpServerTest.class
})
@TestPropertySource(properties = {
		"mtp.core.cluster.nodeName=node0",
		"mtp.core.akka.configurationName=MtpConsumption",
		"mtp.core.akka.logConfiguration=false",
		"mtp.consumption.server.bind.host=localhost",
		"mtp.consumption.server.bind.port=9090",
		"mtp.consumption.server.public.protocol=http",
		"mtp.consumption.server.public.host=localhost",
		"mtp.consumption.server.public.port=9090"

})
public class AkkaHttpServerTest extends AbstractTestNGSpringContextTests {

	@ReplaceWithMock
	@Autowired
	TransactionManager transactionManager;

	@Autowired
	private AkkaHttpServer akkaHttpServer;

	@Autowired
	private ActorSystem actorSystem;

	@Autowired
	@Qualifier("transactionObjectMapper")
	private ObjectMapper objectMapper;

	private HttpClient1 httpClient;

	@BeforeMethod
	public void init() {
		httpClient = new HttpClient1(actorSystem);
		httpClient.init();
	}

	@Test
	public void test() throws Exception {

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

		final Transaction resultTransaction = new Transaction(
				"p1", "t1", "134256", "EUR", "GBP", new BigDecimal("1000"), new BigDecimal("747.10"),
				new BigDecimal("0.7471"), new DateTime(2015, 1, 24, 10, 27, 44, DateTimeZone.UTC),
				"FR", new DateTime(), "n1", TransactionValidationStatus.OK);

		when(transactionManager.addTransaction(any(Transaction.class))).thenReturn(resultTransaction);

		final ContentType contentType = ContentType.create(MediaTypes.APPLICATION_JSON, HttpCharsets.UTF_8);
		final Future<HttpResponse> post = httpClient.post("http://localhost:9090/transactions",
				contentType, request);
		// objectMapper.writeValueAsString(requestTransaction)
		final HttpResponse result = Await.result(post, Duration.apply(10, TimeUnit.SECONDS));
		assertEquals(result.status(), StatusCodes.CREATED, "not created");

		ArgumentCaptor<Transaction> commandCaptor = ArgumentCaptor.forClass(Transaction.class);
		verify(transactionManager, times(1)).addTransaction(commandCaptor.capture());
		verifyNoMoreInteractions(transactionManager);

		final Transaction value = commandCaptor.getValue();
		assertNotNull(value);
		assertEquals(value.getUserId(), resultTransaction.getUserId());
		assertEquals(value.getCurrencyFrom(), resultTransaction.getCurrencyFrom());
		assertEquals(value.getCurrencyTo(), resultTransaction.getCurrencyTo());
		assertEquals(value.getAmountSell(), resultTransaction.getAmountSell());
		assertEquals(value.getAmountBuy(), resultTransaction.getAmountBuy());
		assertEquals(value.getRate(), resultTransaction.getRate());
		assertEquals(value.getPlacedTime(), resultTransaction.getPlacedTime());
		assertEquals(value.getOriginatingCountry(), resultTransaction.getOriginatingCountry());
	}

	// TODO test exceptions...
	// test different content type, encoding, date timezone
}
