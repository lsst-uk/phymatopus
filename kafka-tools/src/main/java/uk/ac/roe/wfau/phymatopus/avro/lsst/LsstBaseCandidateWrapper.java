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
package uk.ac.roe.wfau.phymatopus.avro.lsst;

import org.apache.avro.generic.GenericData;

import uk.ac.roe.wfau.phymatopus.alert.BaseCandidate;

public class LsstBaseCandidateWrapper
implements BaseCandidate
    {
    public LsstBaseCandidateWrapper(final GenericData.Record record, final CharSequence objectid)
        {
        this.record = record;
        }

    protected GenericData.Record record;
    protected GenericData.Record record()
        {
        return this.record;
        }

    protected CharSequence toChar(final Object value)
        {
        if (null != value)
            {
            return (CharSequence) value ;
            }
        else {
            return null;
            }
        }

    protected Integer toInt(final Object value)
        {
        if (null != value)
            {
            return (Integer) value ;
            }
        else {
            return null;
            }
        }

    protected Long toLong(final Object value)
        {
        if (null != value)
            {
            return (Long) value ;
            }
        else {
            return null;
            }
        }

    protected Float toFloat(final Object value)
        {
        if (null != value)
            {
            return (Float) value ;
            }
        else {
            return null;
            }
        }

    protected Double toDouble(final Object value)
        {
        if (null != value)
            {
            return (Double) value ;
            }
        else {
            return null;
            }
        }
    
    private CharSequence objectid;
    @Override
    public CharSequence getObjectId()
        {
        return toChar(this.objectid);
        }

    @Override
    public Double getJd()
        {
        return toDouble(record.get(0));
        }

    @Override
    public Integer getFid()
        {
        return toInt(record.get(1));
        }

    @Override
    public Long getPid()
        {
        return toLong(record.get(2));
        }

    @Override
    public Float getDiffmaglim()
        {
        return toFloat(record.get(3));
        }

    @Override
    public CharSequence getPdiffimfilename()
        {
        return toChar(record.get(4));
        }

    @Override
    public CharSequence getProgrampi()
        {
        return toChar(record.get(5));
        }

    @Override
    public Integer getProgramid()
        {
        return toInt(record.get(6));
        }

    @Override
    public Long getCandid()
        {
        return toLong(record.get(7));
        }
    
    @Override
    public CharSequence getIsdiffpos()
        {
        return toChar(record.get(8));
        }

    @Override
    public Long getTblid()
        {
        return toLong(record.get(9));
        }

    @Override
    public Integer getNid()
        {
        return toInt(record.get(10));
        }

    @Override
    public Integer getRcid()
        {
        return toInt(record.get(11));
        }

    @Override
    public Integer getField()
        {
        return toInt(record.get(12));
        }

    @Override
    public Float getXpos()
        {
        return toFloat(record.get(13));
        }

    @Override
    public Float getYpos()
        {
        return toFloat(record.get(14));
        }

    @Override
    public Double getRa()
        {
        return toDouble(record.get(15));
        }

    @Override
    public Double getDec()
        {
        return toDouble(record.get(16));
        }

    @Override
    public Float getMagpsf()
        {
        return toFloat(record.get(17));
        }

    @Override
    public Float getSigmapsf()
        {
        return toFloat(record.get(18));
        }

    @Override
    public Float getChipsf()
        {
        return toFloat(record.get(19));
        }

    @Override
    public Float getMagap()
        {
        return toFloat(record.get(20));
        }

    @Override
    public Float getSigmagap()
        {
        return toFloat(record.get(21));
        }

    @Override
    public Float getDistnr()
        {
        return toFloat(record.get(22));
        }

    @Override
    public Float getMagnr()
        {
        return toFloat(record.get(23));
        }

    @Override
    public Float getSigmagnr()
        {
        return toFloat(record.get(24));
        }

    @Override
    public Float getChinr()
        {
        return toFloat(record.get(25));
        }

    @Override
    public Float getSharpnr()
        {
        return toFloat(record.get(26));
        }

    @Override
    public Float getSky()
        {
        return toFloat(record.get(27));
        }

    @Override
    public Float getMagdiff()
        {
        return toFloat(record.get(28));
        }

    @Override
    public Float getFwhm()
        {
        return toFloat(record.get(29));
        }

    @Override
    public Float getClasstar()
        {
        return toFloat(record.get(30));
        }

    @Override
    public Float getMindtoedge()
        {
        return toFloat(record.get(31));
        }

    @Override
    public Float getMagfromlim()
        {
        return toFloat(record.get(32));
        }

    @Override
    public Float getSeeratio()
        {
        return toFloat(record.get(33));
        }

    @Override
    public Float getAimage()
        {
        return toFloat(record.get(34));
        }

    @Override
    public Float getBimage()
        {
        return toFloat(record.get(35));
        }

    @Override
    public Float getAimagerat()
        {
        return toFloat(record.get(36));
        }

    @Override
    public Float getBimagerat()
        {
        return toFloat(record.get(37));
        }

    @Override
    public Float getElong()
        {
        return toFloat(record.get(38));
        }

    @Override
    public Integer getNneg()
        {
        return toInt(record.get(39));
        }

    @Override
    public Integer getNbad()
        {
        return toInt(record.get(40));
        }

    @Override
    public Float getRb()
        {
        return toFloat(record.get(41));
        }

    @Override
    public Float getSsdistnr()
        {
        return toFloat(record.get(42));
        }

    @Override
    public Float getSsmagnr()
        {
        return toFloat(record.get(43));
        }

    @Override
    public CharSequence getSsnamenr()
        {
        return toChar(record.get(44));
        }

    @Override
    public Float getSumrat()
        {
        return toFloat(record.get(45));
        }

    @Override
    public Float getMagapbig()
        {
        return toFloat(record.get(46));
        }

    @Override
    public Float getSigmagapbig()
        {
        return toFloat(record.get(47));
        }

    @Override
    public Double getRanr()
        {
        return toDouble(record.get(48));
        }

    @Override
    public Double getDecnr()
        {
        return toDouble(record.get(49));
        }

    // Different to AlertCandidate
    @Override
    public Double getScorr()
        {
        return toDouble(record.get(50));
        }

    // Different to AlertCandidate
    @Override
    public CharSequence getRbversion()
        {
        return toChar(record.get(51));
        }
    }