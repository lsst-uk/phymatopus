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

package uk.ac.roe.wfau.phymatopus.kafka.alert.ztf;

import java.io.IOException;
import java.util.Properties;

import org.apache.avro.Schema;
import org.apache.avro.SchemaNormalization;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.SeekableByteArrayInput;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.reflect.ReflectDatumReader;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.LongDeserializer;

import lombok.extern.slf4j.Slf4j;
import uk.ac.roe.wfau.phymatopus.alert.AlertProcessor;
import uk.ac.roe.wfau.phymatopus.alert.AlertReader;
import uk.ac.roe.wfau.phymatopus.alert.BaseAlert;
import uk.ac.roe.wfau.phymatopus.avro.ztf.ZtfAlertWrapper;
import uk.ac.roe.wfau.phymatopus.kafka.alert.GenericAlertReader;
import uk.ac.roe.wfau.phymatopus.kafka.tools.DebugFormatter;
import ztf.alert;

/**
 * Reads a series on ZtfAlerts from a stream and stops when there are no more alerts and the poll timeout is reached.
 *
 */
@Slf4j
public class ZtfAlertReader
extends GenericAlertReader<Long, byte[], BaseAlert>
implements AlertReader
    {

    /**
     * Public constructor.
     * @param processor The alert processor.
     * @param config The reader configuration.
     *
     */
    public ZtfAlertReader(final AlertProcessor<BaseAlert> processor, final Configuration config)
        {
        super(
            processor,
            config
            );
        }

    @Override
    protected Consumer<Long, byte[]> consumer()
        {
        final Properties properties = new Properties();
        properties.put(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
            this.config.getServers()
            );
        properties.put(
            ConsumerConfig.GROUP_ID_CONFIG,
            this.config.getGroup()
            );
        properties.put(
            ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG,
            "1000"
            );
        // Optimise for large messages.
        // https://community.hortonworks.com/questions/73895/any-experience-based-tips-to-optimize-kafka-broker.html
        properties.put(
            ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,
            Boolean.TRUE.toString()
            );
        properties.put(
            ConsumerConfig.FETCH_MAX_BYTES_CONFIG,
            Integer.toString(
                52428800 * 4
                )
            );
        properties.put(
            ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG,
            Integer.toString(
                1048576 * 4
                )
            );
        properties.put(
            ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
            "earliest"
            );

        final KafkaConsumer<Long, byte[]> consumer = new KafkaConsumer<Long, byte[]>(
            properties,
            new LongDeserializer(),
            new ByteArrayDeserializer()
            );

        return consumer;
        }

    @Override
    protected long process(final byte[] bytes)
        {
        log.trace("Processing byte[]");
/*
 * We already know the schema.
 * If we move to using the SchemaRegistry, then the schema won't be in the message. 
        DebugFormatter debug = new DebugFormatter();
        debug.asciiBytes(bytes);
        
        Schema schema = schema(bytes);
        final Long fingerprint = SchemaNormalization.parsingFingerprint64(schema);
        log.trace("Schema fingerprint [{}]", fingerprint);

        debug.asciiBytes(bytes);
 * 
 */
        DataFileReader<alert> reader = reader(bytes);

        long alertcount = 0 ;
        while (reader.hasNext())
            {
            try {
                //log.trace("Hydrating alert [{}]", alertcount);
                ztf.alert alert = reader.next();
                //log.trace("Processing alert [{}]", alertcount);
                try {
                    processor.process(
                        new ZtfAlertWrapper(
                            alert,
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
                }
            catch (Exception ouch)
                {
                log.error("Exception hydrating alert [{}][{}]", ouch.getClass().getName(), ouch.getMessage());
                if (ouch.getCause() != null)
                    {
                    Throwable cause = ouch.getCause();
                    log.error("Exception cause [{}][{}]", cause.getClass().getName(), cause.getMessage());
                    }
                }
            alertcount++;
            }
        return alertcount ;
        }

    /**
     * Extract our schema from an array of bytes.
     * 
     */
    protected Schema schema(final byte[] bytes)
        {
        log.trace("Extracting message schema");
        DataFileReader<Object> reader = null;
        Schema schema = null ;
        try {
            reader = new DataFileReader<Object>(
                new SeekableByteArrayInput(
                    bytes
                    ),
                new GenericDatumReader<Object>()
                );
            schema = reader.getSchema();
            log.trace("Message schema [{}][{}]", schema.getFullName(), schema.getDoc());
            }
        catch (IOException ouch)
            {
            log.error("IOException creating reader [{}]", ouch.getMessage());
            return null ;
            }
        finally {
            if (null != reader)
                {
                try {
                    reader.close();
                    }
                catch (Exception ouch)
                    {
                    log.error("Exception closing reader [{}]", ouch.getMessage());
                    }
                }
            }
        return schema;
        }

    /**
     * Create a DataFileReader for the alert data type.
     * 
     */
    protected DataFileReader<alert> reader(final byte[] bytes)
        {
        log.trace("Creating alert reader");
        try {
            return new DataFileReader<alert>(
                new SeekableByteArrayInput(
                    bytes
                    ),
                new ReflectDatumReader<alert>(
                    alert.class
                    )
                );
            }
        catch (IOException ouch)
            {
            log.error("IOException creating reader [{}]", ouch.getMessage());
            return null ;
            }
        }

    /**
     * Create a Callable reader.
     * 
     */
    public static CallableAlertReader callable(final AlertProcessor<BaseAlert> processor, final Configuration config)
        {
        return new CallableAlertReader()
            {
            private final ZtfAlertReader reader = new ZtfAlertReader(
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
