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
    public ZtfAlertWrapper(final ztf.alert bean)
        {
        this.bean = bean ;
        }

    /**
     * An Iterable implementation.
     * 
     */
    public static class IterableWrapper
    implements Iterable<ZtfAlert>
        {
        public IterableWrapper(final Iterable<ztf.alert> inner)
            {
            this.inner = inner ;
            }
        private Iterable<ztf.alert> inner;
        @Override
        public Iterator<ZtfAlert> iterator()
            {
            return new IteratorWrapper(
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
        public IteratorWrapper(final Iterator<ztf.alert> inner)
            {
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
                inner.next()
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
            bean.getCandidate()
            );        
        }
        
    @Override
    public Iterable<ZtfCandidate> getPrvCandidates()
        {
        return new ZtfCandidateWrapper.IterableWrapper(
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
    }
