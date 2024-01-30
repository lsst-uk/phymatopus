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

package uk.ac.roe.wfau.phymatopus.cassandra;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import lombok.extern.slf4j.Slf4j;
import uk.ac.roe.wfau.phymatopus.alert.AlertReader;
import uk.ac.roe.wfau.phymatopus.cassandra.AbstractCassandraWriter;
import uk.ac.roe.wfau.phymatopus.cassandra.CandiateWriter;
import uk.ac.roe.wfau.phymatopus.kafka.KafkaObjectReader;

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
public class LsstCandiateWriterTest
extends CassandraWriterTestBase
    {
    /**
     * Public constructor.
     * 
     */
    public LsstCandiateWriterTest()
        {
        super();
        }

    @Override
    protected AlertReader.CallableAlertReader reader()
        {
        return KafkaObjectReader.callable(
            this.processor(),
            this.configuration()
            );
        }

    @Override
    public AbstractCassandraWriter writer()
        {
        return new CandiateWriter(
            this.hostname(),
            this.dcname()
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

