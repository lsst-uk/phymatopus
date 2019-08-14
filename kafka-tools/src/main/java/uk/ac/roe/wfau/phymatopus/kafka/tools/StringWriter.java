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

import java.util.Properties;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import lombok.extern.slf4j.Slf4j;
import uk.ac.roe.wfau.phymatopus.kafka.BaseClient;

/**
 * First attempts at a Java producer.
 * Based on Cloudurable tutorial:
 * http://cloudurable.com/blog/kafka-tutorial-kafka-producer/index.html 
 * 
 */
@Slf4j
public class StringWriter
extends BaseClient
    {

    /**
     * Public constructor.
     * 
     */
    public StringWriter(final Configuration config)
        {
        super(
            config
            );
        }

    /**
     * Create our {@link Producer}. 
     * 
     */
    protected Producer<Long, String> producer()
        {
        Properties properties = new Properties();
        properties.put(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
            this.config.getServers()
            );
        properties.put(
            ProducerConfig.CLIENT_ID_CONFIG,
            "KafkaExampleProducer"
            );
        return new KafkaProducer<Long, String>(
            properties,
            new LongSerializer(),
            new StringSerializer()
            );
        }

    /**
     * Write a series of records to the stream. 
     * 
     */
    public void write(final int count)
        {
        log.debug("Creating Producer<Long, String>");
        final Producer<Long, String> producer = producer(); 

        log.debug("Starting write [{}]", count);
        long start = System.currentTimeMillis();
        
        try {
            for (int index = 0 ; index < count ; index++)
                {
                log.debug("Loop [{}]", index);
                final ProducerRecord<Long, String> record = new ProducerRecord<Long, String>(
                    this.config.getTopic(),
                    (start + index),
                    "Hello Mum [" + index + "]"
                    );
                log.debug("Record   [{}][{}]", record.key(), record.value());
                final RecordMetadata metadata = producer.send(
                    record
                    ).get();
                log.debug("Response [{}][{}]", metadata.partition(), metadata.offset());
                }
            long elapsed = System.currentTimeMillis() - start ;
            float mean = (float)elapsed/(float)count;
            log.debug("Time [{}][{}][{}]", elapsed, count, mean);
            }
        catch (InterruptedException ouch)
            {
            log.debug("InterruptedException during loop", ouch);
            }
        catch (ExecutionException ouch)
            {
            log.debug("ExecutionException during loop", ouch);
            }
        finally {
            producer.flush();
            producer.close();
            }
        }

    }
