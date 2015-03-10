package org.hibernate.cache.ehcache.internal.strategy;

import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.NonstopConfiguration;
import net.sf.ehcache.config.TerracottaConfiguration;

import org.hibernate.cache.spi.access.AccessType;

import org.junit.Assert;
import org.junit.Test;

public class EhcacheAccessStrategyFactoryImplTest {
	@Test
	public void testEventualAccessStrategyChecks() throws Exception {
		EhcacheAccessStrategyFactoryImpl accessStrategyFactory = new EhcacheAccessStrategyFactoryImpl( false, false );
		CacheConfiguration configuration = new CacheConfiguration( "test", 1 ).terracotta(
				new TerracottaConfiguration().consistency(
						TerracottaConfiguration.Consistency.EVENTUAL
				)
		);
		
		accessStrategyFactory.checkAccessTypeCompatibility( AccessType.READ_ONLY,  configuration);
		accessStrategyFactory.checkAccessTypeCompatibility( AccessType.NONSTRICT_READ_WRITE, configuration );

		try {
			accessStrategyFactory.checkAccessTypeCompatibility( AccessType.READ_WRITE, configuration );
			Assert.fail("Expected " + AccessType.READ_WRITE + " with eventual to fail");
		}
		catch (IllegalArgumentException e) {
			// expected 
		}
		
		try {
			accessStrategyFactory.checkAccessTypeCompatibility( AccessType.TRANSACTIONAL, configuration );
			Assert.fail("Expected " + AccessType.TRANSACTIONAL + " with eventual to fail");
		} catch (IllegalArgumentException e) {
			// expected 
		}
	}

	@Test
	public void testNonStopAccessStrategyChecks() throws Exception {
		EhcacheAccessStrategyFactoryImpl accessStrategyFactory = new EhcacheAccessStrategyFactoryImpl( false, false );
		CacheConfiguration configuration = new CacheConfiguration( "test", 1 ).terracotta(
				new TerracottaConfiguration().nonstop( new NonstopConfiguration().enabled( true ) )
		);

		accessStrategyFactory.checkAccessTypeCompatibility( AccessType.READ_ONLY,  configuration);
		accessStrategyFactory.checkAccessTypeCompatibility( AccessType.NONSTRICT_READ_WRITE, configuration );

		try {
			accessStrategyFactory.checkAccessTypeCompatibility( AccessType.READ_WRITE, configuration );
			Assert.fail("Expected " + AccessType.READ_WRITE + " with nonstop to fail");
		}
		catch (IllegalArgumentException e) {
			// expected
		}

		try {
			accessStrategyFactory.checkAccessTypeCompatibility( AccessType.TRANSACTIONAL, configuration );
			Assert.fail("Expected " + AccessType.TRANSACTIONAL + " with nonstop to fail");
		} catch (IllegalArgumentException e) {
			// expected
		}
	}

	@Test
	public void testStrongAccessStrategyChecks() throws Exception {
		EhcacheAccessStrategyFactoryImpl accessStrategyFactory = new EhcacheAccessStrategyFactoryImpl( false, false );
		CacheConfiguration configuration = new CacheConfiguration( "test", 1 ).terracotta(
				new TerracottaConfiguration().consistency( TerracottaConfiguration.Consistency.STRONG )
		);

		accessStrategyFactory.checkAccessTypeCompatibility( AccessType.READ_ONLY,  configuration);
		accessStrategyFactory.checkAccessTypeCompatibility( AccessType.NONSTRICT_READ_WRITE, configuration );
		accessStrategyFactory.checkAccessTypeCompatibility( AccessType.READ_WRITE, configuration );

		try {
			accessStrategyFactory.checkAccessTypeCompatibility( AccessType.TRANSACTIONAL, configuration );
			Assert.fail("Expected " + AccessType.TRANSACTIONAL + " with strong to fail");
		} catch (IllegalArgumentException e) {
			// expected
		}
	}

	@Test
	public void testTransactionalAccessStrategyChecks() throws Exception {
		EhcacheAccessStrategyFactoryImpl accessStrategyFactory = new EhcacheAccessStrategyFactoryImpl( false, false );
		accessStrategyFactory.checkAccessTypeCompatibility( AccessType.TRANSACTIONAL, new CacheConfiguration( "test", 1 ).transactionalMode(
																	CacheConfiguration.TransactionalMode.XA ) );
	}

	@Test
	public void testOverrideNonstopChecks() throws Exception {
		EhcacheAccessStrategyFactoryImpl accessStrategyFactory = new EhcacheAccessStrategyFactoryImpl( true, false );
		CacheConfiguration configuration = new CacheConfiguration( "test", 1 ).terracotta(
				new TerracottaConfiguration().nonstop( new NonstopConfiguration().enabled( true ) ).consistency(
						TerracottaConfiguration.Consistency.STRONG )
		);
		accessStrategyFactory.checkAccessTypeCompatibility( AccessType.READ_WRITE,  configuration );
	}

	@Test
	public void testOverrideEventualChecks() throws Exception {
		EhcacheAccessStrategyFactoryImpl accessStrategyFactory = new EhcacheAccessStrategyFactoryImpl( false, true );
		CacheConfiguration configuration = new CacheConfiguration( "test", 1 ).terracotta(
				new TerracottaConfiguration().consistency( TerracottaConfiguration.Consistency.EVENTUAL )
		);
		accessStrategyFactory.checkAccessTypeCompatibility( AccessType.READ_WRITE,  configuration );
	}
}