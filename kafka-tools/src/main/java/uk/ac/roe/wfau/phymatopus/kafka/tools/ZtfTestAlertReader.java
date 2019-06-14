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
public class ZtfTestAlertReader
extends BaseReader
implements ConsumerRebalanceListener
    {

    /**
     * Interface for the loop statistics.
     * 
     */
    public static interface Statistics
        {
        public long rows();
        public long bytes();
        public long time();
        }

    /**
     * Statistics bean implementation.
     * 
     */
    public static class StatisticsBean
    implements Statistics
        {
        public StatisticsBean(final long rows, final long bytes, final long time)
            {
            this.rows  = rows;
            this.bytes = bytes;
            this.time  = time;
            }
        private final long rows;
        @Override
        public long rows()
            {
            return this.rows;
            }
        private final long bytes ;
        @Override
        public long bytes()
            {
            return this.bytes;
            }
        private final long time ;
        @Override
        public long time()
            {
            return this.time;
            }
        }

    /**
     * Public interface for a reader configuration.
     * 
     */
    public static interface Configuration extends BaseReader.Configuration
        {
        /**
         * The auto-commit flag.
         *
         */
        public Boolean getAutocommit();

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
                DEFAULT_AUTOCOMIT,
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
        public ConfigurationBean(final Boolean autocommit, final Duration looptimeout, final Duration polltimeout, final String servers, final String topic, final String group)
            {
            super(
                servers,
                topic,
                group
                );
            this.autocommit  = autocommit;
            this.polltimeout = polltimeout;
            this.looptimeout = looptimeout;
            log.debug("autocommit  [{}]", autocommit);
            log.debug("polltimeout [{}]", polltimeout);
            log.debug("looptimeout [{}]", looptimeout);
            }

        private final Boolean autocommit;
        @Override
        public Boolean getAutocommit()
            {
            return this.autocommit;
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
    public ZtfTestAlertReader(final ZtfAlert.Processor processor, final Configuration config)
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
            this.config.getAutocommit().toString()
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

        long totalbytes   = 0;
        long totalalerts  = 0;
        long totalrecords = 0;
        long totalstart   = System.nanoTime();
        long totalwait    = 0;

        long loopstart  = 0 ;
        long loopalerts = 0 ;
        long loopcount  = 0 ;
        long loopwait   = 0;

        long looptimeout = config.getLoopTimeout().toNanos();
        Duration polltimeout = config.getLoopTimeout();
        long uncommitted = 0 ;

        do {
            loopcount++;
            if (loopwait == 0)
                {
                loopstart = System.nanoTime();
                }
            log.trace("Loop [{}][{}]", loopcount, loopwait);

            long loopbytes   = 0;
            long looprecords = 0;

            ConsumerRecords<Long, byte[]> records = consumer.poll(
                polltimeout 
                );

            if (records.isEmpty())
                {
                loopwait = System.nanoTime() - loopstart ; 
                }

            else {
                totalwait += loopwait ; 
                loopwait   = 0 ;
                for (ConsumerRecord<Long, byte[]> record : records)
                    {
                    looprecords++;
                    totalrecords++;
                    log.trace("Record [{}][{}]", looprecords, totalrecords);
                    log.trace("Offset [{}]", record.offset());
                    log.trace("Key    [{}]", record.key());

                    byte[] bytearray = record.value(); 
                    loopbytes  = bytearray.length; 
                    totalbytes += loopbytes ;
                    
                    loopalerts = process(
                        bytearray 
                        );

                    totalalerts += loopalerts ;
                    uncommitted += loopalerts ;
                    
                    if ((this.config.getAutocommit() == false) && (uncommitted > 1000))
                        {
                        log.trace("Committing [{}]", uncommitted);
                        consumer.commitSync();
                        uncommitted = 0;
                        }
                    }
                }

            if (looprecords > 0)
                {
                long  loopnano  = System.nanoTime() - loopstart;
                long  loopmicro = loopnano / 1000 ;
                float loopmilli = loopnano / 1000000 ;
                log.debug("Loop [{}] completed [{}:{}] alerts [{}:{}] bytes in [{}]ns [{}]µs [{}]ms => [{}]ns [{}]µs [{}]ms per event",
                    loopcount,
                    loopalerts,
                    totalalerts,
                    loopbytes,
                    totalbytes,
                    loopnano,
                    loopmicro,
                    loopmilli,
                    (loopnano/looprecords),
                    (loopmicro/looprecords),
                    (loopmilli/looprecords)
                    );
                }
            else {
                log.debug("Loop wait [{}] loop timeout [{}]", loopwait, looptimeout);
                }
            }
        while (loopwait < looptimeout);

        totalwait += loopwait ; 

        if ((this.config.getAutocommit() == false) && (uncommitted > 0))
            {
            log.trace("Committing [{}]", uncommitted);
            consumer.commitSync();
            }

        long  totalnano  = System.nanoTime() - (totalstart + totalwait) ;
        long  totalmicro = totalnano / 1000 ;
        float totalmilli = totalnano / 1000000 ;
        if (totalrecords > 0)
            {
            log.info("Total : [{}] alerts in [{}]ns [{}]µs [{}]ms  => [{}]ns [{}]µs [{}]ms per alert",
                totalalerts,
                totalnano,
                totalmicro,
                totalmilli,
                (totalnano/totalrecords),
                (totalmicro/totalrecords),
                (totalmilli/totalrecords)
                );
            }
        else {
            log.info("Total : [{}] alerts in [{}]ns [{}]µs [{}]ms",
                totalalerts,
                totalnano,
                totalmicro,
                totalmilli
                );
        
            }
        return new StatisticsBean(
            totalrecords,
            totalbytes,
            totalnano
            ) ;
        }

    
    public long process(final byte[] bytes)
        {
        log.trace("Processing byte[]");

        Schema schema = schema(bytes);
        final Long fingerprint = SchemaNormalization.parsingFingerprint64(schema);
        log.trace("Schema fingerprint [{}]", fingerprint);

        DataFileReader<alert> reader = reader(bytes);

        long count = 0 ;
        while (reader.hasNext())
            {
            try {
                log.trace("Hydrating alert [{}]", count);
                ztf.alert alert = reader.next();
                log.trace("Processing alert [{}]", count);
                try {
                    processor.process(
                        new ZtfAlertWrapper(
                            alert
                            )
                        );
                    }
                catch (Exception ouch)
                    {
                    log.error("Exception processing alert [{}]", ouch.getMessage());
                    }
                }
            catch (Exception ouch)
                {
                log.error("Exception hydrating alert [{}]", ouch.getMessage());
                }
            count++;
            }
        return count ;
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

    public void hexBytes(byte[] bytes)
        {
        for (int i = 0 ; i < 0x10 ; i++)
            {
            StringBuffer string = new StringBuffer();
            Formatter formatter = new Formatter(string);
            for (int j = 0 ; j < 0x10 ; j++)
                {
                int k = (i * 0x10) + j;
                formatter.format(
                    "%s%02X",
                    ((j > 0) ? " " : ""),
                    bytes[k]
                    );
                }
            formatter.close();
            log.trace("Bytes [{}]", string.toString());
            }
        }

    public void asciiBytes(byte[] bytes)
        {
        for (int i = 0 ; i < 0x10 ; i++)
            {
            StringBuffer string = new StringBuffer();
            Formatter formatter = new Formatter(string);
            for (int j = 0 ; j < 0x10 ; j++)
                {
                int k = (i * 0x10) + j;
                char ascii ;
                if ((k < bytes.length) && (bytes[k] >= ' ') && (bytes[k] <= '~'))
                    {
                    ascii = (char) bytes[k];
                    }
                else {
                    ascii = '.';
                    }
                formatter.format(
                    "%s%c",
                    ((j > 0) ? " " : ""),
                    ascii
                    );
                }
            formatter.close();
            log.trace("ASCII [{}]", string.toString());
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
                new ZtfTestAlertReader(
                    processor,
                    config
                    )
                );
            }
        
        public CallableReader(final ZtfTestAlertReader reader)
            {
            this.reader = reader;
            }

        private final ZtfTestAlertReader reader ;

        public ZtfTestAlertReader reader()
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
