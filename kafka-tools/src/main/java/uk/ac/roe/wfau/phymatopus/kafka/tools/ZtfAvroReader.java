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

import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.SeekableByteArrayInput;
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

        final KafkaConsumer<Long, byte[]> consumer = new KafkaConsumer<Long, byte[]>(
                properties,
                new LongDeserializer(),
                new ByteArrayDeserializer()
                );
        
        return consumer;
        }    

    public void loop(int loops, Duration timeout)
        {
        log.debug("Creating consumer");
        Consumer<Long, byte[]> consumer = consumer(); 

        log.debug("Subscribing ..");
        consumer.subscribe(
                Collections.singletonList(
                    topic()
                    ),
                this
                );
        
        //List<PartitionInfo> partitionsFor(java.lang.String topic)
        
        /*
         * https://stackoverflow.com/a/40017688
         * 
        log.debug("First poll ..");
        ConsumerRecords<Long, byte[]> skip = consumer.poll(
            Duration.ofSeconds(20)
            );
         */ 

        /*
         * 
        //
        // Get the ConsumerCoordinator from the private field of the Consumer:
        ConsumerCoordinator coordinator = (ConsumerCoordinator) FieldUtils.readField(consumer, "coordinator", true);

        // Force partition assignment.
        coordinator.ensureActiveGroup();
        
        //ensurePartitionAssignment();
        // Get the list of partitions assigned to this specific consumer:
        Set<TopicPartition> assigned = consumer.assignment();

        // Now we can call seek(), sequined() or seekToBeginning()) on those partitions only for this consumer as above.
         * 
         */

        /*
        log.debug("Seeking ..");
        consumer.seekToBeginning(
            consumer.assignment()
            );
        log.debug("Committing ..");
        consumer.commitSync();
         */
        
        long loopcount = 0 ;
        long recordcount = 0 ;
        long recordtotal = 0 ;
        long bytecount = 0 ;
        long bytetotal = 0 ;
        long start = System.nanoTime();
        long uncommitted = 0 ;
        
        log.debug("Looping ..");
        for (int i = 0 ; i < loops ; i++)
            {
            log.debug("Loop [{}]", loopcount++);
            do {
                log.debug("Polling ..");
                recordcount = 0;
                ConsumerRecords<Long, byte[]> records = consumer.poll(
                    timeout
                    );
                for (ConsumerRecord<Long, byte[]> record : records)
                    {
                    recordcount++;
                    recordtotal++;
                    uncommitted++;
                    log.debug("Record [{}]", recordcount);
                    log.debug("Offset [{}]", record.offset());
                    log.debug("Key    [{}]", record.key());
                    byte[] bytes = record.value();
                    bytecount += bytes.length;
                    bytetotal += bytes.length;
                    process(
                        bytes
                        );
                    }
                if (uncommitted > 1000)
                    {
                    log.debug("Committing [{}]", uncommitted);
                    consumer.commitSync();
                    uncommitted = 0 ;
                    }
                long nanotime = (System.nanoTime() - start); 
                log.debug("Loop done [{}] [{}][{}] [{}][{}] in [{}]ns [{}]µs [{}]ms [{}]s",
                    loopcount,
                    recordcount,
                    recordtotal,
                    bytecount,
                    bytetotal,
                    nanotime,
                    (nanotime/1000),
                    (nanotime/1000000),
                    (nanotime/1000000000)
                    );
                log.debug("----");
                }
            while (recordcount > 0);

            if (uncommitted > 0)
                {
                log.debug("Committing [{}]", uncommitted);
                consumer.commitSync();
                uncommitted = 0 ;
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
    
    public long process(final byte[] bytes)
        {
        log.debug("Hydrating ....");
        long count = 0 ;
        DataFileReader<alert> reader = null;
        try {
            reader = new DataFileReader<alert>(
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
            }
        while (reader.hasNext())
            {
            try {
                log.debug("Hydrating alert [{}]", count++);
                final alert frog = reader.next();
        
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
            catch (RuntimeException ouch)
                {
                log.error("RuntimeException hydrating alert [{}]", ouch.getMessage());
                }
            }
        return count ;
        }

    @Override
    public void onPartitionsAssigned(Collection<TopicPartition> partitions)
        {
        log.debug("PartitionsAssigned()");
        for (TopicPartition partition : partitions)
            {
            log.debug("Partition [{}],[{}]", partition.topic(), partition.partition());
            }
        }

    @Override
    public void onPartitionsRevoked(Collection<TopicPartition> partitions)
        {
        log.debug("PartitionsRevoked()");
        for (TopicPartition partition : partitions)
            {
            log.debug("Partition [{}],[{}]", partition.topic(), partition.partition());
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
            log.debug("Bytes [{}]", string.toString());
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
            log.debug("ASCII [{}]", string.toString());
            }
        }
    
    
    public void rewind()
        {
        log.debug("Creating consumer");
        Consumer<Long, byte[]> consumer = consumer(); 

        log.debug("Fetching PartitionInfo for topic");
        List<PartitionInfo> partitions = consumer.partitionsFor(
            topic()
            );

        log.debug("Creating TopicPartitions");
        List<TopicPartition> topicpartitions = new ArrayList<TopicPartition>();
        for(PartitionInfo partition : partitions)
            {
            log.debug("Partition [{}][{}]", partition.topic(), partition.partition());
            topicpartitions.add(
                new TopicPartition(
                    partition.topic(),
                    partition.partition()
                    )
                );
            }
        log.debug("Fetching beginning offsets for partitions");
        Map<TopicPartition,Long> offsets = consumer.beginningOffsets(
            topicpartitions
            );

        log.debug("Creating TopicPartition map");
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
        
        log.debug("Committing ...");
        consumer.commitSync(offsetmeta);
        
        log.debug("Closing ...");
        consumer.close();
        }
    
    
    }
