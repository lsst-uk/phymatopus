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
package uk.ac.roe.wfau.phymatopus.avro.bean;

import org.apache.avro.Schema;

import uk.ac.roe.wfau.phymatopus.alert.AlertCandidate;
import uk.ac.roe.wfau.phymatopus.alert.AlertCutout;
import uk.ac.roe.wfau.phymatopus.alert.BaseAlert;
import uk.ac.roe.wfau.phymatopus.alert.PrevCandidate;
import uk.ac.roe.wfau.phymatopus.avro.AvroAlert;
import ztf.alert;

/**
 * A wrapper class for the ZTF {@link alert} bean generated from the JSON schema.  
 *
 */
public class AvroBeanAlertWrapper
implements AvroAlert
    {
    /**
     * Our Avro alert bean.
     *
     */
    private ztf.alert bean ;

    /**
     * Public constructor.
     * 
     */    
    public AvroBeanAlertWrapper(final ztf.alert bean, final String topic)
        {
        this.bean  = bean ;
        this.topic = topic;
        }

    @Override
    public Object get(int key)
        {
        return bean.get(key);
        }

    @Override
    public void put(int key, final Object value)
        {
        bean.put(key, value);
        }

    @Override
    public Schema getSchema()
        {
        return bean.getSchema();
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
        return bean.getSchemavsn();
        }

    @Override
    public CharSequence getPublisher()
        {
        return bean.getPublisher();
        }
        
    @Override
    public CharSequence getObjectId()
        {
        return bean.getObjectId();
        }
        
    @Override
    public Long getCandid()
        {
        return bean.getCandid();
        }
        
    @Override
    public AlertCandidate getCandidate()
        {
        return new AvroBeanAlertCandidateWrapper(
            this.topic,
            bean.getObjectId(),
            bean.getCandidate()
            );        
        }
        
    @Override
    public Iterable<PrevCandidate> getPrvCandidates()
        {
        return new AvroBeanPrevCandidateWrapper.IterableWrapper(
            bean.getPrvCandidates(),
            bean.getObjectId()
            );
        }
        
    @Override
    public AlertCutout getCutoutScience()
        {
        return new AvroBeanCutoutWrapper(
            bean.getCutoutScience()
            );
        }
        
    @Override
    public AlertCutout getCutoutTemplate()
        {
        return new AvroBeanCutoutWrapper(
            bean.getCutoutTemplate()
            );
        }
        
    @Override
    public AlertCutout getCutoutDifference()
        {
        return new AvroBeanCutoutWrapper(
            bean.getCutoutDifference()
            );
        }
    }
