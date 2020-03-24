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

package uk.ac.roe.wfau.phymatopus.kafka;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.LongDeserializer;

import io.confluent.kafka.schemaregistry.client.MockSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.rest.exceptions.RestClientException;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import lombok.extern.slf4j.Slf4j;
import uk.ac.roe.wfau.phymatopus.alert.AlertProcessor;
import uk.ac.roe.wfau.phymatopus.alert.BaseAlert;
import uk.ac.roe.wfau.phymatopus.avro.record.AvroRecordAlertWrapper;
import ztf.alert;


/**
 * A {@link KafkaAlertReader} that reads Alerts from a Kafka Avro stream using a registered schema.
 *
 */
@Slf4j
public class KafkaObjectReader
extends GenericKafkaReader<Long, Object, BaseAlert>
implements KafkaAlertReader
    {

    /**
     * Public constructor.
     * @param processor The alert processor.
     * @param config The reader configuration.
     *
     */
    public KafkaObjectReader(final AlertProcessor<BaseAlert> processor, final Configuration config)
        {
        super(
            processor,
            config
            );
        }

    /**
     * A Deserializer for our key type, {@link Long}.
     *
     */
    protected Deserializer<Long> indexDeserializer(final Map<String, Object> config)
        {
        LongDeserializer deser = new LongDeserializer();
        deser.configure(
            config,
            true // True because this is the Deserializer for keys.
            );  
        return deser ;
        }

    /**
     * A Deserializer for our message type, {@link Object}.
     * At the moment this uses a {@link MockSchemaRegistryClient},
     * later this should use the full SchemaRegistryClient.
     *
     */
    protected Deserializer<Object> dataDeserializer(final Map<String, Object> config)
        {
        SchemaRegistryClient registry = new MockSchemaRegistryClient();
        try {
            registry.register(
                "alert-schema",
                alert.SCHEMA$
                );
            }
        catch (IOException ouch)
            {
            log.error("IOException registering schema [{}]", ouch.getMessage());
            throw new RuntimeException(
                ouch
                );
            }
        catch (RestClientException ouch)
            {
            log.error("RestClientException registering schema [{}]", ouch.getMessage());
            throw new RuntimeException(
                ouch
                );
            }
        KafkaAvroDeserializer deser = new KafkaAvroDeserializer(
            registry
            );
        deser.configure(
            config,
            false // False because this is the Deserializer for data.
            ); 
        return deser;
        }

    /**
     * Get our Avro {@link Schema}.
     *
     */
    protected Schema schema()
        {
        return alert.SCHEMA$;
        }

    @Override
    public Consumer<Long, Object> consumer()
        {
        final Map<String, Object> config = new HashMap<>();
        config.put(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
            this.config.getServers()
            );
        config.put(
            ConsumerConfig.GROUP_ID_CONFIG,
            this.config.getGroup()
            );
        config.put(
            KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG,
            "urn:mock"
            );
        config.put(
            KafkaAvroDeserializerConfig.AUTO_REGISTER_SCHEMAS,
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

    @Override
    protected long process(final Object object)
        {
        long alertcount = 0 ;
        try {
            alertcount++;
            processor.process(
                new AvroRecordAlertWrapper(
                    (GenericData.Record) object,
                    config.getTopic()
                    )
                );
            }
        catch (Exception ouch)
            {
            log.error("Exception processing alert [{}][{}]", ouch.getClass().getName(), ouch.getMessage());
            if (ouch.getCause() != null)
                {
                Throwable cause = ouch.getCause();
                log.error("Exception cause [{}][{}]", cause.getClass().getName(), cause.getMessage());
                }
            throw new RuntimeException(
                ouch
                );
            }
        return alertcount ;
        }

    /**
     * Create a {@link CallableAlertReader}.
     *  
     */
    public static CallableAlertReader callable(final AlertProcessor<BaseAlert> processor, final Configuration config)
        {
        return new CallableAlertReader()
            {
            private final KafkaObjectReader reader = new KafkaObjectReader(
                processor,
                config
                ); 
            @Override
            public LoopStats call()
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

    public static class Factory
    implements KafkaAlertReader.Factory
        {
        @Override
        public KafkaObjectReader reader(AlertProcessor<BaseAlert> processor, final Configuration config)
            {
            return new KafkaObjectReader(
                processor,
                config
                ); 
            }
        @Override
        public CallableAlertReader callable(AlertProcessor<BaseAlert> processor, final Configuration config)
            {
            return KafkaObjectReader .callable(
                processor,
                config
                );
            }
        }
    }


