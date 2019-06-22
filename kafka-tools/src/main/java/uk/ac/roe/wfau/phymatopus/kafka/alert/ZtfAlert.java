/*
 *  Copyright (C) 2019 Royal Observatory, University of Edinburgh, UK
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

import org.apache.avro.specific.SpecificRecord;

public interface ZtfAlert
extends SpecificRecord
    {
   
    /**
     * Alert processor interface.
     * 
     */
    public static interface Processor
        {
        /**
         * Process an Alert.
         * 
         */
        public void process(final ZtfAlert alert);

        /**
         * The number of alerts this processor has handled. 
         * 
         */
        public long count();

        }

    /**
     * Gets the value of the 'schemavsn' field.
     * @return schema version used
     */
    public CharSequence getSchemavsn();

    /**
     * Gets the value of the 'publisher' field.
     * @return origin of alert packet
     */
    public CharSequence getPublisher();

    /**
     * Gets the value of the 'objectId' field.
     * @return object identifier or name
     */
    public CharSequence getObjectId();

    /**
     * Gets the value of the 'candid' field.
     * @return The value of the 'candid' field.
     */
    public Long getCandid();

    /**
     * Gets the value of the 'candidate' field.
     * @return The value of the 'candidate' field.
     */
    public ZtfAlertCandidate getCandidate();

    /**
     * Gets the value of the 'prv_candidates' field.
     * @return The value of the 'prv_candidates' field.
     */
    public Iterable<ZtfCandidate> getPrvCandidates();

    /**
     * Gets the value of the 'cutoutScience' field.
     * @return The value of the 'cutoutScience' field.
     */
    public ZtfCutout getCutoutScience();

    /**
     * Gets the value of the 'cutoutTemplate' field.
     * @return The value of the 'cutoutTemplate' field.
     */
    public ZtfCutout getCutoutTemplate();

    /**
     * Gets the value of the 'cutoutDifference' field.
     * @return The value of the 'cutoutDifference' field.
     */
    public ZtfCutout getCutoutDifference();

    /**
     * The Kafka topic this alert was read from.
     *  
     */
    public String getTopic();
    
    }
