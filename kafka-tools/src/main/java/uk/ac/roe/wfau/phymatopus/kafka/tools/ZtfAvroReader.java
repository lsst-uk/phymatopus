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
import ztf.alert;
import ztf.cutout;


/**
 *
 *
 */
@Slf4j
public class ZtfAvroReader
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
     * Bean class for the loop statistics.
     * 
     */
    public static class LoopResult
    implements Statistics
        {
        public LoopResult(long rows, long bytes, long time)
            {
            this.rows  = rows;
            this.bytes = bytes;
            this.time  = time;
            }
        long rows;
        @Override
        public long rows()
            {
            return this.rows;
            }
        long bytes ;
        @Override
        public long bytes()
            {
            return this.bytes;
            }
        long time ;
        @Override
        public long time()
            {
            return this.time;
            }
        }

    
    
    /**
     * Public constructor.
     * @param servers The list of bootstrap Kafka server names.
     * @param group The Kafka client group identifier.
     * @param topic The Kafka topic name.
     *
     */
    public ZtfAvroReader(final Duration timeout, final String servers, final String group, final String topic, final int loops)
        {
        super(servers, group, topic);
        this.timeout = timeout;
        this.loops   = loops;
        }

    private Duration timeout ;
    protected Duration timeout()
        {
        return this.timeout;
        }
    
    private int loops;
    protected int loops()
        {
        return this.loops;
        }
    
    /**
     * Create our {@link Consumer}.
     *
     */
    public Consumer<Long, byte[]> consumer()
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
        // Optimise for large messages.
        // https://community.hortonworks.com/questions/73895/any-experience-based-tips-to-optimize-kafka-broker.html
        properties.put(
            ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,
            "false"
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
                    topic()
                    ),
                this
                );

        long totalbytes   = 0;
        long totalrecords = 0;
        long totalstart   = System.nanoTime();
        long polldone     = 0;
        
        long loopcount = 0;
        for (int i = 0 ; i < this.loops ; i++)
            {
            loopcount++;
            log.trace("Loop [{}]", loopcount);

            long loopstart   = System.nanoTime();
            long loopbytes   = 0;
            long looprecords = 0;
            long uncommitted = 0;

            long pollstart   = 0;
            long pollbytes   = 0;
            long pollcount   = 0;
            long pollrecords = 0;
            do {
                log.trace("Poll [{}]", pollcount);
                pollstart = System.nanoTime();
                pollbytes = 0;
                pollrecords = 0;
                ConsumerRecords<Long, byte[]> records = consumer.poll(
                    this.timeout
                    );
                if (records.isEmpty())
                    {
                    polldone = pollstart;
                    }
                else {
                    for (ConsumerRecord<Long, byte[]> record : records)
                        {
                        pollrecords++;
                        looprecords++;
                        totalrecords++;
                        uncommitted++;
                        log.trace("Record [{}][{}]", pollrecords, totalrecords);
                        log.trace("Offset [{}]", record.offset());
                        log.trace("Key    [{}]", record.key());
                        byte[] bytes = record.value();
                        pollbytes  += bytes.length;
                        loopbytes  += bytes.length;
                        totalbytes += bytes.length;
                        process(
                            bytes
                            );
                        }
                    if (uncommitted > 1000)
                        {
                        log.trace("Committing [{}]", uncommitted);
                        consumer.commitSync();
                        uncommitted = 0;
                        }
                    polldone = System.nanoTime();
                    }
                long pollnano = polldone - pollstart;
                long pollmicro = pollnano / 1000 ;
                long pollmilli = pollnano / 1000000 ;
                log.debug("Poll [{}] done [{}:{}] records [{}:{}] bytes in [{}]ns [{}]µs [{}]ms => [{}]ns [{}]µs [{}]ms per event",
                    pollcount,
                    pollrecords,
                    totalrecords,
                    pollbytes,
                    totalbytes,
                    pollnano,
                    pollmicro,
                    pollmilli,
                    (pollnano/(( pollrecords > 0) ? pollrecords : 1)),
                    (pollmicro/((pollrecords > 0) ? pollrecords : 1)),
                    (pollmilli/((pollrecords > 0) ? pollrecords : 1))
                    );
                pollcount++;
                }
            while (pollrecords > 0);

            log.trace("Loop commit");
            consumer.commitSync();

            long loopnano  = polldone - loopstart;
            long loopmicro = loopnano / 1000 ;
            long loopmilli = loopnano / 1000000 ;
            log.debug("Loop [{}] done [{}:{}] records [{}:{}] bytes in [{}]ns [{}]µs [{}]ms => [{}]ns [{}]µs [{}]ms per event",
                loopcount,
                looprecords,
                totalrecords,
                loopbytes,
                totalbytes,
                loopnano,
                loopmicro,
                loopmilli,
                (loopnano/(( looprecords > 0) ? looprecords : 1)),
                (loopmicro/((looprecords > 0) ? looprecords : 1)),
                (loopmilli/((looprecords > 0) ? looprecords : 1))
                );
            }

        long totalnano  = polldone - totalstart;
        long totalmicro = totalnano / 1000 ;
        long totalmilli = totalnano / 1000000 ;
        long totaltime  = totalnano / 1000000000 ;

        log.info("Total done [{}] [{}] in [{}]ns [{}]µs [{}]ms [{}]s => [{}]ns [{}]µs [{}]ms per event",
            totalrecords,
            totalbytes,
            totalnano,
            totalmicro,
            totalmilli,
            totaltime,
            (totalnano/(( totalrecords > 0) ? totalrecords : 1)),
            (totalmicro/((totalrecords > 0) ? totalrecords : 1)),
            (totalmilli/((totalrecords > 0) ? totalrecords : 1))
            );

        return new LoopResult(
            totalrecords,
            totalbytes,
            totalnano
            ) ;
        }

    public long process(final byte[] bytes)
        {
        log.trace("Hydrating ....");
        long count = 0 ;

        Schema schema = schema(bytes);
        final Long fingerprint = SchemaNormalization.parsingFingerprint64(schema);
        log.trace("Schema fingerprint [{}]", fingerprint);

        DataFileReader<alert> reader = reader(bytes);

        while (reader.hasNext())
            {
            try {
                log.trace("Hydrating alert [{}]", count++);

                //
                // Select the implementation type based on the schema fingerprint ...
                //

                final alert frog = reader.next();

                log.trace("candId    [{}]", frog.getCandid());
                log.trace("objectId  [{}]", frog.getObjectId());
                log.trace("schemavsn [{}]", frog.getSchemavsn().toString());

                final cutout science    = frog.getCutoutScience();
                final cutout template   = frog.getCutoutTemplate();
                final cutout difference = frog.getCutoutDifference();

                if (null != science)
                    {
                    log.trace("science    [{}][{}][{}]", science.getFileName(), science.getStampData().limit(), science.getStampData().capacity());
                    }
                if (null != template)
                    {
                    log.trace("template   [{}][{}][{}]", template.getFileName(), template.getStampData().limit(), template.getStampData().capacity());
                    }
                if (null != difference)
                    {
                    log.trace("difference [{}][{}][{}]", difference.getFileName(), difference.getStampData().limit(), difference.getStampData().capacity());
                    }
                }
            catch (RuntimeException ouch)
                {
                log.error("RuntimeException hydrating alert [{}]", ouch.getMessage());
                }
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
            topic()
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
     * Callable Reader class.
     *
     */
    public static class CallableReader
    implements Callable<Statistics>
        {
        public CallableReader(final Duration timeout, final String servers, final String group, final String topic, final int loops)
            {
            this(
                new ZtfAvroReader(
                    timeout,
                    servers,
                    group,
                    topic,
                    loops
                    )
                );
            }
        
        public CallableReader(final ZtfAvroReader reader)
            {
            this.reader = reader;
            }

        private final ZtfAvroReader reader ;

        public ZtfAvroReader reader()
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
