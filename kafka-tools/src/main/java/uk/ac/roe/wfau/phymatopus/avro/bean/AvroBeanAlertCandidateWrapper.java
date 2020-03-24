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

import uk.ac.roe.wfau.phymatopus.alert.AlertCandidate;

/**
 * A wrapper class for the ZTF {@link alert} bean generated from the JSON schema.  
 *
 */
public class AvroBeanAlertCandidateWrapper
implements AlertCandidate
    {
    /**
     * Our Avro alert bean.
     *
     */
    private ztf.candidate bean ;

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
    public AvroBeanAlertCandidateWrapper(final String topic, final CharSequence objectid, final ztf.candidate bean)
        {
        this.topic = topic;
        this.objectid = objectid;
        this.bean  = bean;
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
//ZRQ
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

    @Override
    public Float getSgmag1()
        {
        return bean.getSgmag1();
        }

    @Override
    public Float getSrmag1()
        {
        return bean.getSrmag1();
        }

    @Override
    public Float getSimag1()
        {
        return bean.getSimag1();
        }

    @Override
    public Float getSzmag1()
        {
        return bean.getSzmag1();
        }

    @Override
    public Float getSgscore1()
        {
        return bean.getSgscore1();
        }

    @Override
    public Float getDistpsnr1()
        {
        return bean.getDistpsnr1();
        }

    @Override
    public Integer getNdethist()
        {
        return bean.getNdethist();
        }

    @Override
    public Integer getNcovhist()
        {
        return bean.getNcovhist();
        }

    @Override
    public Double getJdstarthist()
        {
        return bean.getJdstarthist();
        }

    @Override
    public Double getJdendhist()
        {
        return bean.getJdendhist();
        }

    @Override
    public Integer getTooflag()
        {
        return bean.getTooflag();
        }

    @Override
    public Long getObjectidps1()
        {
        return bean.getObjectidps1();
        }

    @Override
    public Long getObjectidps2()
        {
        return bean.getObjectidps2();
        }

    @Override
    public Float getSgmag2()
        {
        return bean.getSgmag2();
        }

    @Override
    public Float getSrmag2()
        {
        return bean.getSrmag2();
        }

    @Override
    public Float getSimag2()
        {
        return bean.getSimag2();
        }

    @Override
    public Float getSzmag2()
        {
        return bean.getSzmag2();
        }

    @Override
    public Float getSgscore2()
        {
        return bean.getSgscore2();
        }

    @Override
    public Float getDistpsnr2()
        {
        return bean.getDistpsnr2();
        }

    @Override
    public Long getObjectidps3()
        {
        return bean.getObjectidps3();
        }

    @Override
    public Float getSgmag3()
        {
        return bean.getSgmag3();
        }

    @Override
    public Float getSrmag3()
        {
        return bean.getSrmag3();
        }

    @Override
    public Float getSimag3()
        {
        return bean.getSimag3();
        }

    @Override
    public Float getSzmag3()
        {
        return bean.getSzmag3();
        }

    @Override
    public Float getSgscore3()
        {
        return bean.getSgscore3();
        }

    @Override
    public Float getDistpsnr3()
        {
        return bean.getDistpsnr3();
        }

    @Override
    public Integer getNmtchps()
        {
        return bean.getNmtchps();
        }

    @Override
    public Long getRfid()
        {
        return bean.getRfid();
        }

    @Override
    public Double getJdstartref()
        {
        return bean.getJdstartref();
        }

    @Override
    public Double getJdendref()
        {
        return bean.getJdendref();
        }

    @Override
    public Integer getNframesref()
        {
        return bean.getNframesref();
        }

    @Override
    public Float getDsnrms()
        {
        return bean.getDsnrms();
        }

    @Override
    public Float getSsnrms()
        {
        return bean.getSsnrms();
        }

    @Override
    public Float getDsdiff()
        {
        return bean.getDsdiff();
        }

    @Override
    public Float getMagzpsci()
        {
        return bean.getMagzpsci();
        }

    @Override
    public Float getMagzpsciunc()
        {
        return bean.getMagzpsciunc();
        }

    @Override
    public Float getMagzpscirms()
        {
        return bean.getMagzpscirms();
        }

    @Override
    public Integer getNmatches()
        {
        return bean.getNmatches();
        }

    @Override
    public Float getClrcoeff()
        {
        return bean.getClrcoeff();
        }

    @Override
    public Float getClrcounc()
        {
        return bean.getClrcounc();
        }

    @Override
    public Float getZpclrcov()
        {
        return bean.getZpclrcov();
        }

    @Override
    public Float getZpmed()
        {
        return bean.getZpmed();
        }

    @Override
    public Float getClrmed()
        {
        return bean.getClrmed();
        }

    @Override
    public Float getClrrms()
        {
        return bean.getClrrms();
        }

    @Override
    public Float getNeargaia()
        {
        return bean.getNeargaia();
        }

    @Override
    public Float getNeargaiabright()
        {
        return bean.getNeargaiabright();
        }

    @Override
    public Float getMaggaia()
        {
        return bean.getMaggaia();
        }

    @Override
    public Float getMaggaiabright()
        {
        return bean.getMaggaiabright();
        }

    private String topic;
    @Override
    public String getTopic()
        {
        return this.topic;
        }
    }
