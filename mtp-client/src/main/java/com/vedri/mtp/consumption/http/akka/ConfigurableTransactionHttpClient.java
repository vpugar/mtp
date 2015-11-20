package com.vedri.mtp.consumption.http.akka;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import com.vedri.mtp.core.MtpConstants;
import com.vedri.mtp.core.currency.Currency;
import com.vedri.mtp.core.currency.CurrencyManager;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.vedri.mtp.core.CoreConfig;
import com.vedri.mtp.core.CoreProperties;
import com.vedri.mtp.core.country.Country;
import com.vedri.mtp.core.country.CountryManager;
import com.vedri.mtp.core.rate.Rate;
import com.vedri.mtp.core.rate.RateCalculator;
import com.vedri.mtp.core.support.cassandra.CassandraConfiguration;
import com.vedri.mtp.core.support.http.AkkaHttpClient1;
import com.vedri.mtp.core.support.json.TransactionJacksonConfiguration;

/**
 * Configurable HTTP client that sends data to the mtp-consumption application.
 * <p>
 *    Application parameters:
 *    <ul>
 *        <li>--repeatCount - number of requests, default 10</li>
 *        <li>--pauseMs - pause between each request in ms, default 60000 ms</li>
 *        <li>--url - URL of mtp-consumption, default http://localhost:9090/transactions</li>
 *    </ul>
 *    Client application generates:
 *    <ul>
 *        <li>userId - number from 0 to 1000</li>
 *        <li>currencyFrom - </li>
 *        <li>currencyTo - </li>
 *        <li>amountSell - </li>
 *        <li>amountBuy - </li>
 *        <li>rate - rate for currencyFrom, currencyTo. If there is no real rate, it is generated randomly.</li>
 *        <li>timePlaced - current timestamp minus maximally 30s</li>
 *        <li>originatingCountry - random country from list of countries</li>
 *    </ul>
 * </p>
 */
public class ConfigurableTransactionHttpClient extends TransactionHttpClient {

	@ComponentScan(basePackages = {
			"com.vedri.mtp.core.country",
			"com.vedri.mtp.core.rate"
	})
	@Configuration
	public static class ConfigurableTransactionHttpClientConfiguration extends TransactionHttpClientConfiguration {

		@Bean
		CoreProperties.Cassandra cassandra() {
			return coreProperties.getCassandra();
		}
	}

	static class Args {

		@Parameter(required = false, names = { "-r", "--repeatCount" })
		private int repeatCount = 10;

		@Parameter(required = false, names = { "-p", "--pauseMs" })
		private int pauseMs = 60000;

		@Parameter(required = false, names = { "-u", "--url" })
		private String url = "http://localhost:9090/transactions";
	}

	private Args argsConf;
	private Random random = new Random(System.currentTimeMillis());

	private CountryManager countryManager;
	private CurrencyManager currencyManager;
	private ObjectMapper transactionObjectMapper;
	private RateCalculator rateCalculator;
	private final DecimalFormat decimalFormat2;

	public ConfigurableTransactionHttpClient() {
		decimalFormat2 = new DecimalFormat(MtpConstants.CURRENCY_FORMAT, MtpConstants.DEFAULT_FORMAT_SYMBOLS);
		decimalFormat2.setRoundingMode(RoundingMode.HALF_UP);
	}

	@Override
	protected Class[] getConfigs() {
		return new Class[] {
				CoreConfig.class, ClientAkkConfiguration.class, TransactionHttpClientConfiguration.class,
				TransactionJacksonConfiguration.class, ConfigurableTransactionHttpClientConfiguration.class,
				CassandraConfiguration.class
		};
	}

	@Override
	protected String doCreateRequest() throws Exception {

		final List<Country> countries = countryManager.getCountries();
		final List<Currency> currencies = currencyManager.getCurrencies();

		final Currency currencyFrom = currencies.get(random.nextInt(currencies.size()));
		final Currency currencyTo = currencies.get(random.nextInt(currencies.size()));
		final Rate rate = rateCalculator.sellRate(currencyFrom.getCode(), currencyTo.getCode(), new LocalDate());
		final BigDecimal amountSell = new BigDecimal(decimalFormat2.format(random.nextDouble() * 10000));
		final Country country = countries.get(random.nextInt(countries.size()));

		Map<String, Object> data = Maps.newHashMap();
		data.put("userId", String.valueOf(random.nextInt(1000)));
		data.put("currencyFrom", currencyFrom.getCode());
		data.put("currencyTo", currencyTo.getCode());
		data.put("amountSell", amountSell);
		data.put("amountBuy", new BigDecimal(decimalFormat2.format(rate.getCfRate().multiply(amountSell))));
		data.put("rate", rate.getCfRate());
		data.put("timePlaced", new DateTime().minus(random.nextInt(30000)));
		data.put("originatingCountry", country.getCca2());

		return transactionObjectMapper.writeValueAsString(data);
	}

	@Override
	public void send(AkkaHttpClient1 httpClient) throws Exception {
		for (int i = 0; i < argsConf.repeatCount; i++) {
			super.send(httpClient);
			Thread.sleep(argsConf.pauseMs);
		}
	}

	@Override
	public ApplicationContext startApplication(String[] args, String... customProfile) throws Exception {
		argsConf = new Args();
		new JCommander(argsConf, args);

		setUrl(argsConf.url);

		return super.startApplication(args, customProfile);
	}

	@Override
	protected void doStart(ApplicationContext context) throws Exception {

		transactionObjectMapper = context.getBean("transactionObjectMapper", ObjectMapper.class);
		countryManager = context.getBean(CountryManager.class);
		currencyManager = context.getBean(CurrencyManager.class);
		rateCalculator = context.getBean("cachingRateCalculator", RateCalculator.class);

		super.doStart(context);
	}

	public static void main(String[] args) throws Exception {
		new ConfigurableTransactionHttpClient().startApplication(args, "nodeClient0");
	}
}
