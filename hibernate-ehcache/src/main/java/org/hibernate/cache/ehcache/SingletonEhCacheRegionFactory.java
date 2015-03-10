/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * Copyright (c) 2011, Red Hat Inc. or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Inc.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.hibernate.cache.ehcache;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import net.sf.ehcache.CacheManager;

import org.hibernate.cache.CacheException;
import org.hibernate.cfg.Settings;

import org.jboss.logging.Logger;

/**
 * A singleton EhCacheRegionFactory implementation.
 *
 * @author Chris Dennis
 * @author Greg Luck
 * @author Emmanuel Bernard
 * @author Alex Snaps
 */
public class SingletonEhCacheRegionFactory extends AbstractEhcacheRegionFactory {
	private static final EhCacheMessageLogger LOG = Logger.getMessageLogger(
			EhCacheMessageLogger.class,
			SingletonEhCacheRegionFactory.class.getName()
	);

	private static final AtomicInteger REFERENCE_COUNT = new AtomicInteger();

	/**
	 * Constructs a SingletonEhCacheRegionFactory
	 */
	@SuppressWarnings("UnusedDeclaration")
	public SingletonEhCacheRegionFactory() {
	}

	/**
	 * Constructs a SingletonEhCacheRegionFactory
	 *
	 * @param prop Not used
	 */
	@SuppressWarnings("UnusedDeclaration")
	public SingletonEhCacheRegionFactory(Properties prop) {
		super();
	}

	@Override
	public void doStart(Settings settings, Properties properties) throws CacheException {
		try {
			manager = CacheManager.create( getConfiguration( properties ) );
			REFERENCE_COUNT.incrementAndGet();
		}
		catch (net.sf.ehcache.CacheException e) {
			throw new CacheException( e );
		}
	}

	@Override
	public void stop() {
		try {
			if ( manager != null ) {
				if ( REFERENCE_COUNT.decrementAndGet() == 0 ) {
					manager.shutdown();
				}
				manager = null;
			}
		}
		catch (net.sf.ehcache.CacheException e) {
			throw new CacheException( e );
		}
	}
}
