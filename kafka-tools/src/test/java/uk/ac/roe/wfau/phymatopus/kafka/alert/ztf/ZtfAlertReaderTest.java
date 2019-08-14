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

package uk.ac.roe.wfau.phymatopus.kafka.alert.ztf;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import lombok.extern.slf4j.Slf4j;
import uk.ac.roe.wfau.phymatopus.kafka.KafkaReaderTestBase;
import uk.ac.roe.wfau.phymatopus.kafka.alert.AlertProcessor;
import uk.ac.roe.wfau.phymatopus.kafka.alert.AlertReader.CallableAlertReader;
import uk.ac.roe.wfau.phymatopus.kafka.alert.BaseAlert;

@Slf4j
@RunWith(
        SpringJUnit4ClassRunner.class
        )
@ContextConfiguration(
    locations = {
        "classpath:component-config.xml"
        }
    )
public class ZtfAlertReaderTest
extends KafkaReaderTestBase
    {
    /**
     *
     */
    public ZtfAlertReaderTest()
        {
        super();
        }

    @Override
    protected AlertProcessor<BaseAlert> processor()
        {
        return new AlertProcessor<BaseAlert>()
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
                log.trace("schemavsn [{}]", alert.getSchemavsn().toString());
                }
            };
        }

    @Override
    protected CallableAlertReader reader()
        {
        return ZtfAlertReader.callable(
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

