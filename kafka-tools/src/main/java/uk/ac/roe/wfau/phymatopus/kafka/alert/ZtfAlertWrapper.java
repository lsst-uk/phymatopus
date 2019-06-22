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

import java.util.Iterator;

import org.apache.avro.Schema;

public class ZtfAlertWrapper implements ZtfAlert
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
    public ZtfAlertWrapper(final ztf.alert bean, final String topic)
        {
        this.bean  = bean ;
        this.topic = topic;
        }
    
    /**
     * An Iterable implementation.
     * 
     */
    public static class IterableWrapper
    implements Iterable<ZtfAlert>
        {
        private String topic;
        public IterableWrapper(final String topic, final Iterable<ztf.alert> inner)
            {
            this.topic = topic;
            this.inner = inner;
            }
        private Iterable<ztf.alert> inner;
        @Override
        public Iterator<ZtfAlert> iterator()
            {
            return new IteratorWrapper(
                this.topic,
                inner.iterator()
                );
            }
        }

    /**
     * An Iterator implementation.
     * 
     */
    public static class IteratorWrapper
    implements Iterator<ZtfAlert>
        {
        private String topic;
        public IteratorWrapper(final String topic, final Iterator<ztf.alert> inner)
            {
            this.topic = topic;
            this.inner = inner ;
            }
        private Iterator<ztf.alert> inner;
        @Override
        public boolean hasNext()
            {
            return inner.hasNext();
            }
        @Override
        public ZtfAlert next()
            {
            return new ZtfAlertWrapper(
                inner.next(),
                this.topic
                );
            }
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
    public ZtfAlertCandidate getCandidate()
        {
        return new ZtfAlertCandidateWrapper(
            this.topic,
            bean.getObjectId(),
            bean.getCandidate()
            );        
        }
        
    @Override
    public Iterable<ZtfCandidate> getPrvCandidates()
        {
        return new ZtfCandidateWrapper.IterableWrapper(
            bean.getObjectId(),
            bean.getPrvCandidates()
            );
        }
        
    @Override
    public ZtfCutout getCutoutScience()
        {
        return new ZtfCutoutWrapper(
            bean.getCutoutScience()
            );
        }
        
    @Override
    public ZtfCutout getCutoutTemplate()
        {
        return new ZtfCutoutWrapper(
            bean.getCutoutTemplate()
            );
        }
        
    @Override
    public ZtfCutout getCutoutDifference()
        {
        return new ZtfCutoutWrapper(
            bean.getCutoutDifference()
            );
        }

    private String topic;
    @Override
    public String getTopic()
        {
        return this.topic;
        }

    @Override
    public Object get(int key)
        {
        return bean.get(key);
        }

    @Override
    public void put(int key, Object value)
        {
        bean.put(key, value);
        }

    @Override
    public Schema getSchema()
        {
        return bean.getSchema();
        }
    }
