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

    private int loops = 1 ;
    private Duration timeout = Duration.ofSeconds(5);

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

        for (int i = 0 ; i < threadcount ; i++)   
            {
            readers.add(
                new CallableReader(
                    new AlertProcessor(),
                    this.autocomit,
                    this.timeout,
                    this.servers,
                    this.group,
                    this.topic,
                    this.loops
                    )
                );
            }
        
        if (rewind)
            {
            log.debug("Rewinding consumer group");
            readers.get(0).rewind();
            }

        final ExecutorService executor = Executors.newFixedThreadPool(
            readers.size()
            );        
        try{
            long startnano = System.nanoTime();
            List<Future<Statistics>> futures = executor.invokeAll(readers);
            long totalrows  = 0 ;
            long totalbytes = 0 ;
            long totaltime  = 0 ;
            for (Future<Statistics> future : futures)
                {
                Statistics result = future.get();
                totalrows  += result.rows();
                totalbytes += result.bytes();
                totaltime  += result.time();
                }
            long donenano = System.nanoTime();
            float totalmilli = (donenano - startnano) / 1000000 ;
            float meanmilli = totalmilli / totalrows ;
            log.info("Group [{}] with [{}] threads read [{}] rows in [{}]ms at [{}]ms per row", this.group, threadcount, totalrows, totalmilli, meanmilli);
            
            }
        finally {
            executor.shutdown();
            }
        }
    }

