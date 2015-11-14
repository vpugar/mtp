package com.vedri.mtp.frontend.web.rest.transaction;

import java.net.URISyntaxException;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.vedri.mtp.core.country.Country;
import com.vedri.mtp.core.country.CountryManager;
import com.vedri.mtp.frontend.support.web.PaginationUtil;

@Slf4j
@RestController
@RequestMapping("/api")
public class CountryResource {

	private final CountryManager countryManager;

	@Autowired
	public CountryResource(CountryManager countryManager) {
		this.countryManager = countryManager;
	}

	@RequestMapping(value = "/countries/{cca2}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Country> getAll(@PathVariable String cca2)
			throws URISyntaxException {

		final Country country = countryManager.getCountryFromCca2(cca2);

		final HttpHeaders httpHeaders = PaginationUtil
				.generatePaginationHttpHeaders("/api/countries/" + cca2, null, null);

		return new ResponseEntity<>(country, httpHeaders, HttpStatus.OK);
	}
}
