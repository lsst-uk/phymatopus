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

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Parser;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.ByteBufferDeserializer;
import org.apache.kafka.common.serialization.LongDeserializer;

import io.confluent.kafka.schemaregistry.client.MockSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import lombok.extern.slf4j.Slf4j;

import ztf.alert;
import ztf.cutout;

/**
 * 
 * 
 */
@Slf4j
public class ZtfAvroReader
extends BaseReader
    {
    
    /**
     * Public constructor.
     * @param servers The list of bootstrap Kafka server names.
     * @param group The Kafka client group identifier.
     * @param topic The Kafka topic name.
     * 
     */
    public ZtfAvroReader(final String servers, final String group, final String topic)
        {
        super(servers, group, topic);
        }

    /*
    private String[] schemaNames = {
        "cutout",
        "candidate",
        "prv_candidate",    
        "alert"
        };

    private Parser parser = new Parser();

    public void init()
        {
        for(String schemaName : schemaNames)
            {
            final String schemaFileName = "/" + schemaName + ".avsc" ;
            log.debug("Openning schema file [{}]", schemaFileName);
            final InputStream stream = this.getClass().getResourceAsStream(
                schemaFileName
                );
            if (null == stream)
                {
                log.error("Unable to find schema file [{}]", schemaFileName);
                break ;
                }
            else {
                try {
                    // TODO test out errors we get from bad data ...
                    log.debug("Parsing schema file [{}]", schemaFileName);
                    parser.parse(
                        stream
                        );
                    }
                catch (final IOException ouch)
                    {
                    log.error("IOException processing schema file [{}]", schemaFileName);                
                    break ;
                    }
                finally {
                    try {
                        stream.close();
                        }
                    catch (final IOException ouch)
                        {
                        log.error("IOException closing schema file [{}]", schemaFileName);                
                        }
                    }
                }
            }
        }

    public Map<String, Schema> schemas()
        {
        return this.parser.getTypes();
        }

    public Schema schema()
        {
        return this.parser.getTypes().get(
            "ztf.alert"
            );
        }
     */

    /**
     * Create our {@link SchemaRegistryClient}.
     * 
    public SchemaRegistryClient registry()
        {
        final SchemaRegistryClient registry = new MockSchemaRegistryClient(); 
        try {
            registry.register(
                "ztf.alert",
                this.schema()
                );
            }
        catch (Exception ouch)
            {
            log.error("Exception while registering schema []", ouch.getMessage());
            }
        return registry;
        }
     */
    
    /**
     * Create our {@link Consumer}.
     * 
     */
    public Consumer<Long, ByteBuffer> consumer()
        {
        final Properties properties = new Properties();
        properties.put(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
            servers()
            );
        properties.put(
            ConsumerConfig.GROUP_ID_CONFIG,
            group()
            );
        properties.put(
            ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG,
            "1000"
            );
        properties.put(
            ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,
            "true"
            );
        /*
         * 
        properties.put(
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
            null
            );
        properties.put(
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
            KafkaAvroDeserializer.class.getName()
            );
 *         
 */

        /*
         * 
        final Consumer<Object, Object> consumer = new KafkaConsumer<Object, Object>(
            properties,
            new KafkaAvroDeserializer(
                registry()
                ),
            new KafkaAvroDeserializer(
                registry()
                )
            );
         * 
         */

        final Consumer<Long, ByteBuffer> consumer = new KafkaConsumer<Long, ByteBuffer>(
                properties,
                new LongDeserializer(),
                new ByteBufferDeserializer()
                );
        
        return consumer;
        }    

    
    public void loop(int count, int wait)
        {
        Consumer<Long, ByteBuffer> consumer = consumer(); 

        log.debug("Subscribing ..");
        consumer.subscribe(
                Collections.singletonList(
                    topic()
                    )
                );

        log.debug("Seeking ..");
        consumer.seekToBeginning(
            consumer.assignment()
            );
        
        log.debug("Looping ..");
        for (int i = 0 ; i < count ; i++)
            {
            log.debug("Polling ..");
            ConsumerRecords<Long, ByteBuffer> records = consumer.poll(
                Duration.ofSeconds(
                    wait
                    )
                );
            
            for (ConsumerRecord<Long, ByteBuffer> record : records)
                {
                log.debug("----");
                log.debug("Offset [{}]", record.offset());
                log.debug("Key    [{}]", record.key());
                process(
                    record.value()
                    );
                }        
            }
        }

    
    /*
     * Issues -
     * ZTF Python producer just adds raw Avro, with no schema.
     * The Confluent deserializer expects schema registry magic bytes at the start of the message.
     * https://groups.google.com/forum/#!topic/confluent-platform/9LZh3JvnTtI
     * 
     * IF we owned the producer, we could add the Schema-Registry bytes to our message.
     * https://stackoverflow.com/questions/39489649/encoding-formatting-issues-with-python-kafka-library
     * 
     * There is a Python Schema-Registry ..
     * https://github.com/verisign/python-confluent-schemaregistry
     * 
     */
    
    public void process(final ByteBuffer buffer)
        {
        try {
            log.debug("Hydrating ....");
            final alert frog = alert.fromByteBuffer(buffer);

            log.debug("candId    [{}]", frog.getCandid());
            log.debug("objectId  [{}]", frog.getObjectId());
            log.debug("schemavsn [{}]", frog.getSchemavsn().toString());
        
            final cutout science    = frog.getCutoutScience();
            final cutout template   = frog.getCutoutTemplate();
            final cutout difference = frog.getCutoutDifference();

            if (null != science)
                {
                log.debug("science    [{}][{}][{}]", science.getFileName(), science.getStampData().limit(), science.getStampData().capacity());
                }
            if (null != template)
                {
                log.debug("template   [{}][{}][{}]", template.getFileName(), template.getStampData().limit(), template.getStampData().capacity());
                }
            if (null != difference)
                {
                log.debug("difference [{}][{}][{}]", difference.getFileName(), difference.getStampData().limit(), difference.getStampData().capacity());
                }
            }
        catch (IOException ouch)
            {
            log.error("IOException hydrating Alert [{}]", ouch.getMessage());
            }
        }
    
    
    // Read test data from files to check ..
    // Read the message as a byte[] and tweak the headers ..
    // Save the generated class and tweak the schema ...

    
    
    
    
    }
