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

import org.apache.avro.Schema;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.Serializer;

import io.confluent.kafka.schemaregistry.client.MockSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import lombok.extern.slf4j.Slf4j;
import uk.ac.roe.wfau.phymatopus.kafka.alert.ZtfAlert;
import ztf.alert;

/**
 * First attempt at an alert writer.
 * 
 */
@Slf4j
public class ZtfAlertWriter
extends BaseClient
    {

    /**
     * Public constructor.
     * 
     */
    public ZtfAlertWriter(final Configuration config)
        {
        super(
            config
            );
        }

    /**
     * Public interface for a writer configuration.
     * 
     */
    public static interface Configuration extends BaseReader.Configuration
        {
        }

    /**
     * Configuration implementation.
     * 
     */
    public static class ConfigurationBean
    extends BaseClient.ConfigurationBean
    implements Configuration
        {
        public ConfigurationBean(final String servers, final String topic, final String group)
            {
            super(
                servers,
                topic,
                group
                );
            }
        }

    /**
     * A Serializer for our index type.
     *
     */
    public Serializer<Long> indexSerializer()
        {
        return new LongSerializer();
        }

    /**
     * A Serializer for our data type.
     *
     */
    public Serializer<Object> dataSerializer()
        {
        SchemaRegistryClient reg = new MockSchemaRegistryClient();
        KafkaAvroSerializer  ser = new KafkaAvroSerializer(
            reg
            );
        return ser;
        }

    /**
     * Access our Schema.
     *
     */
    protected Schema schema()
        {
        return alert.SCHEMA$;
        }
    
    /**
     * Our {@link Producer}. 
     * 
     */
    private static Producer<Long,Object> producer;
    
    /**
     * Initialise our {@link Producer}. 
     * 
     */
    protected synchronized void init()
        {
        if (null == producer)
            {
            final Properties properties = new Properties();
            properties.put(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                this.config.getServers()
                );
            properties.put(
                ProducerConfig.CLIENT_ID_CONFIG,
                this.config.getGroup()
                );
            producer = new KafkaProducer<Long, Object>(
                properties,
                indexSerializer(),
                dataSerializer()
                );
            }
        }

    /**
     * Count of the records sent by this writer.
     *
     */
    private long count ;

    /**
     * Write a an alert to the stream. 
     * 
     */
    public void write(final ZtfAlert alert)
        {
        log.debug("write(alert)");
        try {
            log.debug("Creating ProducerRecord");
            final ProducerRecord<Long, Object> record = new ProducerRecord<Long, Object>(
                    this.config.getTopic(),
                    count,
                    alert
                    );
            log.debug("Sending ProducerRecord");
            final RecordMetadata metadata = producer.send(
                record
                ).get();
            log.debug("Response [{}][{}]", metadata.partition(), metadata.offset());
            }
        catch (InterruptedException ouch)
            {
            log.error("InterruptedException during write [{}]", ouch);
            throw new RuntimeException(
                ouch
                );
            }
        catch (ExecutionException ouch)
            {
            log.error("ExecutionException during write [{}]", ouch);
            throw new RuntimeException(
                ouch
                );
            }
        }

    /**
     * Flush our stream.
     * 
     */
    public void flush()
        {
        if (null != producer)
            {
            producer.flush();
            }
        }

    /**
     * Flush the stream and close our connection
     * 
     */
    public void close()
        {
        if (null != producer)
            {
            producer.flush();
            }
        if (null != producer)
            {
            producer.close();
            }
        }
    }
