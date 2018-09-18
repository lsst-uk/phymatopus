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

import java.time.Duration;

import org.apache.avro.Schema;
import org.apache.avro.message.BinaryMessageDecoder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import lombok.extern.slf4j.Slf4j;
import ztf.alert;

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

        this.group   = "java-test-001" ;
        this.topic   = "ztf_20180917_programid1" ;
        this.servers = "172.16.49.217:9092,172.16.49.214:9092,172.16.49.12:9092,172.16.49.208:9092" ;

        }

    /**
     * Test we can load our Avro {@link Schema}.
     * 
    @Test
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
     */

    /**
     * Test we can read some messages.
     * 
     */
    @Test
    public void testLoop()
        {
        final ZtfAvroReader reader = new ZtfAvroReader(
            servers,
            group,
            topic
            ); 
        reader.loop(
            20,
            Duration.ofSeconds(
                120
                )
            );
        }

    /**
     * Test we can run a test.
     * 
    @Test
    public void testTest()
        {
        log.debug("Testing the tester");
        }
     */

    //@Test
    public void testAvro()
        {
        log.debug("Testing Avro serializers ..");
        BinaryMessageDecoder<alert> decoder = alert.getDecoder();
        
        Schema s = alert.SCHEMA$;
        log.debug("Schema name [{}]", s.getName());
        log.debug("Schema full name  [{}]", s.getFullName());
        log.debug("Schema name space [{}]", s.getNamespace());

        
        }
    }
