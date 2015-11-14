package com.vedri.mtp.core.rate;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Map;
import java.util.Random;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import com.vedri.mtp.core.rate.dao.RateDao;

@Service
@Slf4j
public class CachingRateCalculator implements RateCalculator {

	private final RateDao rateDao;
	private final RateCalculator sourceRateCalculator;
	private final Random random = new Random(System.currentTimeMillis());
	private boolean useRandomResultsForRate = true;

	private final Map<Rate.Key, Rate> simpleCache = new ConcurrentLinkedHashMap.Builder<Rate.Key, Rate>()
			.initialCapacity(500)
			.maximumWeightedCapacity(2000).build();

	@Autowired
	public CachingRateCalculator(RateDao rateDao, RateCalculator sourceRateCalculator) {
		this.rateDao = rateDao;
		this.sourceRateCalculator = sourceRateCalculator;
	}

	@Override
	public Rate sellRate(Rate.Key key) throws NoRateException {

		Rate rate = fromCache(key);

		if (rate == null) {
			rate = rateDao.load(key);
			putInCache(rate);
		}

		if (rate == null) {
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
			final BigDecimal rateValue = new BigDecimal(random.nextDouble(), new MathContext(4));
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
