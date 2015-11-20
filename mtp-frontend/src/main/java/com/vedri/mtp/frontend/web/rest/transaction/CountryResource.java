package com.vedri.mtp.frontend.web.rest.transaction;

import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

	@RequestMapping(value = "/countries", method = RequestMethod.GET, params = "action=filter", produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<Country>> getWithFilter(
			@RequestParam(value = "per_page", required = false, defaultValue = "10") int limit,
			@RequestParam(required = false) String filter) throws URISyntaxException {
		String filterUpper = filter.toUpperCase();
		final List<Country> list = countryManager.getCountries().stream()
				.filter(country -> country.getCca2().contains(filterUpper) || country.getOfficialName().toUpperCase().startsWith(filterUpper))
				.limit(limit)
				.collect(Collectors.toList());
		return new ResponseEntity<>(list, HttpStatus.OK);
	}
}
