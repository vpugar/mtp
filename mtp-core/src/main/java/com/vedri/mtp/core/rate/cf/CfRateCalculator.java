package com.vedri.mtp.core.rate.cf;

import java.util.HashMap;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.google.common.collect.Maps;
import com.vedri.mtp.core.rate.NoRateException;
import com.vedri.mtp.core.rate.Rate;
import com.vedri.mtp.core.rate.RateCalculator;
import com.vedri.mtp.core.support.http.HttpComponentsHttpClient;

/**
 * CF implementation of currency calculator based on https://app.currencyfair.com/api/fleece.
 */
@Service
@Slf4j
public class CfRateCalculator implements RateCalculator {

	private final ObjectMapper objectMapper;
	private ObjectReader reader;
	private HttpComponentsHttpClient httpClient;
	private boolean externalClient = false;

	@Autowired
	public CfRateCalculator(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@PostConstruct
	public void init() {
		reader = objectMapper.reader(CfRate.class);
		if (!externalClient) {
			httpClient = new HttpComponentsHttpClient();
			httpClient.init();
		}
	}

	@PreDestroy
	public void destroy() throws Exception {
		if (!externalClient) {
			httpClient.destroy();
		}
	}

	public void setHttpClient(HttpComponentsHttpClient httpClient) {
		this.httpClient = httpClient;
		this.externalClient = true;
	}

	@Override
	public Rate sellRate(final Rate.Key key) throws NoRateException {
		log.debug("Getting rate for key {}", key);

		final HashMap<String, String> map = Maps.newHashMap();
		map.put("amount", "100");
		map.put("currencyfrom", key.getCurrencyFrom());
		map.put("currencyto", key.getCurrencyTo());
		map.put("buysell", "SELL");

		try {
			final String data = httpClient.get("https://app.currencyfair.com/api/fleece", map);
			if (data == null) {
				throw new NoRateException(key);
			}

			final JsonNode jsonNode = objectMapper.readTree(data);

			final JsonNode calculatorJsonNode = jsonNode.get("calculator");
			if (calculatorJsonNode == null) {
				throw new NoRateException(key, "Unknown response, no calculator object");
			}

			final JsonNode errorMessage = calculatorJsonNode.get("errorMessage");
			if (errorMessage != null) {
				throw new NoRateException(key, "CF error message: " + errorMessage.textValue());
			}

			final CfRate cfRate = reader.readValue(calculatorJsonNode);
			if (cfRate == null) {
				throw new NoRateException(key);
			}
			return cfRate.toRate();
		}
		catch (NoRateException e) {
			throw e;
		}
		catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}
}
