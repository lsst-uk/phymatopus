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
package uk.ac.roe.wfau.phymatopus.kafka.cassandra;


import java.net.InetSocketAddress;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.CqlSessionBuilder;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;

import lombok.extern.slf4j.Slf4j;
import uk.ac.roe.wfau.phymatopus.kafka.alert.ZtfAlertCandidate;

/**
 * Simple writer for the candidates table.
 *
 */
@Slf4j
public class SimpleCandiateManager
    {
    protected static CqlSession session ; 
    protected static PreparedStatement prepared ;        
    
    /**
     * Public constructor. 
     * 
     */
    public SimpleCandiateManager()
        {
        
        }
    
    /**
     * Initialise our cluster.
     * 
     */
    public void init()
        {
        log.debug("init - start");
        synchronized (SimpleCandiateManager.class)
            {
            if (SimpleCandiateManager.session == null)
                {
                CqlSessionBuilder builder = CqlSession.builder().addContactPoint(
                    new InetSocketAddress(
                        "Umiwiel",
                        9042
                        )
                    ).withLocalDatacenter(
                        "datacenter1"
                        );
                SimpleCandiateManager.session = builder.build();
                }
            if (SimpleCandiateManager.prepared== null)
                {
                SimpleCandiateManager.prepared = session.prepare(
                    "INSERT INTO ztftest.simple_candidates ("
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
                    + ") values ("
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
        log.debug("init - done");
        }

    /**
     * Disconnect our cluster.
     * 
     */
    public void done()
        {
        log.debug("done- start");
        synchronized (SimpleCandiateManager.class)
            {
            if (SimpleCandiateManager.session != null)
                {
                session.close();
                session = null ;
                }
            }
        log.debug("init - done");
        }

    /**
     * Insert a candidate into the database.
     * 
     */
    public void insert(final ZtfAlertCandidate candidate)
        {
        BoundStatement bound = prepared.bind(
            candidate.getField(),
            candidate.getRa(),
            candidate.getDec(),
            candidate.getJd(),
            candidate.getFid(),
            candidate.getPid(),
            candidate.getDiffmaglim(),
            candidate.getPdiffimfilename(),
            candidate.getProgrampi(),
            candidate.getProgramid(),
            candidate.getIsdiffpos(),
            candidate.getTblid(),
            candidate.getNid(),
            candidate.getRcid(),
            candidate.getXpos(),
            candidate.getYpos(),
            candidate.getMagpsf(),
            candidate.getSigmapsf(),
            candidate.getChipsf(),
            candidate.getMagap(),
            candidate.getSigmagap(),
            candidate.getDistnr(),
            candidate.getMagnr(),
            candidate.getSigmagnr(),
            candidate.getChinr(),
            candidate.getSharpnr(),
            candidate.getSky(),
            candidate.getMagdiff(),
            candidate.getFwhm(),
            candidate.getClasstar(),
            candidate.getMindtoedge(),
            candidate.getMagfromlim(),
            candidate.getSeeratio(),
            candidate.getAimage(),
            candidate.getBimage(),
            candidate.getAimagerat(),
            candidate.getBimagerat(),
            candidate.getElong(),
            candidate.getNneg(),
            candidate.getNbad(),
            candidate.getRb(),
            candidate.getSsdistnr(),
            candidate.getSsmagnr(),
            candidate.getSsnamenr(),
            candidate.getSumrat(),
            candidate.getMagapbig(),
            candidate.getSigmagapbig(),
            candidate.getRanr(),
            candidate.getDecnr(),
            candidate.getScorr(),
            candidate.getRbversion(),
            candidate.getSgmag1(),
            candidate.getSrmag1(),
            candidate.getSimag1(),
            candidate.getSzmag1(),
            candidate.getSgscore1(),
            candidate.getDistpsnr1(),
            candidate.getNdethist(),
            candidate.getNcovhist(),
            candidate.getJdstarthist(),
            candidate.getJdendhist(),
            candidate.getTooflag(),
            candidate.getObjectidps1(),
            candidate.getObjectidps2(),
            candidate.getSgmag2(),
            candidate.getSrmag2(),
            candidate.getSimag2(),
            candidate.getSzmag2(),
            candidate.getSgscore2(),
            candidate.getDistpsnr2(),
            candidate.getObjectidps3(),
            candidate.getSgmag3(),
            candidate.getSrmag3(),
            candidate.getSimag3(),
            candidate.getSzmag3(),
            candidate.getSgscore3(),
            candidate.getDistpsnr3(),
            candidate.getNmtchps(),
            candidate.getRfid(),
            candidate.getJdstartref(),
            candidate.getJdendref(),
            candidate.getNframesref(),
            candidate.getDsnrms(),
            candidate.getSsnrms(),
            candidate.getDsdiff(),
            candidate.getMagzpsci(),
            candidate.getMagzpsciunc(),
            candidate.getMagzpscirms(),
            candidate.getNmatches(),
            candidate.getClrcoeff(),
            candidate.getClrcounc(),
            candidate.getZpclrcov(),
            candidate.getZpmed(),
            candidate.getClrmed(),
            candidate.getClrrms(),
            candidate.getNeargaia(),
            candidate.getNeargaiabright(),
            candidate.getMaggaia(),
            candidate.getMaggaiabright()
            );
        session.execute(bound);        
        }
    
    /**
     * Select a candidate from the database.
     * 
     */
    public ZtfAlertCandidate select(final Long candid)
        {
        return null ;
        }
    }
