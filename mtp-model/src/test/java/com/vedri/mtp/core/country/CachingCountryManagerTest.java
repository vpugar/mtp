package com.vedri.mtp.core.country;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Set;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.vedri.mtp.core.country.dao.CountryDao;
import com.vedri.mtp.core.rate.NoRateException;

public class CachingCountryManagerTest {

	@Mock
	private CountryDao countryDao;
	private CachingCountryManager cachingCountryManager;
	private Country country1;

	@BeforeClass
	public void init() {

		MockitoAnnotations.initMocks(this);

		Mockito.reset(countryDao);

		country1 = new Country("c1", "c11", "name1", "offname1", Sets.newHashSet("cu1", "cu2"));

		Mockito.when(countryDao.loadAll())
				.thenReturn(Lists.newArrayList(
						country1, new Country("c2", "c22", "name2", "offname2", Sets.newHashSet("cu2", "cu3"))));

		cachingCountryManager = new CachingCountryManager(countryDao);
	}

	@Test
	public void testCountryManagerInit() {

		cachingCountryManager.init();
		verify(countryDao, times(1)).loadAll();
		Assert.assertEquals(cachingCountryManager.getCountries().size(), 2);
	}

	@Test(dependsOnMethods = "testCountryManagerInit")
	public void getCountriesFromCurrency() throws NoRateException {
		final Set<Country> result0 = cachingCountryManager.getCountriesFromCurrency("cu0");
		Assert.assertEquals(result0.size(), 0);

		final Set<Country> result1 = cachingCountryManager.getCountriesFromCurrency("cu1");
		Assert.assertEquals(result1.size(), 1);

		final Set<Country> result2 = cachingCountryManager.getCountriesFromCurrency("cu2");
		Assert.assertEquals(result2.size(), 2);
	}

	@Test(dependsOnMethods = "testCountryManagerInit")
	public void getCountryFromCca2() throws NoRateException {
		final Country result1 = cachingCountryManager.getCountryFromCca2("c1");
		Assert.assertNotNull(result1);
		Assert.assertEquals(result1, country1);

		final Country noResult = cachingCountryManager.getCountryFromCca2("cn");
		Assert.assertNull(noResult);
	}
}
