package com.vedri.mtp.core.rate.cf;

import java.io.IOException;
import java.math.BigDecimal;

import com.vedri.mtp.core.rate.NoRateException;
import org.joda.time.LocalDate;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.vedri.mtp.core.rate.Rate;
import com.vedri.mtp.core.support.http.HttpComponentsHttpClient;
import com.vedri.mtp.core.support.json.JacksonConfiguration;

public class CfRateCalculatorTest {

	@Mock
	private HttpComponentsHttpClient httpComponentsHttpClient;

	private CfRateCalculator cfRateCalculator;

	@BeforeMethod
	public void init() {

		MockitoAnnotations.initMocks(this);

		JacksonConfiguration jacksonConfiguration = new JacksonConfiguration();

		cfRateCalculator = new CfRateCalculator(jacksonConfiguration.objectMapper());
		cfRateCalculator.setHttpClient(httpComponentsHttpClient);
		cfRateCalculator.init();
	}

	@Test
	public void sellRateBasic() throws Exception {

        String data = "{\n" +
                "\"calculator\": {\n" +
                "\"currencyFrom\": \"EUR\",\n" +
                "\"currencyTo\": \"GBP\",\n" +
                "\"cfTransferFee\": \"3.00\",\n" +
                "\"bankTransferFee\": \"25.00\",\n" +
                "\"youExchangeCf\": \"97.00\",\n" +
                "\"youExchangeBank\": \"75.00\",\n" +
                "\"youSave\": \"16.91\",\n" +
                "\"cfRate\": \"0.7024\",\n" +
                "\"bankRate\": \"0.6829\",\n" +
                "\"cfBuyAmount\": \"68.13\",\n" +
                "\"bankBuyAmount\": \"51.22\"\n" +
                "}\n" +
                "}";

        Mockito.when(httpComponentsHttpClient.get(Matchers.any(), Matchers.any())).thenReturn(data);

        final Rate.Key key = new Rate.Key("EUR", "GBP", new LocalDate());
        final Rate result = new Rate(key, new BigDecimal("0.7024"), new BigDecimal("0.6829"));

        final Rate rate = cfRateCalculator.sellRate(key);

        Assert.assertNotNull(rate);
        Assert.assertEquals(rate, result);
        Assert.assertEquals(rate.getBankRate(), result.getBankRate());
        Assert.assertEquals(rate.getCfRate(), result.getCfRate());
        Assert.assertEquals(rate.getKey(), result.getKey());
	}

	@Test
	public void sellRateBadRequest() throws Exception {

		String data = "{\n" +
				"\"calculator\": {\n" +
				"\"errorMessage\": \"Bad request\"\n" +
				"}\n" +
				"}";

		Mockito.when(httpComponentsHttpClient.get(Matchers.any(), Matchers.any())).thenReturn(data);

		final Rate.Key key = new Rate.Key("EUR", "GBP", new LocalDate());

		try {
			cfRateCalculator.sellRate(key);
			Assert.fail();
		} catch (NoRateException e) {
			// expected
		}
	}

	@Test
	public void sellRateNoData() throws Exception {

		String data = "{}";

		Mockito.when(httpComponentsHttpClient.get(Matchers.any(), Matchers.any())).thenReturn(data);

		final Rate.Key key = new Rate.Key("EUR", "GBP", new LocalDate());

		try {
			cfRateCalculator.sellRate(key);
			Assert.fail();
		} catch (NoRateException e) {
			// expected
		}
	}

	@Test
	public void sellRateIOException() throws Exception {

		Mockito.when(httpComponentsHttpClient.get(Matchers.any(), Matchers.any())).thenThrow(new IOException());

		final Rate.Key key = new Rate.Key("EUR", "GBP", new LocalDate());

		try {
			cfRateCalculator.sellRate(key);
			Assert.fail();
		} catch (IllegalStateException e) {
//			 expected
		}
	}
}
