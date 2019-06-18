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
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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
@RunWith(
        SpringJUnit4ClassRunner.class
        )
@ContextConfiguration(
    locations = {
        "classpath:component-config.xml"
        }
    )
public class CassandraCandidateWriterTest
extends KafkaTestBase
    {

    @Value("${phymatopus.kafka.looptimeout:T10M}")
    private String   looptimeoutstr ;
    private Duration looptimeout()
        {
        return Duration.parse(looptimeoutstr);
        }

    @Value("${phymatopus.kafka.polltimeout:T10S}")
    private String   polltimeoutstr;
    private Duration polltimeout()
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
     * Cassandrda connection hostname.
     * 
     */
    @Value("${phymatopus.cassandrda.hostname:}")
    private String hostname ;
    
    /**
     * Cassandrda datacenter name.
     * 
     */
    @Value("${phymatopus.cassandrda.dcname:}")
    private String dcname;
    
    /**
     *
     */
    public CassandraCandidateWriterTest()
        {
        super();
        }

    /**
     * Our Alert processor.
     * 
     */
    public class AlertProcessor implements ZtfAlert.Processor
        {
        private long count ;

        private SimpleCandiateManager manager;
        
        /**
         * Public constructor.
         * 
         */
        public AlertProcessor()
            {
            this.count = 0 ;
            manager = new SimpleCandiateManager(
                CassandraCandidateWriterTest.this.hostname,
                CassandraCandidateWriterTest.this.dcname
                );
            manager.init();
            }

        @Override
        public void process(final ZtfAlert alert)
            {
            count++;
            log.trace("Candidate [{}][{}]", count, alert.getCandid());
            manager.insert(
                alert.getCandidate()
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

        for (int i = 0 ; i < this.threadcount ; i++)   
            {
            readers.add(
                new CallableReader(
                    new AlertProcessor(),
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

