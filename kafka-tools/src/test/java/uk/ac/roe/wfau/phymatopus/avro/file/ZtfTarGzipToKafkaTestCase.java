/**
 * 
 */
package uk.ac.roe.wfau.phymatopus.avro.file;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import lombok.extern.slf4j.Slf4j;
import uk.ac.roe.wfau.phymatopus.alert.AlertProcessor;
import uk.ac.roe.wfau.phymatopus.alert.AlertReader.ReaderStatistics;
import uk.ac.roe.wfau.phymatopus.kafka.alert.lsst.LsstAlertWriter;
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
public class ZtfTarGzipToKafkaTestCase
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

    /**
     * The Avro file to load.
     * 
     */
    @Value("${phymatopus.kafka.loader.avrofile:}")
    protected String avrofile;
    
    /**
    *
    */
   public ZtfTarGzipToKafkaTestCase()
       {
       }

   protected LsstAlertWriter writer ;

   protected LsstAlertWriter.Configuration config ;

   @Before
   public void before()
       {
       log.debug("Creating config");
       config = new LsstAlertWriter.ConfigurationBean(
           servers,
           topic,
           group
           );
       log.debug("Creating writer");
       writer = new LsstAlertWriter(
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
               writer.write(alert);
               count++;
               log.trace("candId    [{}]", alert.getCandid());
               log.trace("objectId  [{}]", alert.getObjectId());
               log.trace("schemavsn [{}]", alert.getSchemavsn().toString());
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

       ReaderStatistics stats = reader.loop();
       
       log.debug("Read [{}] in [{}]", stats.count(), stats.time());

       }
    }
