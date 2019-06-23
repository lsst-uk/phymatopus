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

import org.apache.avro.generic.GenericData;

public class LsstCandidateWrapper implements ZtfCandidate
    {
    private GenericData.Record record;
    public LsstCandidateWrapper(final GenericData.Record record, final CharSequence objectid)
        {
        this.record = record;
        }
    
    private CharSequence objectid;
    @Override
    public CharSequence getObjectId()
        {
        return this.objectid;
        }

    @Override
    public Double getJd()
        {
        return (Double) record.get(0);
        }

    @Override
    public Integer getFid()
        {
        return (Integer) record.get(1);
        }

    @Override
    public Long getPid()
        {
        return (Long) record.get(2);
        }

    @Override
    public Float getDiffmaglim()
        {
        return (Float) record.get(3);
        }

    @Override
    public CharSequence getPdiffimfilename()
        {
        return (CharSequence) record.get(4);
        }

    @Override
    public CharSequence getProgrampi()
        {
        return (CharSequence) record.get(5);
        }

    @Override
    public Integer getProgramid()
        {
        return (Integer) record.get(6);
        }

    @Override
    public Long getCandid()
        {
        return (Long) record.get(7);
        }
    
    @Override
    public CharSequence getIsdiffpos()
        {
        return (CharSequence) record.get(8);
        }

    @Override
    public Long getTblid()
        {
        return (Long) record.get(9);
        }

    @Override
    public Integer getNid()
        {
        return (Integer) record.get(10);
        }

    @Override
    public Integer getRcid()
        {
        return (Integer) record.get(11);
        }

    @Override
    public Integer getField()
        {
        return (Integer) record.get(12);
        }

    @Override
    public Float getXpos()
        {
        return (Float) record.get(13);
        }

    @Override
    public Float getYpos()
        {
        return (Float) record.get(14);
        }

    @Override
    public Double getRa()
        {
        return (Double) record.get(15);
        }

    @Override
    public Double getDec()
        {
        return (Double) record.get(16);
        }

    @Override
    public Float getMagpsf()
        {
        return (Float) record.get(17);
        }

    @Override
    public Float getSigmapsf()
        {
        return (Float) record.get(18);
        }

    @Override
    public Float getChipsf()
        {
        return (Float) record.get(19);
        }

    @Override
    public Float getMagap()
        {
        return (Float) record.get(20);
        }

    @Override
    public Float getSigmagap()
        {
        return (Float) record.get(21);
        }

    @Override
    public Float getDistnr()
        {
        return (Float) record.get(22);
        }

    @Override
    public Float getMagnr()
        {
        return (Float) record.get(23);
        }

    @Override
    public Float getSigmagnr()
        {
        return (Float) record.get(24);
        }

    @Override
    public Float getChinr()
        {
        return (Float) record.get(25);
        }

    @Override
    public Float getSharpnr()
        {
        return (Float) record.get(26);
        }

    @Override
    public Float getSky()
        {
        return (Float) record.get(27);
        }

    @Override
    public Float getMagdiff()
        {
        return (Float) record.get(28);
        }

    @Override
    public Float getFwhm()
        {
        return (Float) record.get(29);
        }

    @Override
    public Float getClasstar()
        {
        return (Float) record.get(30);
        }

    @Override
    public Float getMindtoedge()
        {
        return (Float) record.get(31);
        }

    @Override
    public Float getMagfromlim()
        {
        return (Float) record.get(32);
        }

    @Override
    public Float getSeeratio()
        {
        return (Float) record.get(33);
        }

    @Override
    public Float getAimage()
        {
        return (Float) record.get(34);
        }

    @Override
    public Float getBimage()
        {
        return (Float) record.get(35);
        }

    @Override
    public Float getAimagerat()
        {
        return (Float) record.get(36);
        }

    @Override
    public Float getBimagerat()
        {
        return (Float) record.get(37);
        }

    @Override
    public Float getElong()
        {
        return (Float) record.get(38);
        }

    @Override
    public Integer getNneg()
        {
        return (Integer) record.get(39);
        }

    @Override
    public Integer getNbad()
        {
        return (Integer) record.get(40);
        }

    @Override
    public Float getRb()
        {
        return (Float) record.get(41);
        }

    @Override
    public Float getSsdistnr()
        {
        return (Float) record.get(42);
        }

    @Override
    public Float getSsmagnr()
        {
        return (Float) record.get(43);
        }

    @Override
    public CharSequence getSsnamenr()
        {
        return (CharSequence) record.get(44);
        }

    @Override
    public Float getSumrat()
        {
        return (Float) record.get(45);
        }

    @Override
    public Float getMagapbig()
        {
        return (Float) record.get(46);
        }

    @Override
    public Float getSigmagapbig()
        {
        return (Float) record.get(47);
        }

    @Override
    public Double getRanr()
        {
        return (Double) record.get(48);
        }

    @Override
    public Double getDecnr()
        {
        return (Double) record.get(49);
        }

    // Different to AlertCandidate
    @Override
    public Double getScorr()
        {
        return (Double) record.get(50);
        }

    // Different to AlertCandidate
    @Override
    public CharSequence getRbversion()
        {
        return (CharSequence) record.get(51);
        }

    /**
     * An Iterable implementation.
     * 
     */
    public static class IterableWrapper
    implements Iterable<ZtfCandidate>
        {
        private CharSequence objectid ;
        public IterableWrapper(final Iterable<GenericData.Record> inner, final CharSequence objectid)
            {
            this.inner = inner ;
            this.objectid = objectid;
            }
        private Iterable<GenericData.Record> inner;
        @Override
        public Iterator<ZtfCandidate> iterator()
            {
            return new IteratorWrapper(
                ((this.inner != null) ? this.inner.iterator() : null),
                this.objectid
                );
            }
        }

    /**
     * An Iterator implementation.
     * 
     */
    public static class IteratorWrapper
    implements Iterator<ZtfCandidate>
        {
        private CharSequence objectid ;
        public IteratorWrapper(final Iterator<GenericData.Record> inner, final CharSequence objectid)
            {
            this.objectid = objectid;
            this.inner = inner ;
            }
        private Iterator<GenericData.Record> inner;
        @Override
        public boolean hasNext()
            {
            if (null != inner)
                {
                return inner.hasNext();
                }
            else {
                return false ;
                }
            }
        @Override
        public ZtfCandidate next()
            {
            if (null != inner)
                {
                return new LsstCandidateWrapper(
                    this.inner.next(),
                    this.objectid
                    );
                }
            else {
                throw new RuntimeException(
                    "Call to next() on an empty (null) Iterator."
                    );
                }
            }
        }
    }