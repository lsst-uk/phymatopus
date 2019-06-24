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

import org.apache.avro.generic.GenericData;

public class LsstAlertCandidateWrapper
extends LsstCandidateWrapper
implements ZtfAlertCandidate
    {
    private GenericData.Record record;
    public LsstAlertCandidateWrapper(final GenericData.Record record, final CharSequence objectid, final String topic)
        {
        super(
            record,
            objectid
            );
        this.topic  = topic ;
        }
    
    private String topic;
    @Override
    public String getTopic()
        {
        return this.topic;
        }
    
    @Override
    public Float getSgmag1()
        {
        return toFloat(record.get(50));
        }

    @Override
    public Float getSrmag1()
        {
        return toFloat(record.get(51));
        }

    @Override
    public Float getSimag1()
        {
        return toFloat(record.get(52));
        }

    @Override
    public Float getSzmag1()
        {
        return toFloat(record.get(53));
        }

    @Override
    public Float getSgscore1()
        {
        return toFloat(record.get(54));
        }

    @Override
    public Float getDistpsnr1()
        {
        return toFloat(record.get(55));
        }

    @Override
    public Integer getNdethist()
        {
        return toInt(record.get(56));
        }

    @Override
    public Integer getNcovhist()
        {
        return toInt(record.get(57));
        }

    @Override
    public Double getJdstarthist()
        {
        return toDouble(record.get(58));
        }

    @Override
    public Double getJdendhist()
        {
        return toDouble(record.get(59));
        }
//ZRQ
    @Override
    public Double getScorr()
        {
        return toDouble(record.get(60));
        }

    @Override
    public Integer getTooflag()
        {
        return toInt(record.get(61));
        }

    @Override
    public Long getObjectidps1()
        {
        return toLong(record.get(62));
        }

    @Override
    public Long getObjectidps2()
        {
        return toLong(record.get(63));
        }

    @Override
    public Float getSgmag2()
        {
        return toFloat(record.get(64));
        }

    @Override
    public Float getSrmag2()
        {
        return toFloat(record.get(65));
        }

    @Override
    public Float getSimag2()
        {
        return toFloat(record.get(66));
        }

    @Override
    public Float getSzmag2()
        {
        return toFloat(record.get(67));
        }

    @Override
    public Float getSgscore2()
        {
        return toFloat(record.get(68));
        }

    @Override
    public Float getDistpsnr2()
        {
        return toFloat(record.get(69));
        }

    @Override
    public Long getObjectidps3()
        {
        return toLong(record.get(70));
        }

    @Override
    public Float getSgmag3()
        {
        return toFloat(record.get(71));
        }

    @Override
    public Float getSrmag3()
        {
        return toFloat(record.get(72));
        }

    @Override
    public Float getSimag3()
        {
        return toFloat(record.get(73));
        }

    @Override
    public Float getSzmag3()
        {
        return toFloat(record.get(74));
        }

    @Override
    public Float getSgscore3()
        {
        return toFloat(record.get(75));
        }

    @Override
    public Float getDistpsnr3()
        {
        return toFloat(record.get(76));
        }

    @Override
    public Integer getNmtchps()
        {
        return toInt(record.get(77));
        }

    @Override
    public Long getRfid()
        {
        return toLong(record.get(78));
        }

    @Override
    public Double getJdstartref()
        {
        return toDouble(record.get(79));
        }

    @Override
    public Double getJdendref()
        {
        return toDouble(record.get(80));
        }

    @Override
    public Integer getNframesref()
        {
        return toInt(record.get(81));
        }

    @Override
    public CharSequence getRbversion()
        {
        return (CharSequence) record.get(82);
        }

    @Override
    public Float getDsnrms()
        {
        return toFloat(record.get(83));
        }

    @Override
    public Float getSsnrms()
        {
        return toFloat(record.get(84));
        }

    @Override
    public Float getDsdiff()
        {
        return toFloat(record.get(85));
        }

    @Override
    public Float getMagzpsci()
        {
        return toFloat(record.get(86));
        }

    @Override
    public Float getMagzpsciunc()
        {
        return toFloat(record.get(87));
        }

    @Override
    public Float getMagzpscirms()
        {
        return toFloat(record.get(88));
        }

    @Override
    public Integer getNmatches()
        {
        return toInt(record.get(89));
        }

    @Override
    public Float getClrcoeff()
        {
        return toFloat(record.get(90));
        }

    @Override
    public Float getClrcounc()
        {
        return toFloat(record.get(91));
        }

    @Override
    public Float getZpclrcov()
        {
        return toFloat(record.get(92));
        }

    @Override
    public Float getZpmed()
        {
        return toFloat(record.get(93));
        }

    @Override
    public Float getClrmed()
        {
        return toFloat(record.get(94));
        }

    @Override
    public Float getClrrms()
        {
        return toFloat(record.get(95));
        }

    @Override
    public Float getNeargaia()
        {
        return toFloat(record.get(96));
        }

    @Override
    public Float getNeargaiabright()
        {
        return toFloat(record.get(97));
        }

    @Override
    public Float getMaggaia()
        {
        return toFloat(record.get(98));
        }

    @Override
    public Float getMaggaiabright()
        {
        return toFloat(record.get(99));
        }
    }