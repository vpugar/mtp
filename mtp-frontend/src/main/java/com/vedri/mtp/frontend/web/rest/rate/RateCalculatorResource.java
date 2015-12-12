package com.vedri.mtp.frontend.web.rest.rate;

import lombok.extern.slf4j.Slf4j;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vedri.mtp.core.rate.NoRateException;
import com.vedri.mtp.core.rate.Rate;
import com.vedri.mtp.core.rate.RateCalculator;
import com.vedri.mtp.core.rate.cf.CfRate;

@Slf4j
@RestController
@RequestMapping("/test")
public class RateCalculatorResource {

	private final ObjectMapper objectMapper;
	private final RateCalculator rateCalculator;

	@Autowired
	public RateCalculatorResource(ObjectMapper objectMapper,
                                  @Qualifier("cachingRateCalculator") RateCalculator rateCalculator) {
		this.objectMapper = objectMapper;
		this.rateCalculator = rateCalculator;
	}

	@RequestMapping(value = "/rates", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<ObjectNode> getRates(
			@RequestParam String currencyfrom,
			@RequestParam String currencyto) throws NoRateException {

		final Rate rate = rateCalculator.sellRate(currencyfrom, currencyto, LocalDate.now());
		final ObjectNode objectNode = objectMapper.createObjectNode();
		objectNode.putPOJO("calculator", CfRate.fromRate(rate));

		return new ResponseEntity<>(objectNode, HttpStatus.OK);
	}
}
