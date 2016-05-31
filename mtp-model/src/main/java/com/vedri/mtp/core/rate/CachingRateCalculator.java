package com.vedri.mtp.core.rate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import com.vedri.mtp.core.MtpConstants;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import com.vedri.mtp.core.rate.dao.RateDao;

/**
 * Caching implementation of rate calculator. It loads rate in next order with fallback to next step:
 * <ul>
 * <li>from Cassandra DB</li>
 * <li>from sourceRateCalculator implementation</li>
 * <li>randomly generates rate</li>
 * </ul>
 * It stores resolved rate in Cassandra DB and in memory for next use.
 */
@Service
@Slf4j
public class CachingRateCalculator implements RateCalculator {

	private final RateDao rateDao;
	private final RateCalculator sourceRateCalculator;
	private final Random random = new Random(System.currentTimeMillis());
	private boolean useRandomResultsForRate = true;
	private boolean useSourceRateCalculator = false;
	private final DecimalFormat decimalFormat;

	private final Map<Rate.Key, Rate> simpleCache = new ConcurrentLinkedHashMap.Builder<Rate.Key, Rate>()
			.initialCapacity(500)
			.maximumWeightedCapacity(2000).build();

	@Autowired
	public CachingRateCalculator(RateDao rateDao, RateCalculator sourceRateCalculator) {
		this.rateDao = rateDao;
		this.sourceRateCalculator = sourceRateCalculator;
		decimalFormat = new DecimalFormat(MtpConstants.RATE_FORMAT, MtpConstants.DEFAULT_FORMAT_SYMBOLS);
		decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
	}

	public void setUseRandomResultsForRate(boolean useRandomResultsForRate) {
		this.useRandomResultsForRate = useRandomResultsForRate;
	}

	public void setUseSourceRateCalculator(boolean useSourceRateCalculator) {
		this.useSourceRateCalculator = useSourceRateCalculator;
	}

	@Override
	public Rate sellRate(Rate.Key key) throws NoRateException {

		Rate rate = fromCache(key);

		if (rate == null) {
			rate = rateDao.load(key);
			putInCache(rate);
		}

		if (useSourceRateCalculator && rate == null) {
			try {
				rate = sourceRateCalculator.sellRate(key);
				storeInDaoAndCache(rate);
			}
			catch (NoRateException e) {
				if (useRandomResultsForRate) {
					log.warn(e.getMessage());
				}
				else {
					throw e;
				}
			}
		}

		if (useRandomResultsForRate && rate == null) {
			log.warn("Using random rate for example purposes");
			final double num = random.nextDouble() * 100;
			BigDecimal rateValue;
			synchronized (decimalFormat) {
				rateValue = new BigDecimal(decimalFormat.format(num));
			}
			rate = new Rate(key, rateValue, rateValue.add(BigDecimal.ONE));
			storeInDaoAndCache(rate);
		}
		else if (rate == null) {
			throw new NoRateException(key);
		}

		log.info("Using rate {}", rate);

		return rate;
	}

	private void storeInDaoAndCache(Rate rate) {
		if (rate != null) {
			rateDao.save(rate);
			putInCache(rate);
		}
	}

	private void putInCache(Rate rate) {
		if (rate != null) {
			simpleCache.put(rate.getKey(), rate);
		}
	}

	private Rate fromCache(Rate.Key key) {
		return simpleCache.get(key);
	}
}
