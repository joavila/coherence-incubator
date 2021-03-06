/*
 * File: LiveObjectTest.java
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * The contents of this file are subject to the terms and conditions of 
 * the Common Development and Distribution License 1.0 (the "License").
 *
 * You may not use this file except in compliance with the License.
 *
 * You can obtain a copy of the License by consulting the LICENSE.txt file
 * distributed with this file, or by consulting https://oss.oracle.com/licenses/CDDL
 *
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file LICENSE.txt.
 *
 * MODIFICATIONS:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 */

package com.oracle.coherence.common.events.processing;

import com.oracle.coherence.common.events.EntryEvent;
import com.oracle.coherence.common.events.EntryInsertedEvent;
import com.oracle.coherence.common.events.EntryRemovedEvent;
import com.oracle.coherence.common.events.EntryUpdatedEvent;
import com.oracle.coherence.common.events.Event;
import com.oracle.coherence.common.events.dispatching.EventDispatcher;
import com.oracle.coherence.common.events.processing.annotations.EventProcessorFor;
import com.oracle.coherence.common.events.processing.annotations.LiveObject;

import com.oracle.coherence.environment.Environment;

import com.oracle.coherence.environment.extensible.ExtensibleEnvironment;

import com.oracle.tools.junit.AbstractCoherenceTest;

import com.tangosol.net.CacheFactory;
import com.tangosol.net.ConfigurableCacheFactory;
import com.tangosol.net.NamedCache;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

import java.io.Serializable;

import java.util.Map.Entry;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Functional Test for {@link LiveObject}s.
 * <p>
 * Copyright (c) 2013. All Rights Reserved. Oracle Corporation.<br>
 * Oracle is a registered trademark of Oracle Corporation and/or its affiliates.
 *
 * @author Brian Oliver
 */
public class LiveObjectTest extends AbstractCoherenceTest
{
    private static final String INSERTS = "INSERTS";
    private static final String UPDATES = "UPDATES";
    private static final String REMOVES = "REMOVES";

    /**
     * The {@link ConfigurableCacheFactory} for the test.
     */
    private ConfigurableCacheFactory ccf;
    private NamedCache               m_cacheLiveObjects;
    private Environment              m_environment;


    /**
     * {@inheritDoc}
     */
    @Before
    @Override
    public void onBeforeEachTest()
    {
        super.onBeforeEachTest();

        // establish the CCF for the test
        ccf = (ConfigurableCacheFactory) new ExtensibleEnvironment("coherence-common-cache-config.xml");
        CacheFactory.setCacheFactoryBuilder(null);
        CacheFactory.setConfigurableCacheFactory(ccf);

        m_cacheLiveObjects = CacheFactory.getCache("coherence.live.objects.distributed");

        m_environment      = (Environment) CacheFactory.getConfigurableCacheFactory();

        m_environment.registerResource(AtomicLong.class, INSERTS, new AtomicLong());
        m_environment.registerResource(AtomicLong.class, REMOVES, new AtomicLong());
        m_environment.registerResource(AtomicLong.class, UPDATES, new AtomicLong());
    }


    /**
     * Ensure LiveObjects are inserted, updated and removed as expected.
     */
    @Test
    public void testLiveObjectInsertUpdateRemove()
    {
        assertEquals(0, m_cacheLiveObjects.size());
        assertEquals(0, m_environment.getResource(AtomicLong.class, INSERTS).get());
        assertEquals(0, m_environment.getResource(AtomicLong.class, UPDATES).get());
        assertEquals(0, m_environment.getResource(AtomicLong.class, REMOVES).get());

        m_cacheLiveObjects.put("alo", new AnnotatedLiveObject());

        assertEquals(1, m_cacheLiveObjects.size());
        assertEquals(1, m_environment.getResource(AtomicLong.class, INSERTS).get());
        assertEquals(0, m_environment.getResource(AtomicLong.class, UPDATES).get());
        assertEquals(0, m_environment.getResource(AtomicLong.class, REMOVES).get());

        m_cacheLiveObjects.put("alo", new AnnotatedLiveObject());

        assertEquals(1, m_cacheLiveObjects.size());
        assertEquals(1, m_environment.getResource(AtomicLong.class, INSERTS).get());
        assertEquals(1, m_environment.getResource(AtomicLong.class, UPDATES).get());
        assertEquals(0, m_environment.getResource(AtomicLong.class, REMOVES).get());

        m_cacheLiveObjects.remove("alo");

        assertEquals(0, m_cacheLiveObjects.size());
        assertEquals(1, m_environment.getResource(AtomicLong.class, INSERTS).get());
        assertEquals(1, m_environment.getResource(AtomicLong.class, UPDATES).get());
        assertEquals(1, m_environment.getResource(AtomicLong.class, REMOVES).get());
    }


    /**
     * An {@link AnnotatedLiveObject}.
     */
    @LiveObject
    @SuppressWarnings("serial")
    public static class AnnotatedLiveObject implements Serializable
    {
        /**
         * Constructs an {@link AnnotatedLiveObject}.
         */
        public AnnotatedLiveObject()
        {
            // required for serialization
        }


        /**
         * Handle when the {@link AnnotatedLiveObject} is inserted.
         *
         * @param dispatcher  the {@link EventDispatcher}
         * @param event       the {@link Event}
         */
        @EventProcessorFor(events = {EntryInsertedEvent.class})
        public void onInsert(EventDispatcher                                dispatcher,
                             EntryEvent<Entry<String, AnnotatedLiveObject>> event)
        {
            dispatcher.getEnvironment().getResource(AtomicLong.class, LiveObjectTest.INSERTS).incrementAndGet();
        }


        /**
         * Handle when the {@link AnnotatedLiveObject} is updated.
         *
         * @param dispatcher  the {@link EventDispatcher}
         * @param event       the {@link Event}
         */
        @EventProcessorFor(events = {EntryUpdatedEvent.class})
        public void onUpdate(EventDispatcher                                dispatcher,
                             EntryEvent<Entry<String, AnnotatedLiveObject>> event)
        {
            dispatcher.getEnvironment().getResource(AtomicLong.class, LiveObjectTest.UPDATES).incrementAndGet();
        }


        /**
         * Handle when the {@link AnnotatedLiveObject} is removed.
         *
         * @param dispatcher  the {@link EventDispatcher}
         * @param event       the {@link Event}
         */
        @EventProcessorFor(events = {EntryRemovedEvent.class})
        public void onRemove(EventDispatcher                                dispatcher,
                             EntryEvent<Entry<String, AnnotatedLiveObject>> event)
        {
            dispatcher.getEnvironment().getResource(AtomicLong.class, LiveObjectTest.REMOVES).incrementAndGet();
        }
    }
}
