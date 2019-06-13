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
import uk.ac.roe.wfau.phymatopus.kafka.tools.ZtfAvroReader.CallableReader;
import uk.ac.roe.wfau.phymatopus.kafka.tools.ZtfAvroReader.Statistics;

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
public class ZtfAvroReaderTest
extends KafkaTestBase
    {

    private int loops = 1 ;
    private Duration timeout = Duration.ofSeconds(5);

    /**
     * The target kafka servers.
     * 
     */
    @Value("${phymatopus.kafka.threads:4}")
    private Integer threadcount ;
    
    /**
     *
     */
    public ZtfAvroReaderTest()
        {
        super();

        //this.group   = "java-test-001" ;
        //this.topic   = "ztf_20190612_programid1" ;
        //this.servers = "Stedigo:9092" ;

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
                    this.timeout,
                    this.servers,
                    this.group,
                    this.topic,
                    this.loops
                    )
                );
            }
        
        readers.get(0).rewind();

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
            log.info("Group [{}] with [{}] threads read [{}] rows in [{}]ms = [{}]ms per row", threadcount, this.group, totalrows, totalmilli, meanmilli);
            
            }
        finally {
            executor.shutdown();
            }
        }
    }

