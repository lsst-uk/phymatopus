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

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.mortbay.log.Log;

import ztf.alert;

public class LsstAlertWrapper implements ZtfAlert
    {
    private GenericData.Record record;
    public LsstAlertWrapper(final GenericData.Record record, final String topic)
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
    public ZtfAlertCandidate getCandidate()
        {
        return new LsstAlertCandidateWrapper(
            (GenericData.Record) record.get(4),
            this.getObjectId(),
            this.getTopic()
            );
        }
    
    @SuppressWarnings("unchecked")
    @Override
    public Iterable<ZtfCandidate> getPrvCandidates()
        {
        return new LsstCandidateWrapper.IterableWrapper(
            (GenericData.Array<GenericData.Record>) record.get(5),
            this.getObjectId()
            );
        }
    
    @Override
    public ZtfCutout getCutoutScience()
        {
        Log.debug("getCutoutScience() [{}]", record.get(6).getClass().getName());
        //return (ZtfCutout) record.get(6);
        return null;
        }
    
    @Override
    public ZtfCutout getCutoutTemplate()
        {
        //return (ZtfCutout) record.get(7);
        return null;
        }
    
    @Override
    public ZtfCutout getCutoutDifference()
        {
        //return (ZtfCutout) record.get(8);
        return null;
        }
    }