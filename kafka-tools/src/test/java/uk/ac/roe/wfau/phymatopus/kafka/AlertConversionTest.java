/*
 *  Copyright (C) 2020 Royal Observatory, University of Edinburgh, UK
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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import lombok.extern.slf4j.Slf4j;
import uk.ac.roe.wfau.phymatopus.alert.AlertProcessor;
import uk.ac.roe.wfau.phymatopus.alert.AlertReader;
import uk.ac.roe.wfau.phymatopus.alert.BaseAlert;
import uk.ac.roe.wfau.phymatopus.kafka.KafkaInlineReader;
import uk.ac.roe.wfau.phymatopus.kafka.KafkaObjectWriter;

/**
 * TODO Use this to create an AlertTransformer base class that reads alerts from a reader, does something to them, and writes them to a writer.
 *
 */
@Slf4j
@RunWith(
        SpringJUnit4ClassRunner.class
        )
@ContextConfiguration(
    locations = {
        "classpath:component-config.xml"
        }
    )
public class AlertConversionTest
extends KafkaReaderTestBase
    {
    /**
     * The target kafka servers.
     * 
     */
    @Value("${phymatopus.kafka.writer.servers:}")
    protected String servers;

    /**
     * The target kafka topic.
     * 
     */
    @Value("${phymatopus.kafka.writer.topic:}")
    protected String topic;

    /**
     * The target kafka group.
     * 
     */
    @Value("${phymatopus.kafka.writer.group:}")
    protected String group;

    protected KafkaObjectWriter writer ;

    protected KafkaObjectWriter.Configuration config ;
    
    /**
     *
     */
    public AlertConversionTest()
        {
        super();
        }
    
    @Before
    public void before()
        {
        log.debug("Creating config");
        config = new KafkaObjectWriter.ConfigurationBean(
            servers,
            topic,
            group
            );
        log.debug("Creating writer");
        writer = new KafkaObjectWriter(
            config
            );
        log.debug("Initialising writer");
        writer.init();
        }
    
    @Override
    protected AlertProcessor<BaseAlert> processor()
        {
        return new AlertProcessor<BaseAlert> ()
            {
            private long count ;
            @Override
            public long count()
                {
                return this.count;
                }
            @Override
            public void process(final BaseAlert alert)
                {
                count++;
                log.trace("candId    [{}]", alert.getCandid());
                log.trace("objectId  [{}]", alert.getObjectId());
                log.trace("class     [{}]", alert.getClass().getName());
                writer.write(
                    alert
                    );
                }
            };
        }

    @Override
    protected AlertReader.CallableAlertReader reader()
        {
        return KafkaInlineReader.callable(
            this.processor(),
            this.configuration()
            );
        }

    @Test
    @Override
    public void testThreads()
    throws Exception
        {
        super.testThreads();
        }
    }

