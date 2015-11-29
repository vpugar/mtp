package com.vedri.mtp.frontend.web.rest.transaction;

import java.net.URISyntaxException;
import java.util.List;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.codahale.metrics.annotation.Timed;
import com.vedri.mtp.core.transaction.Transaction;
import com.vedri.mtp.core.transaction.dao.TransactionDao;
import com.vedri.mtp.frontend.support.web.PaginationUtil;

@Slf4j
@RestController
@RequestMapping("/api")
public class TransactionResource {

	private final TransactionDao transactionDao;

	@Autowired
	public TransactionResource(TransactionDao transactionDao) {
		this.transactionDao = transactionDao;
	}

	/**
	 * GET /users -> get all users.
	 */
	@RequestMapping(value = "/transactions", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<Transaction>> getAllTransactions(
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime timeReceivedFrom,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime timeReceivedTo,
			@RequestParam(required = false) String userId,
			@RequestParam(required = false) String currencyFrom,
			@RequestParam(required = false) String currencyTo,
			@RequestParam(required = false) String originatingCountry,
			@RequestParam(value = "page", required = false, defaultValue = "0") Integer offset,
			@RequestParam(value = "per_page", required = false, defaultValue = "20") Integer limit)
					throws URISyntaxException {

		if (Strings.isNullOrEmpty(userId)) {
			userId = null;
		}
		if (Strings.isNullOrEmpty(currencyFrom)) {
			currencyFrom = null;
		}
		if (Strings.isNullOrEmpty(currencyTo)) {
			currencyTo = null;
		}
		if (Strings.isNullOrEmpty(originatingCountry)) {
			originatingCountry = null;
		}

		final List<Transaction> transactions = transactionDao.loadAll(timeReceivedFrom, timeReceivedTo, userId,
				currencyFrom, currencyTo, originatingCountry, (offset - 1) * limit, limit);

		final HttpHeaders httpHeaders = PaginationUtil
				.generatePaginationHttpHeaders("/api/transactions", offset, limit);

		return new ResponseEntity<>(transactions, httpHeaders, HttpStatus.OK);
	}

	@RequestMapping(value = "/transactions/{transactionId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Transaction> getAll(@PathVariable String transactionId)
			throws URISyntaxException {

		final Transaction transaction = transactionDao.load(transactionId);

		final HttpHeaders httpHeaders = PaginationUtil
				.generatePaginationHttpHeaders("/api/transactions/" + transactionId, null, null);

		return new ResponseEntity<>(transaction, httpHeaders, HttpStatus.OK);
	}

}
