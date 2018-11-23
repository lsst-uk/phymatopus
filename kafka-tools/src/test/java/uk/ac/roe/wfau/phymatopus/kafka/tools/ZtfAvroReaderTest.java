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

    private int loopcount = 6 ;
    private Duration loopwait = Duration.ofSeconds(6);

    /**
     *
     */
    public ZtfAvroReaderTest()
        {
        super();

        this.group   = "java-test-001" ;
        this.topic   = "ztf_20181120_programid1" ;
        this.servers = "172.16.49.217:9092,172.16.49.214:9092,172.16.49.12:9092,172.16.49.208:9092" ;

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
     * Runnable Reader.
     *
     */
    public class RunnableReader
    implements Runnable
        {
        public RunnableReader(final ZtfAvroReader reader)
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

        public void run()
            {
            reader.loop(
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
        {
        final RunnableReader[] readers = {
            new RunnableReader(
                new ZtfAvroReader(
                    servers,
                    group,
                    topic
                    )
                ),
            new RunnableReader(
                new ZtfAvroReader(
                    servers,
                    group,
                    topic
                    )
                ),
            new RunnableReader(
                new ZtfAvroReader(
                    servers,
                    group,
                    topic
                    )
                )
            };

        readers[0].rewind();

        for (RunnableReader reader : readers)
            {
            Thread thread = new Thread(reader);
            thread.start();
            }
        }
    }

