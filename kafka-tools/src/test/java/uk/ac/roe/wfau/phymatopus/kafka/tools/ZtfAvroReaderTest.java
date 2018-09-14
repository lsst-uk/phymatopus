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

import static org.junit.Assert.*;

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

    /**
     * 
     */
    public ZtfAvroReaderTest()
        {
        super();

        this.group   = "java-test" ;
        this.topic   = "ztf_20180811_programid1" ;
        this.servers = "172.16.49.217:9092,172.16.49.214:9092,172.16.49.12:9092,172.16.49.208:9092" ;

        }

    /**
     * Test we can load our Avro {@link Schema}.
     * 
     */
    //@Test
    public void testInit()
        {
        final ZtfAvroReader reader = new ZtfAvroReader(
            servers,
            group,
            topic
            ); 
        reader.init();
        assertNotNull(
            reader.schema()
            );
        }

    /**
     * Test we can read some messages.
     * 
     */
    //@Test
    public void testLoop()
        {
        final ZtfAvroReader reader = new ZtfAvroReader(
            servers,
            group,
            topic
            ); 
        reader.init();
        reader.loop(10);
        }


    /**
     * Test we can run a test.
     * 
     */
    @Test
    public void testTest()
        {
        log.debug("Testing the tester");
        }
    }