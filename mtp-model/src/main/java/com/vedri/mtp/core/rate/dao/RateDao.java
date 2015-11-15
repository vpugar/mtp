package com.vedri.mtp.core.rate.dao;

import com.vedri.mtp.core.rate.Rate;

/**
 * Rate DAO.
 */
public interface RateDao {

	Rate save(final Rate rate);

	Rate load(final Rate.Key key);

}
