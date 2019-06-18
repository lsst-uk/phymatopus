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

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import lombok.extern.slf4j.Slf4j;
import uk.ac.roe.wfau.phymatopus.kafka.alert.ZtfAlert;

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
public abstract class KafkaTestBase
    {

    /**
     * The target kafka servers.
     * 
     */
    @Value("${phymatopus.kafka.servers:}")
    protected String servers;

    /**
     * The target kafka topic.
     * 
     */
    @Value("${phymatopus.kafka.topic:}")
    protected String topic;

    /**
     * The target kafka group.
     * 
     */
    @Value("${phymatopus.kafka.group:}")
    protected String group;

    @Value("${phymatopus.kafka.looptimeout:T10M}")
    private String   looptimeoutstr ;
    protected Duration looptimeout()
        {
        return Duration.parse(looptimeoutstr);
        }

    @Value("${phymatopus.kafka.polltimeout:T10S}")
    private String   polltimeoutstr;
    protected Duration polltimeout()
        {
        return Duration.parse(polltimeoutstr);
        }

    /**
     * The number of concurrent threads.
     * 
     */
    @Value("${phymatopus.kafka.threads:4}")
    private Integer threadcount ;

    /**
     * Flag to reset the stream.
     * 
     */
    @Value("${phymatopus.kafka.rewind:true}")
    private Boolean rewind ;

    /**
     * Our Cassandrda connection hostname.
     * 
     */
    @Value("${phymatopus.cassandrda.hostname:}")
    private String hostname ;
    
    /**
     * 
     */
    public KafkaTestBase()
        {
        }

    /**
     * 
     * 
     */
    @Before
    public void before()
        {
        log.debug("Before test ..");
        }

    /**
     * 
     * 
     */
    @After
    public void after()
        {
        log.debug("After test ..");
        }

    
    /**
     * Create our alert processor.
     * 
     */
    public abstract ZtfAlert.Processor processor();
    
    }
