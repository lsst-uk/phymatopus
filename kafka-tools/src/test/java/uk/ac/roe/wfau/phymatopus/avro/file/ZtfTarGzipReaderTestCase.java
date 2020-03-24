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
package uk.ac.roe.wfau.phymatopus.avro.file;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import lombok.extern.slf4j.Slf4j;
import uk.ac.roe.wfau.phymatopus.alert.AlertProcessor;
import uk.ac.roe.wfau.phymatopus.alert.AlertReader.LoopStats;
import uk.ac.roe.wfau.phymatopus.alert.BaseAlert;

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
public class ZtfTarGzipReaderTestCase
    {

    /**
     * The Avro file to load.
     * 
     */
    @Value("${phymatopus.kafka.loader.avrofile:}")
    protected String avrofile;

    /**
    *
    */
   public ZtfTarGzipReaderTestCase()
       {
       }

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
               //log.trace("candId    [{}]", alert.getCandid());
               //log.trace("objectId  [{}]", alert.getObjectId());
               }
           };
       }

   @Test
   public void testSomething()
       {
       ZtfTarGzipReader reader = new ZtfTarGzipReader(
           this.processor(),
           avrofile
           );
       LoopStats stats = reader.loop();
       log.debug("Read [{}] in [{}]", stats.count(), stats.time());
       }
    }
