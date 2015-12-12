package com.vedri.mtp.consumption.http.akka;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.vedri.mtp.core.MtpConstants;
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
import org.testng.annotations.*;

import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import akka.actor.ActorSystem;
import akka.http.javadsl.model.*;

import com.beust.jcommander.internal.Lists;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.vedri.mtp.consumption.ConsumptionTestConfig;
import com.vedri.mtp.consumption.MtpConsumptionConstants;
import com.vedri.mtp.consumption.transaction.TransactionManager;
import com.vedri.mtp.consumption.transaction.ValidationFailedException;
import com.vedri.mtp.core.CoreConfig;
import com.vedri.mtp.core.support.akka.AkkaConfiguration;
import com.vedri.mtp.core.support.http.AkkaHttpClient1;
import com.vedri.mtp.core.support.json.JacksonConfiguration;
import com.vedri.mtp.core.support.json.TransactionJacksonConfiguration;
import com.vedri.mtp.core.transaction.Transaction;
import com.vedri.mtp.core.transaction.aggregation.TransactionValidationStatus;

@ContextConfiguration(loader = SpringockitoAnnotatedContextLoader.class, classes = {
		ConsumptionTestConfig.class, AkkaHttpServerTest.class, JacksonConfiguration.class, CoreConfig.class,
		TransactionJacksonConfiguration.class, AkkaConfiguration.class, AkkaHttpServer.class,

})
@TestPropertySource(properties = {
		"mtp.consumption.cluster.nodeName=nodeConsumption0",
		"mtp.consumption.akka.akkaSystemName=MtpConsumption",
		"mtp.consumption.akka.logConfiguration=false",
		"mtp.consumption.httpServer.bindHost=localhost",
		"mtp.consumption.httpServer.bindPort=9090",
		"mtp.consumption.httpServer.publicProtocol=http",
		"mtp.consumption.httpServer.publicHost=localhost",
		"mtp.consumption.httpServer.publicPort=9090"

})
public class AkkaHttpServerTest extends AbstractTestNGSpringContextTests {

	private static final class TimeOffset extends akka.http.scaladsl.model.headers.CustomHeader {

		private final String value;

		private TimeOffset(String value) {
			this.value = value;
		}

		@Override
		public String value() {
			return value;
		}

		@Override
		public String name() {
			return MtpConsumptionConstants.HTTP_HEADER_TIME_OFFSET;
		}

		@Override
		public String lowercaseName() {
			return MtpConsumptionConstants.HTTP_HEADER_TIME_OFFSET.toLowerCase();
		}
	};

	private static final String REQUEST = "{\n" +
			"     \"userId\": \"134256\",\n" +
			"     \"currencyFrom\": \"EUR\",\n" +
			"     \"currencyTo\": \"GBP\",\n" +
			"     \"amountSell\": 1000,\n" +
			"     \"amountBuy\": 747.10,\n" +
			"     \"rate\": 0.7471,\n" +
			"     \"timePlaced\" : \"24JAN15 10:27:44\",\n" +
			"     \"originatingCountry\": \"FR\"\n" +
			"}";

	@ReplaceWithMock
	@Autowired
	TransactionManager transactionManager;

	@Autowired
	private AkkaHttpServer akkaHttpServer;

	@Autowired
	private ActorSystem actorSystem;

	@Autowired
	@Qualifier("transactionObjectMapper")
	private ObjectMapper transactionObjectMapper;

	private AkkaHttpClient1 httpClient;

	private final Transaction resultTransaction = new Transaction(
			"p1", "t1", "134256", "EUR", "GBP", new BigDecimal("1000"), new BigDecimal("747.10"),
			new BigDecimal("1000"), new BigDecimal("0.7471"), new DateTime(2015, 1, 24, 10, 27, 44,
			MtpConstants.DEFAULT_TIME_ZONE),
			"FR", new DateTime(), "n1", TransactionValidationStatus.OK);

	private final Transaction resultTransactionWithOffsetMinus2 = new Transaction(
			"p1", "t1", "134256", "EUR", "GBP", new BigDecimal("1000"), new BigDecimal("747.10"),
			new BigDecimal("1000"), new BigDecimal("0.7471"),
			new DateTime(2015, 1, 24, 10, 27, 44, DateTimeZone.forOffsetHours(-2)),
			"FR", new DateTime(), "n1", TransactionValidationStatus.OK);

	private final Transaction resultTransactionWithOffsetPlus3 = new Transaction(
			"p1", "t1", "134256", "EUR", "GBP", new BigDecimal("1000"), new BigDecimal("747.10"),
			new BigDecimal("1000"), new BigDecimal("0.7471"),
			new DateTime(2015, 1, 24, 10, 27, 44, DateTimeZone.forOffsetHours(3)),
			"FR", new DateTime(), "n1", TransactionValidationStatus.OK);

	@BeforeClass
	public void init() throws Exception {
		akkaHttpServer.start();
		httpClient = new AkkaHttpClient1(actorSystem);
		httpClient.init();
	}

	@AfterClass
	public void destroy() throws Exception {
		httpClient.destroy();
		akkaHttpServer.stop();
	}

	@BeforeMethod
	public void initTest() {
	}

	@AfterMethod
	public void destroyTest() {
	}

	@Test
	public void basicTest() throws Exception {

		doPrepareFlow(resultTransaction, null);

		final ContentType contentType = ContentType.create(MediaTypes.APPLICATION_JSON, HttpCharsets.UTF_8);
		final HttpResponse response = doRequest(contentType, REQUEST);
		assertEquals(response.status(), StatusCodes.CREATED, "not created");

		final Transaction result = checkFlowAndGetResult();
		checkTransaction(result, resultTransaction);
	}

	@Test
	public void basicTestWithZoneOffset() throws Exception {

		doPrepareFlow(resultTransaction, null);

		final ContentType contentType = ContentType.create(MediaTypes.APPLICATION_JSON, HttpCharsets.UTF_8);
		final HttpResponse response = doRequest(contentType, REQUEST, new TimeOffset("-02:00"));
		assertEquals(response.status(), StatusCodes.CREATED, "not created");

		final Transaction result = checkFlowAndGetResult();
		checkTransaction(result, resultTransactionWithOffsetMinus2);
	}

	@DataProvider(name = "transactionData1")
	public Object[][] transactionData1() {
		return new String[][] {
				{ "noDataTest", "" },
				{ "notValidJsonTest", "{ \"json_is_not_valid: \"sss\" }" }
		};
	}

	@Test(dataProvider = "transactionData1")
	public void notValidJsonTest(String desc, String request) throws Exception {

		doPrepareFlow(resultTransaction, null);

		final ContentType contentType = ContentType.create(MediaTypes.APPLICATION_JSON, HttpCharsets.UTF_8);
		final HttpResponse response = doRequest(contentType, request);
		assertEquals(response.status(), StatusCodes.BAD_REQUEST);
	}

	@DataProvider(name = "transactionData2")
	public Object[][] transactionData2() {
		Map<String, Object> transactionData = Maps.newHashMap();
		transactionData.put("userId", resultTransaction.getUserId());
		transactionData.put("currencyFrom", resultTransaction.getCurrencyFrom());
		transactionData.put("currencyTo", resultTransaction.getCurrencyTo());
		transactionData.put("amountSell", resultTransaction.getAmountSell());
		transactionData.put("amountBuy", resultTransaction.getAmountBuy());
		transactionData.put("rate", resultTransaction.getRate());
		transactionData.put("timePlaced", resultTransaction.getPlacedTime());
		transactionData.put("originatingCountry", resultTransaction.getOriginatingCountry());

		List<Object[]> testData = Lists.newArrayList();

		{
			testData.add(new Object[] {
					"main", transactionData,
					ContentType.create(MediaTypes.APPLICATION_JSON, HttpCharsets.UTF_8), null,
					StatusCodes.CREATED, resultTransaction, null });
		}

		{
			testData.add(new Object[] {
					"application/octet_stream content type", transactionData,
					ContentType.create(MediaTypes.APPLICATION_OCTET_STREAM), null,
					StatusCodes.CREATED, resultTransaction,
					null });
		}

		{
			testData.add(new Object[] {
					"text/plain content type", transactionData,
					ContentType.create(MediaTypes.TEXT_PLAIN, HttpCharsets.UTF_8), null,
					StatusCodes.CREATED,
					resultTransaction,
					null });
		}

		{
			testData.add(new Object[] {
					"text/plain content type", transactionData,
					ContentType.create(MediaTypes.TEXT_PLAIN, HttpCharsets.US_ASCII), null,
					StatusCodes.CREATED,
					resultTransaction,
					null });
		}

		{
			testData.add(new Object[] {
					"zone offset +03:00", transactionData,
					ContentType.create(MediaTypes.TEXT_PLAIN, HttpCharsets.US_ASCII), "+03:00",
					StatusCodes.CREATED,
					resultTransactionWithOffsetPlus3,
					null });
		}

		{
			testData.add(new Object[] {
					"zone offset -02:00", transactionData,
					ContentType.create(MediaTypes.TEXT_PLAIN, HttpCharsets.US_ASCII), "-02:00",
					StatusCodes.CREATED,
					resultTransactionWithOffsetMinus2,
					null });
		}

		{
			testData.add(new Object[] {
					"zone offset -02", transactionData,
					ContentType.create(MediaTypes.TEXT_PLAIN, HttpCharsets.US_ASCII), "-02",
					StatusCodes.CREATED,
					resultTransactionWithOffsetMinus2,
					null });
		}

		{
			testData.add(new Object[] {
					"zone offset -2", transactionData,
					ContentType.create(MediaTypes.TEXT_PLAIN, HttpCharsets.US_ASCII), "-2",
					StatusCodes.BAD_REQUEST,
					null, null });
		}

		{
			testData.add(new Object[] {
					"null content type", transactionData, null, null, StatusCodes.CREATED, resultTransaction,
					null });
		}

		{
			testData.add(new Object[] {
					"time offset wrong format", transactionData,
					ContentType.create(MediaTypes.APPLICATION_JSON, HttpCharsets.UTF_8), "xxyyzz",
					StatusCodes.BAD_REQUEST, null, null });
		}

		{
			// timePlaced wrong format
			Map<String, Object> transactionDataClone = Maps.newHashMap(transactionData);
			transactionDataClone.put("timePlaced", "240115 10:27:44");
			testData.add(new Object[] {
					"timePlaced wrong format", transactionDataClone,
					ContentType.create(MediaTypes.APPLICATION_JSON, HttpCharsets.UTF_8), null,
					StatusCodes.BAD_REQUEST,
					null, null });
		}

		{
			// no userId
			Map<String, Object> transactionDataClone = Maps.newHashMap(transactionData);
			transactionDataClone.remove("userId");
			testData.add(new Object[] {
					"no userId", transactionDataClone,
					ContentType.create(MediaTypes.APPLICATION_JSON, HttpCharsets.UTF_8), null, StatusCodes.BAD_REQUEST,
					null, new ValidationFailedException("no userId") });
		}

		{
			// no currencyFrom
			Map<String, Object> transactionDataClone = Maps.newHashMap(transactionData);
			transactionDataClone.remove("currencyFrom");
			testData.add(new Object[] {
					"no currencyFrom", transactionDataClone,
					ContentType.create(MediaTypes.APPLICATION_JSON, HttpCharsets.UTF_8), null, StatusCodes.BAD_REQUEST,
					null, new ValidationFailedException("no currencyFrom") });
		}

		{
			// no currencyTo
			Map<String, Object> transactionDataClone = Maps.newHashMap(transactionData);
			transactionDataClone.remove("currencyTo");
			testData.add(new Object[] {
					"no currencyTo", transactionDataClone,
					ContentType.create(MediaTypes.APPLICATION_JSON, HttpCharsets.UTF_8), null, StatusCodes.BAD_REQUEST,
					null, new ValidationFailedException("no currencyTo") });
		}

		{
			// no amountSell
			Map<String, Object> transactionDataClone = Maps.newHashMap(transactionData);
			transactionDataClone.remove("amountSell");
			testData.add(new Object[] {
					"no amountSell", transactionDataClone,
					ContentType.create(MediaTypes.APPLICATION_JSON, HttpCharsets.UTF_8), null, StatusCodes.BAD_REQUEST,
					null, new ValidationFailedException("no amountSell") });
		}

		{
			// no amountBuy
			Map<String, Object> transactionDataClone = Maps.newHashMap(transactionData);
			transactionDataClone.remove("amountBuy");
			testData.add(new Object[] {
					"no amountBuy", transactionDataClone,
					ContentType.create(MediaTypes.APPLICATION_JSON, HttpCharsets.UTF_8), null, StatusCodes.BAD_REQUEST,
					null, new ValidationFailedException("no amountBuy") });
		}

		{
			// no rate
			Map<String, Object> transactionDataClone = Maps.newHashMap(transactionData);
			transactionDataClone.remove("rate");
			testData.add(new Object[] {
					"no rate", transactionDataClone,
					ContentType.create(MediaTypes.APPLICATION_JSON, HttpCharsets.UTF_8), null, StatusCodes.BAD_REQUEST,
					null, new ValidationFailedException("no rate") });
		}

		{
			// no timePlaced
			Map<String, Object> transactionDataClone = Maps.newHashMap(transactionData);
			transactionDataClone.remove("timePlaced");
			testData.add(new Object[] {
					"no timePlaced", transactionDataClone,
					ContentType.create(MediaTypes.APPLICATION_JSON, HttpCharsets.UTF_8), null, StatusCodes.BAD_REQUEST,
					null, new ValidationFailedException("no timePlaced") });
		}

		{
			// no originatingCountry
			Map<String, Object> transactionDataClone = Maps.newHashMap(transactionData);
			transactionDataClone.remove("originatingCountry");
			testData.add(new Object[] {
					"no originatingCountry", transactionDataClone,
					ContentType.create(MediaTypes.APPLICATION_JSON, HttpCharsets.UTF_8), null, StatusCodes.BAD_REQUEST,
					null, new ValidationFailedException("no originatingCountry") });
		}

		return testData.toArray(new Object[testData.size()][]);
	}

	@Test(dataProvider = "transactionData2")
	public void transactionDataTest(String desc, Map<String, Object> data, ContentType contentType, String timeOffset,
			StatusCode statusCode, Transaction expectedTransaction, Exception expectedException) throws Exception {

		doPrepareFlow(expectedTransaction, expectedException);

		final HttpResponse response = timeOffset != null
				? doRequest(contentType, transactionObjectMapper.writeValueAsString(data), new TimeOffset(timeOffset))
				: doRequest(contentType, transactionObjectMapper.writeValueAsString(data));
		assertEquals(response.status(), statusCode, "expected status " + statusCode);

		if (expectedTransaction != null || expectedException != null) {
			final Transaction result = checkFlowAndGetResult();
			checkTransaction(result, expectedTransaction);
		}
	}

	private Transaction checkFlowAndGetResult() throws Exception {
		ArgumentCaptor<Transaction> commandCaptor = ArgumentCaptor.forClass(Transaction.class);
		verify(transactionManager, times(1)).addTransaction(commandCaptor.capture());
		verifyNoMoreInteractions(transactionManager);

		return commandCaptor.getValue();
	}

	private void checkTransaction(Transaction actual, Transaction expected) {
		if (expected != null) {
			assertNotNull(actual);
			assertEquals(actual.getUserId(), expected.getUserId());
			assertEquals(actual.getCurrencyFrom(), expected.getCurrencyFrom());
			assertEquals(actual.getCurrencyTo(), expected.getCurrencyTo());
			assertEquals(actual.getAmountSell(), expected.getAmountSell());
			assertEquals(actual.getAmountBuy(), expected.getAmountBuy());
			assertEquals(actual.getRate(), expected.getRate());
			assertEquals(actual.getPlacedTime().getMillis(),
					expected.getPlacedTime().getMillis());
			assertEquals(actual.getPlacedTime(), expected.getPlacedTime());
			assertEquals(actual.getPlacedTime().withZone(MtpConstants.DEFAULT_TIME_ZONE),
					expected.getPlacedTime().withZone(MtpConstants.DEFAULT_TIME_ZONE));
			assertEquals(actual.getOriginatingCountry(), expected.getOriginatingCountry());
		}
	}

	private void doPrepareFlow(Transaction expected, Exception expectedException) throws Exception {
		reset(transactionManager);

		if (expected != null) {
			when(transactionManager.addTransaction(any(Transaction.class))).thenReturn(expected);
		}
		else if (expectedException != null) {
			when(transactionManager.addTransaction(any(Transaction.class))).thenThrow(expectedException);
		}
	}

	private HttpResponse doRequest(final ContentType contentType, String request, HttpHeader... headers)
			throws Exception {
		final Future<HttpResponse> post = headers != null
				? httpClient.post("http://localhost:9090/transactions", contentType, request, headers)
				: httpClient.post("http://localhost:9090/transactions", contentType, request);
		// objectMapper.writeValueAsString(requestTransaction)
		return Await.result(post, Duration.apply(10, TimeUnit.SECONDS));
	}
}
