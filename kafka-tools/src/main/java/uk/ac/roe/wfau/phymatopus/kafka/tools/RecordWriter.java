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
import org.apache.avro.SchemaBuilder ;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecordBuilder;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.LongSerializer;

import io.confluent.kafka.schemaregistry.client.MockSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import lombok.extern.slf4j.Slf4j;

/**
 * First attempt at a schema structure writer.
 * 
 */
@Slf4j
public class RecordWriter
extends BaseClient
    {

    /**
     * Public constructor.
     * 
     */
    public RecordWriter(final Configuration config)
        {
        super(
            config
            );
        }
    
    /**
     * Create our {@link Producer}. 
     * 
     */
    protected Producer<Long, Object> producer()
        {
        Properties properties = new Properties();
        properties.put(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
            this.config.getServers()
            );
        properties.put(
            ProducerConfig.CLIENT_ID_CONFIG,
            "AvroExampleProducer"
            );

        SchemaRegistryClient reg = new MockSchemaRegistryClient();
        KafkaAvroSerializer ser = new KafkaAvroSerializer(
            reg
            );
        return new KafkaProducer<Long, Object>(
            properties,
            new LongSerializer(),
            ser
            );
        }

    /**
     * Our Avro {@link Schema}.
     * 
     */
    protected Schema schema;
    protected Schema schema()
        {
        if (null == schema)
            {
            schema = SchemaBuilder.builder()
                .record("test001")
                .namespace("uk.ac.roe.wfau.phymatopus")
                .fields()
                    .requiredDouble("ra")
                    .requiredDouble("dec")
                    .optionalDouble("cx")
                    .optionalDouble("cy")
                    .optionalDouble("cz")
                    .endRecord();
            }
        return schema ;
        }

    /**
     * Create a new Avro {@link GenericData.Record}.
     * 
     */
    protected GenericData.Record record()
        {
        final GenericRecordBuilder builder = new GenericRecordBuilder(
            schema()
            );
        builder.set("ra",  new Double(1.0));
        builder.set("dec", new Double(2.0));

        builder.set("cx", new Double(3.0));
        builder.set("cy", new Double(4.0));
        builder.set("cz", new Double(5.0));
        
        return builder.build();
        }
    
    /**
     * Write a series of records to the stream. 
     * 
     */
    public void write(final int count)
        {
        log.debug("Creating Producer<Long, String>");
        final Producer<Long, Object> producer = producer(); 
        
        log.debug("Starting write [{}]", count);
        long start = System.currentTimeMillis();
        
        try {
            for (int index = 0 ; index < count ; index++)
                {
                log.debug("Loop [{}]", index);
                final ProducerRecord<Long, Object> record = new ProducerRecord<Long, Object>(
                    this.config.getTopic(),
                    (start + index),
                    record()
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
        catch (Exception ouch)
            {
            log.debug("Exception during loop", ouch);
            }
        finally {
            producer.flush();
            producer.close();
            }
        }
    }
