package uk.ac.roe.wfau.phymatopus.avro.file;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.SeekableByteArrayInput;
import org.apache.avro.reflect.ReflectDatumReader;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;

import lombok.extern.slf4j.Slf4j;
import uk.ac.roe.wfau.phymatopus.alert.AlertProcessor;
import uk.ac.roe.wfau.phymatopus.alert.AlertReader;
import uk.ac.roe.wfau.phymatopus.alert.BaseAlert;
import uk.ac.roe.wfau.phymatopus.avro.ztf.ZtfAlertWrapper;
import ztf.alert;

@Slf4j
//public class TarGzipAlertReader<AlertType> implements AlertReader
public class ZtfTarGzipReader implements AlertReader
    {

    /**
     * Public constructor.
     * 
     */
    public ZtfTarGzipReader(final AlertProcessor<BaseAlert> processor, final String tarname)
        {
        this.processor = processor;
        this.tarname = tarname;
        }

    /**
     * Our Alert processor.
     * 
     */
    final AlertProcessor<BaseAlert> processor ;
    
    /**
     * Our tar file name.
     * 
     */
    final String tarname ;
    
    /*
 * https://stackoverflow.com/a/17079034

TarArchiveInputStream tarInput = 
      new TarArchiveInputStream(new GZipInputStream(new FileInputStream("Your file name")));

 TarArchiveEntry currentEntry = tarInput.getNextTarEntry();
 while(currentEntry != null) {
      File f = currentEntry.getFile();
      // TODO write to file as usual
 }
 *     
 *     
 */
    
    @Override
    public ReaderStatistics loop()
        {
        long  totalalerts = 0 ;
        long  totalstart  = System.nanoTime() ;

        //TarArchiveInputStream tarstream = null ;
        ArchiveInputStream tarstream = null ;
        try {
            tarstream = new ArchiveStreamFactory().createArchiveInputStream(
                new BufferedInputStream(
                    new GZIPInputStream(
                        new FileInputStream(
                            tarname
                            )
                        )
                    )
                );

/*
 * 
            tarstream = new TarArchiveInputStream(
                new GZIPInputStream(
                    new FileInputStream(
                        tarname
                        )
                    )
                );
 *         
 */

            ArchiveEntry entry = tarstream.getNextEntry();
            while(entry != null)
                {
                int size = (int)entry.getSize();
                if (size > 0)
                    {
                    byte bytes[] = new byte[size];
                    tarstream.read(bytes);                
                    DataFileReader<alert> reader = reader(bytes);

                    while (reader.hasNext())
                        {
                        try {
                            //log.trace("Hydrating alert [{}]", alertcount);
                            ztf.alert alert = reader.next();
                            //log.trace("Processing alert [{}]", alertcount);
                            try {
                                processor.process(
                                    new ZtfAlertWrapper(
                                        alert,
                                        tarname
                                        )
                                    );
                                totalalerts++;
                                }
                            catch (Exception ouch)
                                {
                                log.error("Exception processing alert [{}][{}]", ouch.getClass().getName(), ouch.getMessage());
                                log.error("Exception processing alert ", ouch);
                                if (ouch.getCause() != null)
                                    {
                                    Throwable cause = ouch.getCause();
                                    log.error("Exception cause [{}][{}]", cause.getClass().getName(), cause.getMessage());
                                    }
                                throw new RuntimeException(
                                    ouch
                                    );
                                }
                            }
                        catch (Exception ouch)
                            {
                            log.error("Exception hydrating alert [{}][{}]", ouch.getClass().getName(), ouch.getMessage());
                            if (ouch.getCause() != null)
                                {
                                Throwable cause = ouch.getCause();
                                log.error("Exception cause [{}][{}]", cause.getClass().getName(), cause.getMessage());
                                }
                            throw new RuntimeException(
                                ouch
                                );
                            }
                        }
                    }
                entry = tarstream.getNextEntry();
                }

            long  totaltime  = System.nanoTime() - totalstart ;
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
            return new ReaderStatistics.Bean(
                totalalerts,
                totaltime
                ) ;
            }
        catch (ArchiveException ouch)
            {
            log.error("ArchiveException for [{}]", this.tarname);
            throw new RuntimeException(
                ouch
                );
            }
        catch (FileNotFoundException ouch)
            {
            log.error("FileNotFoundException for [{}]", this.tarname);
            throw new RuntimeException(
                ouch
                );
            }
        catch (IOException ouch)
            {
            log.error("IOException reading [{}]", this.tarname);
            throw new RuntimeException(
                ouch
                );
            }
        finally
            {
            try {
                if (null != tarstream)
                    {
                    tarstream.close();
                    }
                }
            catch (IOException ouch)
                {
                log.error("IOException closing [{}]", this.tarname);
                throw new RuntimeException(
                    ouch
                    );
                }
            finally
                {
                tarstream = null ;
                }
            }
        }

    @Override
    public void rewind()
        {
        }

    /**
     * Create a DataFileReader for the alert data type.
     * 
     */
    protected DataFileReader<alert> reader(final byte[] bytes)
        {
        try {
            return new DataFileReader<alert>(
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
            throw new RuntimeException(
                ouch
                );
            }
        }
    }
