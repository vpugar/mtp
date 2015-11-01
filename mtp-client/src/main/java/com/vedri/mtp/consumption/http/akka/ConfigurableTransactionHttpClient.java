package com.vedri.mtp.consumption.http.akka;

import java.util.List;
import java.util.Map;
import java.util.Random;

import com.vedri.mtp.core.CoreProperties;
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
import com.vedri.mtp.core.country.Country;
import com.vedri.mtp.core.country.CountryManager;
import com.vedri.mtp.core.rate.Rate;
import com.vedri.mtp.core.rate.RateCalculator;
import com.vedri.mtp.core.support.cassandra.CassandraConfiguration;
import com.vedri.mtp.core.support.http.AkkaHttpClient1;
import com.vedri.mtp.core.support.json.TransactionJacksonConfiguration;

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

		@Parameter(required = false, names = "repeatCount")
		private int repeatCount = 10;

		@Parameter(required = false, names = "pauseMs")
		private int pauseMs = 60000;
	}

	private Args argsConf;
	private Random random = new Random(System.currentTimeMillis());

	private CountryManager countryManager;
	private ObjectMapper transactionObjectMapper;
	private RateCalculator rateCalculator;

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
		final List<String> currencies = countryManager.getCurrencies();

		final String currencyFrom = currencies.get(random.nextInt(currencies.size()));
		final String currencyTo = currencies.get(random.nextInt(currencies.size()));
		final Rate rate = rateCalculator.sellRate(currencyFrom, currencyTo, new LocalDate());
		final double amountSell = random.nextDouble() * 10000;
		final Country country = countries.get(random.nextInt(countries.size()));

		Map<String, Object> data = Maps.newHashMap();
		data.put("userId", String.valueOf(random.nextInt(1000)));
		data.put("currencyFrom", currencyFrom);
		data.put("currencyTo", currencyTo);
		data.put("amountSell", amountSell);
		data.put("amountBuy", amountSell * rate.getCfRate().doubleValue());
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

		return super.startApplication(args, customProfile);
	}

	@Override
	protected void doStart(ApplicationContext context) throws Exception {

		transactionObjectMapper = context.getBean("transactionObjectMapper", ObjectMapper.class);
		countryManager = context.getBean(CountryManager.class);
		rateCalculator = context.getBean("cachingRateCalculator", RateCalculator.class);

		super.doStart(context);
	}

	public static void main(String[] args) throws Exception {
		new ConfigurableTransactionHttpClient().startApplication(args, "nodeClient0");
	}
}
