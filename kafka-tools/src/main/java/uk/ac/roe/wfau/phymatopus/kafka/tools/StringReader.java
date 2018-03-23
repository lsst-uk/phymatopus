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

import java.util.Collections;
import java.util.Properties;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

import lombok.extern.slf4j.Slf4j;

/**
 * First attempts at a Java consumer.
 * Based on Cloudurable tutorial:
 * http://cloudurable.com/blog/kafka-tutorial-kafka-consumer/index.html
 * 
 */
@Slf4j
public class StringReader
    {

    /**
     * Public constructor.
     * 
     */
    public StringReader(final String servers, final String topic)
        {
        this.topic   = topic;
        this.servers = servers;
        }

    /**
     * Our target kafka servers.
     * 
     */
    protected String servers;
    protected String servers()
        {
        return this.servers.trim();
        }

    /**
     * Our target kafka topic.
     * 
     */
    protected String topic;
    protected String topic()
        {
        return this.topic.trim();
        }

    /**
     * Create our {@link Consumer}.
     * 
     */
    private Consumer<Long, String> consumer()
        {
        final Properties properties = new Properties();
        properties.put(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
            servers()
            );
        properties.put(
            ConsumerConfig.GROUP_ID_CONFIG,
            "KafkaExampleConsumer"
            );
        properties.put(
            ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG,
            "1000"
            );
        properties.put(
            ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,
            "true"
            );
        final Consumer<Long, String> consumer = new KafkaConsumer<>(
            properties,
            new LongDeserializer(),
            new StringDeserializer()
            );

        consumer.subscribe(
            Collections.singletonList(
                topic()
                )
            );
        return consumer;
        }    
    
    /**
     * Read a series of records from the stream. 
     * 
     */
    public void read(final int target)
        {
        log.debug("Starting read [{}]", target);
        final Consumer<Long, String> consumer = consumer(); 
        long start = System.currentTimeMillis();

        int count   = 0;
        int limit   = 10;
        int timeout = 1000;
        
        for (int loop = 0 ; ((count < target) && (loop < limit)) ; loop++)
            {
            log.debug("Loop start [{}]", loop);
            final ConsumerRecords<Long, String> records = consumer.poll(
                timeout
                );

            log.debug("Recived [{}]", records.count());

            for (ConsumerRecord<Long, String> record : records)
                {
                log.debug("Record  [{}][{}][{}]", count, record.key(), record.value());
                count++;
                }
            log.debug("Loop done [{}]", loop);
            }

        //consumer.commitSync();
        consumer.close();

        long elapsed = System.currentTimeMillis() - start ;
        float mean = (float)elapsed/(float)count;
        log.debug("Time [{}][{}][{}]", elapsed, count, mean);

        }
    }
