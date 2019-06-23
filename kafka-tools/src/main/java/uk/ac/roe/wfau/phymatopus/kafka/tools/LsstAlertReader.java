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
import java.util.HashMap;
import java.util.Map;

import org.apache.avro.Schema;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.LongDeserializer;

import io.confluent.kafka.schemaregistry.client.MockSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.rest.exceptions.RestClientException;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;
import lombok.extern.slf4j.Slf4j;
import uk.ac.roe.wfau.phymatopus.kafka.alert.AlertProcessor;
import uk.ac.roe.wfau.phymatopus.kafka.alert.ZtfAlert;
import uk.ac.roe.wfau.phymatopus.kafka.alert.ZtfAlertWrapper;
import ztf.alert;


/**
 * Reads a series on ZtfAlerts from a stream and stops when there are no more alerts and the poll timeout is reached.
 *
 */
@Slf4j
public class LsstAlertReader
extends GenericAlertReader<Long, Object, ZtfAlert>
implements AlertReader
    {

    /**
     * Public constructor.
     * @param processor The alert processor.
     * @param config The reader configuration.
     *
     */
    public LsstAlertReader(final AlertProcessor<ZtfAlert> processor, final Configuration config)
        {
        super(
            processor,
            config
            );
        }

    /**
     * A Deserializer for our index type.
     *
     */
    public Deserializer<Long> indexDeserializer(final Map<String, Object> config)
        {
        LongDeserializer  result = new LongDeserializer();
        result.configure(config, true); // True because this is the Deserializer for keys. 
        return result ;
        }

    /**
     * A Deserializer for our data type.
     *
     */
    public Deserializer<Object> dataDeserializer(final Map<String, Object> config)
        {
        SchemaRegistryClient  reg = new MockSchemaRegistryClient();
        try {
            reg.register(
                "alert-schema",
                alert.SCHEMA$
                );
            }
        catch (IOException ouch)
            {
            log.error("IOException registering schema [{}]", ouch.getMessage());
            log.error("IOException registering schema ", ouch);
            }
        catch (RestClientException ouch)
            {
            log.error("RestClientException registering schema [{}]", ouch.getMessage());
            log.error("RestClientException registering schema ", ouch);
            }
        KafkaAvroDeserializer ser = new KafkaAvroDeserializer(
            reg
            );
        ser.configure(config, false); // False because this is the Deserializer for data.
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
     * Create our {@link Consumer}.
     *
     */
    public Consumer<Long, Object> consumer()
        {
        final Map<String, Object> config = new HashMap<>();
        config.put(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
            this.config.getServers()
            );
        config.put(
            ProducerConfig.CLIENT_ID_CONFIG,
            this.config.getGroup()
            );
        config.put(
            KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG,
            "urn:mock"
            );
        config.put(
            KafkaAvroSerializerConfig.AUTO_REGISTER_SCHEMAS,
            true
            );
        config.put(
            ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG,
            "1000"
            );
        // Optimise for large messages.
        // https://community.hortonworks.com/questions/73895/any-experience-based-tips-to-optimize-kafka-broker.html
        config.put(
            ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,
            Boolean.TRUE.toString()
            );
        config.put(
            ConsumerConfig.FETCH_MAX_BYTES_CONFIG,
            Integer.toString(
                52428800 * 4
                )
            );
        config.put(
            ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG,
            Integer.toString(
                1048576 * 4
                )
            );
        config.put(
            ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
            "earliest"
            );
        KafkaConsumer<Long, Object> consumer = new KafkaConsumer<Long, Object>(
            config,
            indexDeserializer(
                config
                ),
            dataDeserializer(
                config
                )
            );
        return consumer;
        }

    public long process(final Object object)
        {
        log.trace("Processing Object");
        long alertcount = 0 ;
        try {
            alertcount++;
            processor.process(
                new ZtfAlertWrapper(
                    (alert) object,
                    this.config.getTopic()
                    )
                );
            }
        catch (Exception ouch)
            {
            log.error("Exception processing alert [{}][{}]", ouch.getClass().getName(), ouch.getMessage());
            log.error("Exception processing alert ", ouch);
            if (ouch.getCause() != null)
                {
                Throwable cause = ouch.getCause();
                log.error("Exception cause [{}][{}]", cause.getClass().getName(), cause.getMessage());
                }
            }
        return alertcount ;
        }

    /**
     * Create a Callable reader.
     * 
     */
    public static CallableAlertReader callable(final AlertProcessor<ZtfAlert> processor, final Configuration config)
        {
        return new CallableAlertReader()
            {
            private final LsstAlertReader reader = new LsstAlertReader(
                processor,
                config
                ); 
            @Override
            public ReaderStatistics call()
                {
                return reader.loop();
                }
            @Override
            public void rewind()
                {
                reader.rewind();
                }
            };
        }
    }
