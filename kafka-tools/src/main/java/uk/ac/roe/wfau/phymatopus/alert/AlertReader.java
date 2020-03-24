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

package uk.ac.roe.wfau.phymatopus.alert;

import java.util.concurrent.Callable;

/**
 * TODO Rename this to <something>Reader.
 * This interface doesn't actually refer to a type of alert ...
 * Possibly LoopProcessor .. ? 
 *
 */
public interface AlertReader
    {
    /**
     * Read alerts until we reach the end of file (File) or we exceed a wait timeout (Kafka). 
     * 
     */
    public LoopStats loop();
    
    /**
     * Rewind to the start of the file (File) or stream (Kafka).
     * 
     */
    public void rewind();
    
    /**
     * Interface for the loop statistics.
     * @deprecated Need to figure out a better way of handling this.
     * 
     */
    public static interface LoopStats
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
        implements LoopStats
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

    /**
     * Public interface for a {@link Callable} reader.
     *
     */
    public static interface CallableAlertReader
    extends Callable<AlertReader.LoopStats>
        {
        /**
         * Read alerts until we reach the end of file (File) or we exceed a wait timeout (Kafka). 
         *  
         */
        public AlertReader.LoopStats call();

        /**
         * Rewind to the start of the file (File) or stream (Kafka).
         * 
         */
        public void rewind();

        }
    
    /**
     * Public factory interface.
     * 
     */
    public static interface Factory
        {
        }
    }
