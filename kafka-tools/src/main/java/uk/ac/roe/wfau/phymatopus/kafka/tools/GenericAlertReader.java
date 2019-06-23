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

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;

import lombok.extern.slf4j.Slf4j;
import uk.ac.roe.wfau.phymatopus.kafka.alert.AlertProcessor;

/**
 * Reads a series on alerts from a stream and stops when there are no more alerts and the poll timeout is reached.
 *
 */
@Slf4j
public abstract class GenericAlertReader<KeyType, DataType, AlertType>
extends BaseReader
implements ConsumerRebalanceListener, AlertReader
    {

    /**
     * Public constructor.
     * @param processor The alert processor.
     * @param config The reader configuration.
     *
     */
    public GenericAlertReader(final AlertProcessor<AlertType> processor, final Configuration config)
        {
        super(config);
        this.processor = processor ;
        }

    /**
     * Our alert processor.
     * 
     */
    protected AlertProcessor<AlertType> processor;

    /**
     * Create our {@link Consumer}.
     *
     */
    protected abstract Consumer<KeyType, DataType> consumer();

    /**
     * Process a DataType instance.
     *
     */
    protected abstract long process(final DataType data);

    public ReaderStatistics loop()
        {
        log.trace("Creating consumer");
        Consumer<KeyType, DataType> consumer = consumer();

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
        long lastwait    = 0;

        long looptimeout = config.getLoopTimeout().toNanos();
        Duration polltimeout = config.getLoopTimeout();

        do {
            loopcount++;
            loopalerts = 0 ;
            loopstart  = System.nanoTime() ; 

            log.trace("Loop [{}][{}]", loopcount, lastwait);

            ConsumerRecords<KeyType, DataType> records = consumer.poll(
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
                for (ConsumerRecord<KeyType, DataType> record : records)
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
        
        return new ReaderStatistics.Bean(
            totalalerts,
            totaltime
            ) ;
        }

    /**
     * Rewind our group to the start of the topic.
     * 
     */
    public void rewind()
        {
        log.trace("Creating consumer");
        Consumer<KeyType, DataType> consumer = consumer();

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
    }
