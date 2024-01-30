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
package uk.ac.roe.wfau.phymatopus.kafka;

import uk.ac.roe.wfau.phymatopus.alert.AlertProcessor;
import uk.ac.roe.wfau.phymatopus.alert.AlertReader;
import uk.ac.roe.wfau.phymatopus.alert.BaseAlert;
import uk.ac.roe.wfau.phymatopus.alert.AlertReader.CallableAlertReader;
import uk.ac.roe.wfau.phymatopus.kafka.KafkaReaderBase.Configuration;

/**
 * Public interface for a reader that reads alerts from a Kafka stream and passes them to an {@link AlertProcessor}.
 *
 */
public interface KafkaAlertReader
extends AlertReader
    {
    
    /**
     *  Factory interface.
     *
     */
    public static interface Factory
    extends AlertReader.Factory
        {
        /**
         * Create a new {@link AlertReader}
         * 
         */
        public KafkaAlertReader reader(AlertProcessor<BaseAlert> processor, final Configuration config);
        
        /**
         * Create a new {@link CallableAlertReader}.
         * 
         */
        public CallableAlertReader callable(AlertProcessor<BaseAlert> processor, final Configuration config);
        }
    }
