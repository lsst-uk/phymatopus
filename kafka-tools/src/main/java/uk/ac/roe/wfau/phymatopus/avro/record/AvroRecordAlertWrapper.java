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
package uk.ac.roe.wfau.phymatopus.avro.record;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.mortbay.log.Log;

import uk.ac.roe.wfau.phymatopus.alert.AlertCandidate;
import uk.ac.roe.wfau.phymatopus.alert.AlertCutout;
import uk.ac.roe.wfau.phymatopus.alert.PrevCandidate;
import uk.ac.roe.wfau.phymatopus.avro.AvroAlert;
import ztf.alert;

/**
 * A wrapper class for Avro {@link GenericData.Record}s based on the ZTF {@link alert} schema.  
 *
 */
public class AvroRecordAlertWrapper
implements AvroAlert
    {
    private GenericData.Record record;
    public AvroRecordAlertWrapper(final GenericData.Record record, final String topic)
        {
        this.topic  = topic ;
        this.record = record;
        }

    @Override
    public Object get(int key)
        {
        return record.get(key);
        }

    @Override
    public void put(int key, final Object value)
        {
        record.put(key, value);        
        }

    @Override
    public Schema getSchema()
        {
        return alert.SCHEMA$;
        }
    
    private String topic;
    @Override
    public String getTopic()
        {
        return this.topic;
        }
    
    @Override
    public CharSequence getSchemavsn()
        {
        return (CharSequence) record.get(0);
        }
    
    @Override
    public CharSequence getPublisher()
        {
        return (CharSequence) record.get(1);
        }
    
    @Override
    public CharSequence getObjectId()
        {
        return (CharSequence) record.get(2);
        }
    
    @Override
    public Long getCandid()
        {
        return (Long) record.get(3);
        }
    
    @Override
    public AlertCandidate getCandidate()
        {
        return new AvroRecordAlertCandidateWrapper(
            (GenericData.Record) record.get(4),
            this.getObjectId(),
            this.getTopic()
            );
        }
    
    @SuppressWarnings("unchecked")
    @Override
    public Iterable<PrevCandidate> getPrvCandidates()
        {
        return new AvroRecordPrevCandidateWrapper.IterableWrapper(
            (GenericData.Array<GenericData.Record>) record.get(5),
            this.getObjectId()
            );
        }
    
    @Override
    public AlertCutout getCutoutScience()
        {
        Log.debug("getCutoutScience() [{}]", record.get(6).getClass().getName());
        //return (ZtfCutout) record.get(6);
        return null;
        }
    
    @Override
    public AlertCutout getCutoutTemplate()
        {
        Log.debug("getCutoutTemplate() [{}]", record.get(7).getClass().getName());
        //return (ZtfCutout) record.get(7);
        return null;
        }
    
    @Override
    public AlertCutout getCutoutDifference()
        {
        Log.debug("getCutoutDifference() [{}]", record.get(8).getClass().getName());
        //return (ZtfCutout) record.get(8);
        return null;
        }
    }