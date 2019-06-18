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

package uk.ac.roe.wfau.phymatopus.kafka.cassandra;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;

import lombok.extern.slf4j.Slf4j;
import uk.ac.roe.wfau.phymatopus.kafka.alert.ZtfAlert;
import uk.ac.roe.wfau.phymatopus.kafka.tools.KafkaTestBase;
import uk.ac.roe.wfau.phymatopus.kafka.tools.ZtfTestAlertReader.CallableReader;
import uk.ac.roe.wfau.phymatopus.kafka.tools.ZtfTestAlertReader.ConfigurationBean;
import uk.ac.roe.wfau.phymatopus.kafka.tools.ZtfTestAlertReader.Statistics;

/**
 *
 *
 */
@Slf4j
public abstract class ZtfAbstractWriterTest
extends KafkaTestBase
    {

    @Value("${phymatopus.kafka.looptimeout:T10M}")
    private String   looptimeoutstr ;
    protected Duration looptimeout()
        {
        return Duration.parse(looptimeoutstr);
        }

    @Value("${phymatopus.kafka.polltimeout:T10S}")
    private String   polltimeoutstr;
    protected Duration polltimeout()
        {
        return Duration.parse(polltimeoutstr);
        }

    /**
     * The number of concurrent threads.
     * 
     */
    @Value("${phymatopus.kafka.threads:4}")
    private Integer threadcount ;

    /**
     * Flag to reset the stream.
     * 
     */
    @Value("${phymatopus.kafka.rewind:true}")
    private Boolean rewind ;

    /**
     * Our Cassandrda connection hostname.
     * 
     */
    @Value("${phymatopus.cassandrda.hostname:}")
    private String hostname ;

    /**
     * Our Cassandrda connection hostname.
     * 
     */
    public String hostname()
        {
        return this.hostname;
        }

    /**
     * Our Cassandrda datacenter name.
     * 
     */
    @Value("${phymatopus.cassandrda.dcname:}")
    private String dcname;

    /**
     * Our Cassandrda datacenter name.
     * 
     */
    public String dcname()
        {
        return this.dcname;
        }
    
    /**
     * Public constructor.
     *
     */
    public ZtfAbstractWriterTest()
        {
        super();
        }
    
    /**
     * Our Alert processor class.
     * 
     */
    public class Processor implements ZtfAlert.Processor
        {
        private long count ;
        public long count()
            {
            return this.count;
            }

        private AbstractCassandraWriter writer;
        
        /**
         * Public constructor.
         * 
         */
        public Processor(AbstractCassandraWriter writer)
            {
            this.writer = writer;
            writer.init();
            }

        @Override
        public void process(final ZtfAlert alert)
            {
            this.count++;
            log.trace("Candidate [{}][{}]", this.count, alert.getCandid());
            writer.insert(
                alert
                );
            }
        }

    /**
     * Create a new alert processor.
     * 
     */
    public Processor processor()
        {
        return new Processor(
            this.writer()
            ); 
        }

    /**
     * Create a new alert writer.
     * 
     */
    public abstract AbstractCassandraWriter writer();

    /**
     * Test multiple threads.
     *
     */
    @Test
    public void testThreads()
    throws Exception
        {
        final List<CallableReader> readers = new ArrayList<CallableReader>(); 

        for (int i = 0 ; i < this.threadcount ; i++)   
            {
            readers.add(
                new CallableReader(
                    this.processor(),
                    new ConfigurationBean(
                        this.looptimeout(),
                        this.polltimeout(),
                        this.servers,
                        this.topic,
                        this.group
                        ) 
                    )
                );
            }
        
        if (this.rewind)
            {
            log.debug("Rewinding consumer group");
            readers.get(0).rewind();
            }

        final ExecutorService executor = Executors.newFixedThreadPool(
            readers.size()
            );        
        try{

            long teststart = System.nanoTime();

            List<Future<Statistics>> futures = executor.invokeAll(readers);
            long alerts  = 0 ;
            long runtime = 0 ;
            for (Future<Statistics> future : futures)
                {
                Statistics result = future.get();
                alerts  += result.alerts();
                runtime += result.runtime();
                }

            long testtime  = (System.nanoTime() - teststart) - looptimeout().toNanos() ;
            
            float testmilli = testtime / (1000 * 1000);
            float meanmilli  = testmilli / alerts;
            log.info("Group [{}] with [{}] threads read [{}] alerts from topic [{}] in [{}]ms at [{}]ms per alert", this.group, threadcount, alerts, this.topic, testmilli, meanmilli);
            
            }
        finally {
            executor.shutdown();
            }
        }
    }

