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

import org.apache.commons.collections.iterators.EmptyIterator;

public class ZtfCandidateWrapper implements ZtfCandidate
    {
    /**
     * Our Avro alert bean.
     *
     */
    private ztf.prv_candidate bean ;

    /**
     * The corresponding objectId.
     * 
     */
    private CharSequence objectid ;

    @Override
    public CharSequence getObjectId()
        {
        return this.objectid;
        }
    
    /**
     * Public constructor.
     * 
     */    
    public ZtfCandidateWrapper(final CharSequence objectid, final ztf.prv_candidate bean)
        {
        this.bean = bean ;
        this.objectid = objectid ;
        }

    /**
     * An Iterable implementation.
     * 
     */
    public static class IterableWrapper
    implements Iterable<ZtfCandidate>
        {
        private CharSequence objectid ;
        public IterableWrapper(final CharSequence objectid, final Iterable<ztf.prv_candidate> inner)
            {
            this.objectid = objectid;
            this.inner = inner ;
            }
        private Iterable<ztf.prv_candidate> inner;
        @Override
        public Iterator<ZtfCandidate> iterator()
            {
            return new IteratorWrapper(
                this.objectid,
                this.inner.iterator()
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
        public IteratorWrapper(final CharSequence objectid, final Iterator<ztf.prv_candidate> inner)
            {
            this.objectid = objectid;
            this.inner = inner ;
            }
        private Iterator<ztf.prv_candidate> inner;
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
                return new ZtfCandidateWrapper(
                    this.objectid,
                    this.inner.next()
                    );
                }
            else {
                throw new RuntimeException(
                    "Call to next() on an empty (null) Iterator."
                    );
                }
            }
        }
    
    @Override
    public Double getJd()
        {
        return bean.getJd();
        }

    @Override
    public Integer getFid()
        {
        return bean.getFid();
        }

    @Override
    public Long getPid()
        {
        return bean.getPid();
        }

    @Override
    public Float getDiffmaglim()
        {
        return bean.getDiffmaglim();
        }

    @Override
    public CharSequence getPdiffimfilename()
        {
        return bean.getPdiffimfilename();
        }

    @Override
    public CharSequence getProgrampi()
        {
        return bean.getProgrampi();
        }

    @Override
    public Integer getProgramid()
        {
        return bean.getProgramid();
        }

    @Override
    public Long getCandid()
        {
        return bean.getCandid();
        }

    @Override
    public CharSequence getIsdiffpos()
        {
        return bean.getIsdiffpos();
        }

    @Override
    public Long getTblid()
        {
        return bean.getTblid();
        }

    @Override
    public Integer getNid()
        {
        return bean.getNid();
        }

    @Override
    public Integer getRcid()
        {
        return bean.getRcid();
        }

    @Override
    public Integer getField()
        {
        return bean.getField();
        }

    @Override
    public Float getXpos()
        {
        return bean.getXpos();
        }

    @Override
    public Float getYpos()
        {
        return bean.getYpos();
        }

    @Override
    public Double getRa()
        {
        return bean.getRa();
        }

    @Override
    public Double getDec()
        {
        return bean.getDec();
        }

    @Override
    public Float getMagpsf()
        {
        return bean.getMagpsf();
        }

    @Override
    public Float getSigmapsf()
        {
        return bean.getSigmapsf();
        }

    @Override
    public Float getChipsf()
        {
        return bean.getChipsf();
        }

    @Override
    public Float getMagap()
        {
        return bean.getMagap();
        }

    @Override
    public Float getSigmagap()
        {
        return bean.getSigmagap();
        }

    @Override
    public Float getDistnr()
        {
        return bean.getDistnr();
        }

    @Override
    public Float getMagnr()
        {
        return bean.getMagnr();
        }

    @Override
    public Float getSigmagnr()
        {
        return bean.getSigmagnr();
        }

    @Override
    public Float getChinr()
        {
        return bean.getChinr();
        }

    @Override
    public Float getSharpnr()
        {
        return bean.getSharpnr();
        }

    @Override
    public Float getSky()
        {
        return bean.getSky();
        }

    @Override
    public Float getMagdiff()
        {
        return bean.getMagdiff();
        }

    @Override
    public Float getFwhm()
        {
        return bean.getFwhm();
        }

    @Override
    public Float getClasstar()
        {
        return bean.getClasstar();
        }

    @Override
    public Float getMindtoedge()
        {
        return bean.getMindtoedge();
        }

    @Override
    public Float getMagfromlim()
        {
        return bean.getMagfromlim();
        }

    @Override
    public Float getSeeratio()
        {
        return bean.getSeeratio();
        }

    @Override
    public Float getAimage()
        {
        return bean.getAimage();
        }

    @Override
    public Float getBimage()
        {
        return bean.getBimage();
        }

    @Override
    public Float getAimagerat()
        {
        return bean.getAimagerat();
        }

    @Override
    public Float getBimagerat()
        {
        return bean.getBimagerat();
        }

    @Override
    public Float getElong()
        {
        return bean.getElong();
        }

    @Override
    public Integer getNneg()
        {
        return bean.getNneg();
        }

    @Override
    public Integer getNbad()
        {
        return bean.getNbad();
        }

    @Override
    public Float getRb()
        {
        return bean.getRb();
        }

    @Override
    public Float getSsdistnr()
        {
        return bean.getSsdistnr();
        }

    @Override
    public Float getSsmagnr()
        {
        return bean.getSsmagnr();
        }

    @Override
    public CharSequence getSsnamenr()
        {
        return bean.getSsnamenr();
        }

    @Override
    public Float getSumrat()
        {
        return bean.getSumrat();
        }

    @Override
    public Float getMagapbig()
        {
        return bean.getMagapbig();
        }

    @Override
    public Float getSigmagapbig()
        {
        return bean.getSigmagapbig();
        }

    @Override
    public Double getRanr()
        {
        return bean.getRanr();
        }

    @Override
    public Double getDecnr()
        {
        return bean.getDecnr();
        }

    @Override
    public Double getScorr()
        {
        return bean.getScorr();
        }

    @Override
    public CharSequence getRbversion()
        {
        return bean.getRbversion();
        }

    }
