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

import java.nio.ByteBuffer;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import lombok.extern.slf4j.Slf4j;
import uk.ac.roe.wfau.phymatopus.alert.BaseAlert;
import uk.ac.roe.wfau.phymatopus.avro.bean.AvroBeanAlertWrapper;
import uk.ac.roe.wfau.phymatopus.kafka.KafkaObjectWriter;
import ztf.alert;
import ztf.candidate;
import ztf.cutout;
import ztf.prv_candidate;

/**
 *
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
public class KafkaObjectWriterTest
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
    public KafkaObjectWriterTest()
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

    @After
    public void after()
        {
        log.debug("Closing writer");
        writer.close();
        }
    
    /**
     * Test a simple write.
     *
     */
    @Test
    public void testSimpleWrite()
    throws Exception
        {
        log.debug("Starting test ....");
        for (int candid = 0 ; candid < 1000 ; candid++)
            {
            log.debug("Sending alert [{}]", candid);
            BaseAlert alert = new AvroBeanAlertWrapper(
                new alert (
                    "schemavsn",
                    "publisher",
                    "objectId",
                    new Long(candid),
                    new candidate(
                        new Double(100),
                        new Integer(1),
                        new Long(-1),
                        new Float(0.5),
                        "pdiffimfilename",
                        "programpi",
                        new Integer(0),
                        new Long(candid),
                        "isdiffpos",
                        new Long(1),
                        new Integer(1),
                        new Integer(1),
                        new Integer(1),
                        new Float(0.5),
                        new Float(0.5),
                        new Double(0.5),
                        new Double(0.5),
                        new Float(0.5),
                        new Float(0.5),
                        new Float(0.5),
                        new Float(0.5),
                        new Float(0.5),
                        new Float(0.5),
                        new Float(0.5),
                        new Float(0.5),
                        new Float(0.5),
                        new Float(0.5),
                        new Float(0.5),
                        new Float(0.5),
                        new Float(0.5),
                        new Float(0.5),
                        new Float(0.5),
                        new Float(0.5),
                        new Float(0.5),
                        new Float(0.5),
                        new Float(0.5),
                        new Float(0.5),
                        new Float(0.5),
                        new Float(0.5),
                        new Integer(1),
                        new Integer(1),
                        new Float(0.5),
                        new Float(0.5),
                        new Float(0.5),
                        "ssnamenr",
                        new Float(0.5),
                        new Float(0.5),
                        new Float(0.5),
                        new Double(0.5),
                        new Double(0.5),
                        new Float(0.5),
                        new Float(0.5),
                        new Float(0.5),
                        new Float(0.5),
                        new Float(0.5),
                        new Float(0.5),
                        new Integer(1),
                        new Integer(1),
                        new Double(0.5),
                        new Double(0.5),
                        new Double(0.5),
                        new Integer(1),
                        new Long(1),
                        new Long(1),
                        new Float(0.5),
                        new Float(0.5),
                        new Float(0.5),
                        new Float(0.5),
                        new Float(0.5),
                        new Float(0.5),
                        new Long(1),
                        new Float(0.5),
                        new Float(0.5),
                        new Float(0.5),
                        new Float(0.5),
                        new Float(0.5),
                        new Float(0.5),
                        new Integer(1),
                        new Long(1),
                        new Double(0.5),
                        new Double(0.5),
                        new Integer(1),
                        "rbversion",
                        new Float(0.5),
                        new Float(0.5),
                        new Float(0.5),
                        new Float(0.5),
                        new Float(0.5),
                        new Float(0.5),
                        new Integer(1),
                        new Float(0.5),
                        new Float(0.5),
                        new Float(0.5),
                        new Float(0.5),
                        new Float(0.5),
                        new Float(0.5),
                        new Float(0.5),
                        new Float(0.5),
                        new Float(0.5),
                        new Float(0.5)
                        ),
                    new ArrayList<prv_candidate>(),
                    new cutout("science", ByteBuffer.allocate(1024)),
                    new cutout("template", ByteBuffer.allocate(1024)),
                    new cutout("difference", ByteBuffer.allocate(1024))
                    ),
                topic
                );
            writer.write(
                alert
                );
            }
        }
    }

