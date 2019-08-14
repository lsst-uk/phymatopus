/**
 * 
 */
package uk.ac.roe.wfau.phymatopus.kafka.tools;

import java.util.Formatter;

import lombok.extern.slf4j.Slf4j;

/**
 *
 */
@Slf4j
public class DebugFormatter
    {
    public DebugFormatter()
        {
        }
    
    public void hexBytes(byte[] bytes)
        {
        for (int i = 0 ; i < 0x10 ; i++)
            {
            StringBuffer buffer = new StringBuffer();
            Formatter formatter = new Formatter(buffer);
            for (int j = 0 ; j < 0x10 ; j++)
                {
                int k = (i * 0x10) + j;
                formatter.format(
                    "%s%02X",
                    ((j > 0) ? " " : ""),
                    bytes[k]
                    );
                }
            formatter.close();
            log.trace("Bytes [{}]", buffer.toString());
            }
        }

    public void asciiBytes(byte[] bytes)
        {
        for (int i = 0 ; i < 0x10 ; i++)
            {
            StringBuffer string = new StringBuffer();
            Formatter formatter = new Formatter(string);
            for (int j = 0 ; j < 0x10 ; j++)
                {
                int k = (i * 0x10) + j;
                char ascii ;
                if ((k < bytes.length) && (bytes[k] >= ' ') && (bytes[k] <= '~'))
                    {
                    ascii = (char) bytes[k];
                    }
                else {
                    ascii = '.';
                    }
                formatter.format(
                    "%s%c",
                    ((j > 0) ? " " : ""),
                    ascii
                    );
                }
            formatter.close();
            log.trace("ASCII [{}]", string.toString());
            }
        }
    }
