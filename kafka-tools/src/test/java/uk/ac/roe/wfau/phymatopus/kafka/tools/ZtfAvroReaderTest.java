/*
 *  Copyright (C) 2018 Royal Observatory, University of Edinburgh, UK
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package uk.ac.roe.wfau.phymatopus.kafka.tools;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import lombok.extern.slf4j.Slf4j;

/**
 *
 *
 */
@Slf4j
@RunWith(JUnit4.class)
public class ZtfAvroReaderTest
extends KafkaTestBase
    {

    private int loopcount = 1 ;
    private Duration loopwait = Duration.ofSeconds(5);
    private int threadcount = 4 ;

    /**
     *
     */
    public ZtfAvroReaderTest()
        {
        super();

        this.group   = "java-test-001" ;
        this.topic   = "ztf_20190612_programid1" ;
        this.servers = "Stedigo:9092" ;

        }

    /**
     * Test we can read some messages.
     *
     */
    public void testLoop()
        {
        final ZtfAvroReader reader = new ZtfAvroReader(
            servers,
            group,
            topic
            );
        reader.rewind();
        reader.loop(
            loopcount,
            loopwait
            );
        }


    /**
     * Callable Reader.
     *
     */
    public class CallableReader
    implements Callable<Long>
        {
        public CallableReader()
            {
            this(
                new ZtfAvroReader(
                    servers,
                    group,
                    topic
                    )
                );
            }
        
        public CallableReader(final ZtfAvroReader reader)
            {
            this.reader = reader;
            }

        private final ZtfAvroReader reader ;

        public ZtfAvroReader reader()
            {
            return this.reader ;
            }

        public void rewind()
            {
            this.reader.rewind();
            }

        public Long call()
            {
            return reader.loop(
                loopcount,
                loopwait
                );
            }
        }

    /**
     * Test multiple threads.
     *
     */
    @Test
    public void testThreads()
    throws Exception
        {
        final List<CallableReader> readers = new ArrayList<CallableReader>(); 

        for (int i = 0 ; i < threadcount ; i++)   
            {
            readers.add(
                new CallableReader()
                );
            }
        
        readers.get(0).rewind();

        final ExecutorService executor = Executors.newFixedThreadPool(
            readers.size()
            );        
        try{
            List<Future<Long>> futures = executor.invokeAll(readers);
            long totalrecords = 0 ;
            for (Future<Long> future : futures)
                {
                totalrecords += future.get();
                }
            log.debug("Total records[{}]", totalrecords);
            }
        finally {
            executor.shutdown();
            }
        }
    }

