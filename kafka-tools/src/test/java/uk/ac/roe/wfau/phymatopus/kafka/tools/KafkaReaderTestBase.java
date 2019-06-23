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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Value;

import lombok.extern.slf4j.Slf4j;
import uk.ac.roe.wfau.phymatopus.kafka.alert.AlertProcessor;
import uk.ac.roe.wfau.phymatopus.kafka.alert.ZtfAlert;

/**
 *
 *
 */
@Slf4j
public abstract class KafkaReaderTestBase
    {
    /**
     * The target kafka servers.
     * 
     */
    @Value("${phymatopus.kafka.reader.servers:}")
    protected String servers;

    /**
     * The target kafka topic.
     * 
     */
    @Value("${phymatopus.kafka.reader.topic:}")
    protected String topic;

    /**
     * The target kafka group.
     * 
     */
    @Value("${phymatopus.kafka.reader.group:}")
    protected String group;
    
    @Value("${phymatopus.kafka.reader.looptimeout:T10M}")
    private String   looptimeoutstr ;
    protected Duration looptimeout()
        {
        return Duration.parse(looptimeoutstr);
        }

    @Value("${phymatopus.kafka.reader.polltimeout:T10S}")
    private String   polltimeoutstr;
    protected Duration polltimeout()
        {
        return Duration.parse(polltimeoutstr);
        }

    /**
     * The number of concurrent threads.
     * 
     */
    @Value("${phymatopus.kafka.reader.threads:4}")
    private Integer threadcount ;

    /**
     * Flag to reset the stream.
     * 
     */
    @Value("${phymatopus.kafka.reader.rewind:true}")
    private Boolean rewind ;

    /**
     * Public constructor.
     *
     */
    public KafkaReaderTestBase()
        {
        super();
        }

    /**
     * Create a new reader configuration.
     * 
     */
    public ZtfAlertReader.Configuration configuration()
        {
        return new ZtfAlertReader.ConfigurationBean(
            this.looptimeout(),
            this.polltimeout(),
            this.servers,
            this.topic,
            this.group
            );
        }

    /**
     * Create a new alert processor.
     * 
     */
    protected abstract AlertProcessor<ZtfAlert> processor() ;

    /**
     * Create a new alert reader.
     * 
     */
    protected abstract CallableAlertReader reader();
    
    /**
     * Test multiple threads.
     *
     */
    protected void testThreads()
    throws Exception
        {
        final List<CallableAlertReader> readers = new ArrayList<CallableAlertReader>(); 

        for (int i = 0 ; i < this.threadcount ; i++)   
            {
            readers.add(
                reader()
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

            List<Future<ReaderStatistics>> futures = executor.invokeAll(readers);
            long alerts  = 0 ;
            long runtime = 0 ;
            for (Future<ReaderStatistics> future : futures)
                {
                ReaderStatistics result = future.get();
                alerts  += result.count();
                runtime += result.time();
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

