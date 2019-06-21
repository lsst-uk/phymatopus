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
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Callable;

import org.apache.avro.Schema;
import org.apache.avro.SchemaNormalization;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.SeekableByteArrayInput;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.reflect.ReflectDatumReader;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.LongDeserializer;

import lombok.extern.slf4j.Slf4j;
import uk.ac.roe.wfau.phymatopus.kafka.alert.ZtfAlert;
import uk.ac.roe.wfau.phymatopus.kafka.alert.ZtfAlertWrapper;
import ztf.alert;


/**
 * Reads a series on ZtfAlerts from a stream and stops when there are no more alerts and the poll timeout is reached.
 *
 */
@Slf4j
public class ZtfAlertReader
extends BaseReader
implements ConsumerRebalanceListener
    {

    /**
     * Interface for the loop statistics.
     * 
     */
    public static interface Statistics
        {
        public long alerts();
        public long runtime();
        }

    /**
     * Statistics bean implementation.
     * 
     */
    public static class StatisticsBean
    implements Statistics
        {
        public StatisticsBean(final long alerts, final long runtime)
            {
            this.alerts  = alerts;
            this.runtime = runtime;
            }
        private final long alerts;
        @Override
        public long alerts()
            {
            return this.alerts;
            }
        private final long runtime ;
        @Override
        public long runtime()
            {
            return this.runtime;
            }
        }

    /**
     * Public interface for a reader configuration.
     * 
     */
    public static interface Configuration extends BaseReader.Configuration
        {
        /**
         * The timeout for waiting for new messages.
         * 
         */
        public Duration getLoopTimeout();

        /**
         * The timeout for polling the server.
         * 
         */
        public Duration getPollTimeout();

        }

    /**
     * Configuration bean implementation.
     * 
     */
    @Slf4j
    public static class ConfigurationBean extends BaseReader.ConfigurationBean implements Configuration 
        {
        static final Boolean  DEFAULT_AUTOCOMIT = true ;
        static final Duration DEFAULT_LOOPTIMEOUT = Duration.ofMinutes(10);
        static final Duration DEFAULT_POLLTIMEOUT = Duration.ofSeconds(10);
        
        /**
         * Public constructor.
         * 
         */
        public ConfigurationBean(final String servers, final String topic, final String group)
            {
            this(
                DEFAULT_LOOPTIMEOUT,
                DEFAULT_POLLTIMEOUT,
                servers,
                topic,
                group
                );
            }

        /**
         * Public constructor.
         * 
         */
        public ConfigurationBean(final Duration looptimeout, final Duration polltimeout, final String servers, final String topic, final String group)
            {
            super(
                servers,
                topic,
                group
                );
            this.polltimeout = polltimeout;
            this.looptimeout = looptimeout;
            log.debug("polltimeout [{}]", polltimeout);
            log.debug("looptimeout [{}]", looptimeout);
            }

        private final Duration looptimeout;
        @Override
        public Duration getLoopTimeout()
            {
            return this.looptimeout;
            }

        private final Duration polltimeout;
        @Override
        public Duration getPollTimeout()
            {
            return this.polltimeout;
            }
        }


    /**
     * Public constructor.
     * @param processor The alert processor.
     * @param config The reader configuration.
     *
     */
    public ZtfAlertReader(final ZtfAlert.Processor processor, final Configuration config)
        {
        super(config);
        this.processor = processor ;
        this.config = config;
        }

    /**
     * Our reader configuration.
     * 
     */
    protected Configuration config;

    /**
     * Our alert processor.
     * 
     */
    protected ZtfAlert.Processor processor;

    /**
     * Create our {@link Consumer}.
     *
     */
    public Consumer<Long, byte[]> consumer()
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

    public Statistics loop()
        {
        log.trace("Creating consumer");
        Consumer<Long, byte[]> consumer = consumer();

        log.trace("Subscribing ..");
        consumer.subscribe(
                Collections.singletonList(
                    this.config.getTopic()
                    ),
                this
                );

        long totalalerts = 0;
        long totalstart  = System.nanoTime();
        long totalwait   = 0;

        long loopalerts  = 0;
        long loopstart   = System.nanoTime();
        long loopcount   = 0;
        //long thiswait    = 0;
        long lastwait    = 0;

        long looptimeout = config.getLoopTimeout().toNanos();
        Duration polltimeout = config.getLoopTimeout();

        do {
            loopcount++;
            loopalerts = 0 ;
            loopstart  = System.nanoTime() ; 

            log.trace("Loop [{}][{}]", loopcount, lastwait);

            ConsumerRecords<Long, byte[]> records = consumer.poll(
                polltimeout 
                );

            if (records.isEmpty())
                {
                long thiswait = System.nanoTime() - loopstart ; 
                lastwait  += thiswait ; 
                totalwait += thiswait ; 
                log.debug("Loop wait [{}][{}][{}]", thiswait, lastwait, totalwait );
                }

            else {
                lastwait = 0 ;
                for (ConsumerRecord<Long, byte[]> record : records)
                    {
                    long alertcount = process(
                        record.value()
                        );
                    loopalerts  += alertcount;
                    totalalerts += alertcount;
                    }

                if (loopalerts > 0)
                    {
                    long  loopnano  = System.nanoTime() - loopstart;
                    long  loopmicro = loopnano / 1000 ;
                    float loopmilli = loopnano / 1000000 ;
                    log.debug("Loop [{}] completed [{}:{}] alerts in [{}]ns [{}]µs [{}]ms => [{}]ns [{}]µs [{}]ms per alert",
                        loopcount,
                        loopalerts,
                        totalalerts,
                        loopnano,
                        loopmicro,
                        loopmilli,
                        (loopnano/loopalerts),
                        (loopmicro/loopalerts),
                        (loopmilli/loopalerts)
                        );
                    }
                else {
                    log.debug("Loop wait [{}][{}]", lastwait, totalwait);
                    }
                }
            }
        while (lastwait < looptimeout);

        long  totaltime  = (System.nanoTime() - totalstart) - totalwait ;
        long  totalmicro = totaltime / 1000 ;
        float totalmilli = totaltime / (1000 * 1000) ;
        if (totalalerts > 0)
            {
            log.info("Total : [{}] alerts in [{}]µs [{}]ms  => [{}]µs [{}]ms per alert",
                totalalerts,
                totalmicro,
                totalmilli,
                (totalmicro/totalalerts),
                (totalmilli/totalalerts)
                );
            }
        else {
            log.info("Total : [{}] alerts in [{}]µs [{}]ms",
                totalalerts,
                totalmicro,
                totalmilli
                );
        
            }

        consumer.close();
        
        return new StatisticsBean(
            totalalerts,
            totaltime
            ) ;
        }
    
    public long process(final byte[] bytes)
        {
        log.trace("Processing byte[]");

        DebugFormatter debug = new DebugFormatter();
        debug.asciiBytes(bytes);
        
        Schema schema = schema(bytes);
        final Long fingerprint = SchemaNormalization.parsingFingerprint64(schema);
        log.trace("Schema fingerprint [{}]", fingerprint);

        debug.asciiBytes(bytes);
        DataFileReader<alert> reader = reader(bytes);

        long alertcount = 0 ;
        while (reader.hasNext())
            {
            try {
                log.trace("Hydrating alert [{}]", alertcount);
                ztf.alert alert = reader.next();
                log.trace("Processing alert [{}]", alertcount);
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

    public Schema schema(final byte[] bytes)
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

    public DataFileReader<alert> reader(final byte[] bytes)
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

    public void rewind()
        {
        log.trace("Creating consumer");
        Consumer<Long, byte[]> consumer = consumer();

        log.trace("Fetching PartitionInfo for topic");
        List<PartitionInfo> partitions = consumer.partitionsFor(
            this.config.getTopic()
            );

        log.trace("Creating TopicPartitions");
        List<TopicPartition> topicpartitions = new ArrayList<TopicPartition>();
        for(PartitionInfo partition : partitions)
            {
            log.trace("Partition [{}][{}]", partition.topic(), partition.partition());
            topicpartitions.add(
                new TopicPartition(
                    partition.topic(),
                    partition.partition()
                    )
                );
            }
        log.trace("Fetching beginning offsets for partitions");
        Map<TopicPartition,Long> offsets = consumer.beginningOffsets(
            topicpartitions
            );

        log.trace("Creating TopicPartition map");
        Map<TopicPartition, OffsetAndMetadata> offsetmeta = new HashMap<TopicPartition, OffsetAndMetadata>();
        for (TopicPartition partition : offsets.keySet())
            {
            offsetmeta.put(
                partition,
                new OffsetAndMetadata(
                    offsets.get(
                        partition
                        )
                    )
                );
            }

        log.trace("Committing ...");
        consumer.commitSync(offsetmeta);

        log.trace("Closing ...");
        consumer.close();
        }

    @Override
    public void onPartitionsAssigned(Collection<TopicPartition> partitions)
        {
        log.trace("PartitionsAssigned()");
        for (TopicPartition partition : partitions)
            {
            log.trace("Partition [{}],[{}]", partition.topic(), partition.partition());
            }
        }

    @Override
    public void onPartitionsRevoked(Collection<TopicPartition> partitions)
        {
        log.trace("PartitionsRevoked()");
        for (TopicPartition partition : partitions)
            {
            log.trace("Partition [{}],[{}]", partition.topic(), partition.partition());
            }
        }


    
    /**
     * Callable Reader wrapper class.
     *
     */
    public static class CallableReader
    implements Callable<Statistics>
        {
        public CallableReader(final ZtfAlert.Processor processor, final Configuration config)
            {
            this(
                new ZtfAlertReader(
                    processor,
                    config
                    )
                );
            }
        
        public CallableReader(final ZtfAlertReader reader)
            {
            this.reader = reader;
            }

        private final ZtfAlertReader reader ;

        public ZtfAlertReader reader()
            {
            return this.reader ;
            }

        public void rewind()
            {
            this.reader.rewind();
            }

        public Statistics call()
            {
            return reader.loop();
            }
        }
    }
