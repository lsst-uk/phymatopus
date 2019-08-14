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

package uk.ac.roe.wfau.phymatopus.kafka.alert;

import java.util.concurrent.Callable;

public interface AlertReader
    {
    /**
     * Read alerts until the loop timeout is exceeded. 
     * 
     */
    public ReaderStatistics loop();
    
    /**
     * Rewind to the start of the topic.
     * 
     */
    public void rewind();
    
    /**
     * Interface for the loop statistics.
     * 
     */
    public static interface ReaderStatistics
        {
        /**
         * The number of events that have been processed.
         * 
         */
        public long count();

        /**
         * The time taken (excluding wait) to process the events.
         * 
         */
        public long time();

        /**
         * Statistics bean implementation.
         * 
         */
        public static class Bean
        implements ReaderStatistics
            {
            public Bean(final long count, final long time)
                {
                this.alerts = count;
                this.time   = time;
                }
            private final long alerts;
            @Override
            public long count()
                {
                return this.alerts;
                }
            private final long time ;
            @Override
            public long time()
                {
                return this.time;
                }
            }
        }    

    public static interface CallableAlertReader
    extends Callable<AlertReader.ReaderStatistics>
        {
        /**
         * Read alerts until the loop timeout is exceeded.
         *  
         */
        public AlertReader.ReaderStatistics call();

        /**
         * Rewind to the start of the topic.
         * 
         */
        public void rewind();

        }    
    }
