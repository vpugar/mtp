package com.vedri.mtp.core.rate;

import com.vedri.mtp.core.rate.dao.RateDao;
import org.joda.time.LocalDate;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.math.BigDecimal;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class CachingRateCalculatorTest {

    @Mock
    private RateDao rateDao;
    @Mock
    private RateCalculator rateCalculator;
    private CachingRateCalculator cachingRateCalculator;

    @BeforeMethod
    public void init() {
        MockitoAnnotations.initMocks(this);

        Mockito.reset(rateDao, rateCalculator);

        cachingRateCalculator = new CachingRateCalculator(rateDao, rateCalculator);
        cachingRateCalculator.setUseSourceRateCalculator(true);
        cachingRateCalculator.setUseRandomResultsForRate(true);
    }

    @Test
    public void sellRateWithDelegate() throws NoRateException {

        final Rate.Key key = new Rate.Key("F1", "T1", new LocalDate());
        final Rate result = new Rate(key, new BigDecimal(100), new BigDecimal(200));

        Mockito.when(rateDao.load(key)).thenReturn(null);
        Mockito.when(rateCalculator.sellRate(key)).thenReturn(result);

        final Rate rate1 = cachingRateCalculator.sellRate(key);
        // this must be cached
        final Rate rate2 = cachingRateCalculator.sellRate(key);

        Assert.assertNotNull(rate1);
        Assert.assertNotNull(rate1.getBankRate());
        Assert.assertNotNull(rate1.getCfRate());
        Assert.assertEquals(rate1.getKey(), key);
        Assert.assertEquals(rate1, rate2);

        verify(rateDao, times(1)).load(key);
        verify(rateCalculator, times(1)).sellRate(key);
        verify(rateDao, times(1)).save(result);
        verifyNoMoreInteractions(rateCalculator, rateDao);
    }

    @Test
    public void sellRateWithDao() throws NoRateException {

        final Rate.Key key = new Rate.Key("F2", "T2", new LocalDate());
        final Rate result = new Rate(key, new BigDecimal(100), new BigDecimal(200));

        Mockito.when(rateDao.load(key)).thenReturn(result);
        Mockito.when(rateCalculator.sellRate(key)).thenReturn(null);

        final Rate rate1 = cachingRateCalculator.sellRate(key);
        // this must be cached
        final Rate rate2 = cachingRateCalculator.sellRate(key);

        Assert.assertNotNull(rate1);
        Assert.assertNotNull(rate1.getBankRate());
        Assert.assertNotNull(rate1.getCfRate());
        Assert.assertEquals(rate1.getKey(), key);
        Assert.assertEquals(rate1.getBankRate(), result.getBankRate());
        Assert.assertEquals(rate1.getCfRate(), result.getCfRate());
        Assert.assertEquals(rate1, rate2);

        verify(rateDao, times(1)).load(key);
        verifyNoMoreInteractions(rateCalculator, rateDao);
    }

    @Test
    public void sellRateWithRandom() throws NoRateException {

        final Rate.Key key = new Rate.Key("F3", "T3", new LocalDate());

        Mockito.when(rateDao.load(key)).thenReturn(null);
        Mockito.when(rateCalculator.sellRate(key)).thenReturn(null);

        final Rate rate1 = cachingRateCalculator.sellRate(key);
        // this must be cached
        final Rate rate2 = cachingRateCalculator.sellRate(key);

        Assert.assertNotNull(rate1);
        Assert.assertNotNull(rate1.getBankRate());
        Assert.assertNotNull(rate1.getCfRate());
        Assert.assertEquals(rate1.getKey(), key);
        Assert.assertEquals(rate1, rate2);

        verify(rateDao, times(1)).load(key);
        verify(rateDao, times(1)).save(any());
        verify(rateCalculator, times(1)).sellRate(key);
        verifyNoMoreInteractions(rateCalculator, rateDao);
    }
}
