package org.hibernate.test.cache;

import net.sf.ehcache.CacheManager;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.hibernate.cache.ehcache.management.impl.EhcacheStatsImpl;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Alex Snaps
 */
public class EhcacheStatsImplTest {

	private static EhcacheStatsImpl stats;
	private static CacheManager manager;

	@BeforeClass
	public static void createCache() throws Exception {
		manager = CacheManager.getInstance();
		stats = new EhcacheStatsImpl( manager );
	}

	@Test
	public void testIsRegionCacheOrphanEvictionEnabled() {
		assertThat( stats.isRegionCacheOrphanEvictionEnabled( "sampleCache1" ), is( false ) );
	}

	@Test
	public void testGetRegionCacheOrphanEvictionPeriod() {
		assertThat( stats.getRegionCacheOrphanEvictionPeriod( "sampleCache1" ), is( -1 ) );
	}

	@AfterClass
	public static void destroyCache() {
		manager.shutdown();
	}
}