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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import lombok.extern.slf4j.Slf4j;
import uk.ac.roe.wfau.phymatopus.kafka.alert.ZtfAlert;
import uk.ac.roe.wfau.phymatopus.kafka.alert.ZtfCutout;
import uk.ac.roe.wfau.phymatopus.kafka.tools.ZtfTestAlertReader.ConfigurationBean;
import uk.ac.roe.wfau.phymatopus.kafka.tools.ZtfTestAlertReader.CallableReader;
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
public class ZtfTestAlertReaderTest
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
     * Flag to enable auto-commit.
     * 
     */
    @Value("${phymatopus.kafka.autocomit:true}")
    private Boolean autocomit ;

    /**
     *
     */
    public ZtfTestAlertReaderTest()
        {
        super();
        }

    /**
     * Our Alert processor.
     * 
     */
    public class AlertProcessor implements ZtfAlert.Processor
        {
        /**
         * Public constructor.
         * 
         */
        public AlertProcessor()
            {
            }

        @Override
        public void process(final ZtfAlert alert)
            {
            log.trace("candId    [{}]", alert.getCandid());
            log.trace("objectId  [{}]", alert.getObjectId());
            log.trace("schemavsn [{}]", alert.getSchemavsn().toString());

            final ZtfCutout science    = alert.getCutoutScience();
            final ZtfCutout template   = alert.getCutoutTemplate();
            final ZtfCutout difference = alert.getCutoutDifference();

            if (null != science)
                {
                log.trace("science    [{}][{}][{}]", science.getFileName(), science.getStampData().limit(), science.getStampData().capacity());
                }
            if (null != template)
                {
                log.trace("template   [{}][{}][{}]", template.getFileName(), template.getStampData().limit(), template.getStampData().capacity());
                }
            if (null != difference)
                {
                log.trace("difference [{}][{}][{}]", difference.getFileName(), difference.getStampData().limit(), difference.getStampData().capacity());
                }
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
                        this.autocomit,
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
            log.info("Group [{}] with [{}] threads read [{}] alerts in [{}]ms at [{}]ms per alert", this.group, threadcount, alerts, testmilli, meanmilli);
            
            }
        finally {
            executor.shutdown();
            }
        }
    }

