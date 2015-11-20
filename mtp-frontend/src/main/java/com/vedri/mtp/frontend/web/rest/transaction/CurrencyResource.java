package com.vedri.mtp.frontend.web.rest.transaction;

import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.vedri.mtp.core.currency.Currency;
import com.vedri.mtp.core.currency.CurrencyManager;

@Slf4j
@RestController
@RequestMapping("/api")
public class CurrencyResource {

	private final CurrencyManager currencyManager;

	@Autowired
	public CurrencyResource(CurrencyManager currencyManager) {
		this.currencyManager = currencyManager;
	}

	@RequestMapping(value = "/currencies", method = RequestMethod.GET, params = "action=filter", produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<Currency>> getWithFilter(
			@RequestParam(value = "per_page", required = false, defaultValue = "10") int limit,
			@RequestParam(required = false) String filter) throws URISyntaxException {
		String filterUpper = filter.toUpperCase();
		final List<Currency> list = currencyManager.getCurrencies().stream()
				.filter(country -> country.getCode().contains(filterUpper))
				.limit(limit)
				.collect(Collectors.toList());
		return new ResponseEntity<>(list, HttpStatus.OK);
	}
}
