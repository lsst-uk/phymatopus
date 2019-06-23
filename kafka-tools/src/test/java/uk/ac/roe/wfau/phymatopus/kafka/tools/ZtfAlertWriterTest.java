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

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import lombok.extern.slf4j.Slf4j;
import uk.ac.roe.wfau.phymatopus.kafka.alert.ZtfAlert;
import uk.ac.roe.wfau.phymatopus.kafka.alert.ZtfAlertWrapper;
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
public class ZtfAlertWriterTest
extends KafkaTestBase
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

    protected ZtfAlertWriter writer ;

    protected ZtfAlertWriter.Configuration config ;
    
    /**
     *
     */
    public ZtfAlertWriterTest()
        {
        super();
        }
    
    @Before
    @Override
    public void before()
        {
        log.debug("Creating config");
        config = new ZtfAlertWriter.ConfigurationBean(
            servers,
            topic,
            group
            );
        log.debug("Creating writer");
        writer = new ZtfAlertWriter(
            config
            );
        log.debug("Initialising writer");
        writer.init();
        }

    /**
     * Test a simple write.
     *
     */
    @Test
    public void testThreads()
    throws Exception
        {
        for (int candid = 0 ; candid < 1000 ; candid++)
            {
            ZtfAlert alert = new ZtfAlertWrapper(
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

