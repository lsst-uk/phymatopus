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
package uk.ac.roe.wfau.phymatopus.cassandra;


import com.datastax.oss.driver.api.core.cql.PreparedStatement;

import uk.ac.roe.wfau.phymatopus.alert.AlertCandidate;
import uk.ac.roe.wfau.phymatopus.alert.BaseAlert;

/**
 * Simple writer for the candidates table.
 *
 */
public class CandiateWriter
extends AbstractCassandraWriter
    {
    
    /**
     * Public constructor. 
     * 
     */
    public CandiateWriter(final String hostname, final String dcname)
        {
        super(
            hostname,
            dcname
            );
        }

    private PreparedStatement insert ;        
    
    @Override
    protected void prepare()
        {
        if (this.insert == null)
            {
            this.insert = this.session().prepare(
                "INSERT INTO ztftest.candidates ("
                    + "candid,"
                    + "objectid,"
                    + "topic,"
                    + "field,"
                    + "ra,"
                    + "dec,"
                    + "jd,"
                    + "fid,"
                    + "pid,"
                    + "diffmaglim,"
                    + "pdiffimfilename,"
                    + "programpi,"
                    + "programid,"
                    + "isdiffpos,"
                    + "tblid,"
                    + "nid,"
                    + "rcid,"
                    + "xpos,"
                    + "ypos,"
                    + "magpsf,"
                    + "sigmapsf,"
                    + "chipsf,"
                    + "magap,"
                    + "sigmagap,"
                    + "distnr,"
                    + "magnr,"
                    + "sigmagnr,"
                    + "chinr,"
                    + "sharpnr,"
                    + "sky,"
                    + "magdiff,"
                    + "fwhm,"
                    + "classtar,"
                    + "mindtoedge,"
                    + "magfromlim,"
                    + "seeratio,"
                    + "aimage,"
                    + "bimage,"
                    + "aimagerat,"
                    + "bimagerat,"
                    + "elong,"
                    + "nneg,"
                    + "nbad,"
                    + "rb,"
                    + "ssdistnr,"
                    + "ssmagnr,"
                    + "ssnamenr,"
                    + "sumrat,"
                    + "magapbig,"
                    + "sigmagapbig,"
                    + "ranr,"
                    + "decnr,"
                    + "scorr,"
                    + "rbversion,"
                    + "sgmag1,"
                    + "srmag1,"
                    + "simag1,"
                    + "szmag1,"
                    + "sgscore1,"
                    + "distpsnr1,"
                    + "ndethist,"
                    + "ncovhist,"
                    + "jdstarthist,"
                    + "jdendhist,"
                    + "tooflag,"
                    + "objectidps1,"
                    + "objectidps2,"
                    + "sgmag2,"
                    + "srmag2,"
                    + "simag2,"
                    + "szmag2,"
                    + "sgscore2,"
                    + "distpsnr2,"
                    + "objectidps3,"
                    + "sgmag3,"
                    + "srmag3,"
                    + "simag3,"
                    + "szmag3,"
                    + "sgscore3,"
                    + "distpsnr3,"
                    + "nmtchps,"
                    + "rfid,"
                    + "jdstartref,"
                    + "jdendref,"
                    + "nframesref,"
                    + "dsnrms,"
                    + "ssnrms,"
                    + "dsdiff,"
                    + "magzpsci,"
                    + "magzpsciunc,"
                    + "magzpscirms,"
                    + "nmatches,"
                    + "clrcoeff,"
                    + "clrcounc,"
                    + "zpclrcov,"
                    + "zpmed,"
                    + "clrmed,"
                    + "clrrms,"
                    + "neargaia,"
                    + "neargaiabright,"
                    + "maggaia,"
                    + "maggaiabright"
                + ") VALUES ("
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?,"
                    + "?"
                    + ")"
                );
            }
        }

    protected String notNull(final CharSequence value)
        {
        if (null != value)
            {
            return value.toString() ;
            }
        else {
            return "";
            }
        }

    protected Integer notNull(final Integer value)
        {
        if (null != value)
            {
            return value ;
            }
        else {
            return 0;
            }
        }

    protected Long notNull(final Long value)
        {
        if (null != value)
            {
            return value ;
            }
        else {
            return 0L;
            }
        }

    protected Float notNull(final Float value)
        {
        if (null != value)
            {
            return value ;
            }
        else {
            return Float.NaN;
            }
        }

    protected Double notNull(final Double value)
        {
        if (null != value)
            {
            return value ;
            }
        else {
            return Double.NaN;
            }
        }
    
    @Override
    public void process(final BaseAlert alert)
        {
        AlertCandidate candidate = alert.getCandidate();
        this.session().execute(
            this.insert.bind(
                notNull(candidate.getCandid()),
                notNull(candidate.getObjectId()),
                notNull(candidate.getTopic()),
                notNull(candidate.getField()),
                notNull(candidate.getRa()),
                notNull(candidate.getDec()),
                notNull(candidate.getJd()),
                notNull(candidate.getFid()),
                notNull(candidate.getPid()),
                notNull(candidate.getDiffmaglim()),
                notNull(candidate.getPdiffimfilename()),
                notNull(candidate.getProgrampi()),
                notNull(candidate.getProgramid()),
                notNull(candidate.getIsdiffpos()),
                notNull(candidate.getTblid()),
                notNull(candidate.getNid()),
                notNull(candidate.getRcid()),
                notNull(candidate.getXpos()),
                notNull(candidate.getYpos()),
                notNull(candidate.getMagpsf()),
                notNull(candidate.getSigmapsf()),
                notNull(candidate.getChipsf()),
                notNull(candidate.getMagap()),
                notNull(candidate.getSigmagap()),
                notNull(candidate.getDistnr()),
                notNull(candidate.getMagnr()),
                notNull(candidate.getSigmagnr()),
                notNull(candidate.getChinr()),
                notNull(candidate.getSharpnr()),
                notNull(candidate.getSky()),
                notNull(candidate.getMagdiff()),
                notNull(candidate.getFwhm()),
                notNull(candidate.getClasstar()),
                notNull(candidate.getMindtoedge()),
                notNull(candidate.getMagfromlim()),
                notNull(candidate.getSeeratio()),
                notNull(candidate.getAimage()),
                notNull(candidate.getBimage()),
                notNull(candidate.getAimagerat()),
                notNull(candidate.getBimagerat()),
                notNull(candidate.getElong()),
                notNull(candidate.getNneg()),
                notNull(candidate.getNbad()),
                notNull(candidate.getRb()),
                notNull(candidate.getSsdistnr()),
                notNull(candidate.getSsmagnr()),
                notNull(candidate.getSsnamenr()),
                notNull(candidate.getSumrat()),
                notNull(candidate.getMagapbig()),
                notNull(candidate.getSigmagapbig()),
                notNull(candidate.getRanr()),
                notNull(candidate.getDecnr()),
                notNull(candidate.getScorr()),
                notNull(candidate.getRbversion()),
                notNull(candidate.getSgmag1()),
                notNull(candidate.getSrmag1()),
                notNull(candidate.getSimag1()),
                notNull(candidate.getSzmag1()),
                notNull(candidate.getSgscore1()),
                notNull(candidate.getDistpsnr1()),
                notNull(candidate.getNdethist()),
                notNull(candidate.getNcovhist()),
                notNull(candidate.getJdstarthist()),
                notNull(candidate.getJdendhist()),
                notNull(candidate.getTooflag()),
                notNull(candidate.getObjectidps1()),
                notNull(candidate.getObjectidps2()),
                notNull(candidate.getSgmag2()),
                notNull(candidate.getSrmag2()),
                notNull(candidate.getSimag2()),
                notNull(candidate.getSzmag2()),
                notNull(candidate.getSgscore2()),
                notNull(candidate.getDistpsnr2()),
                notNull(candidate.getObjectidps3()),
                notNull(candidate.getSgmag3()),
                notNull(candidate.getSrmag3()),
                notNull(candidate.getSimag3()),
                notNull(candidate.getSzmag3()),
                notNull(candidate.getSgscore3()),
                notNull(candidate.getDistpsnr3()),
                notNull(candidate.getNmtchps()),
                notNull(candidate.getRfid()),
                notNull(candidate.getJdstartref()),
                notNull(candidate.getJdendref()),
                notNull(candidate.getNframesref()),
                notNull(candidate.getDsnrms()),
                notNull(candidate.getSsnrms()),
                notNull(candidate.getDsdiff()),
                notNull(candidate.getMagzpsci()),
                notNull(candidate.getMagzpsciunc()),
                notNull(candidate.getMagzpscirms()),
                notNull(candidate.getNmatches()),
                notNull(candidate.getClrcoeff()),
                notNull(candidate.getClrcounc()),
                notNull(candidate.getZpclrcov()),
                notNull(candidate.getZpmed()),
                notNull(candidate.getClrmed()),
                notNull(candidate.getClrrms()),
                notNull(candidate.getNeargaia()),
                notNull(candidate.getNeargaiabright()),
                notNull(candidate.getMaggaia()),
                notNull(candidate.getMaggaiabright())
                )
            );        
        }
    }
