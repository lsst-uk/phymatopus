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


import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import com.datastax.oss.driver.api.core.cql.PreparedStatement;

import lombok.extern.slf4j.Slf4j;
import uk.ac.roe.wfau.phymatopus.kafka.alert.ZtfAlert;
import uk.ac.roe.wfau.phymatopus.kafka.alert.ZtfCandidate;

/**
 * Simple writer for the objects table.
 *
 */
@Slf4j
public class ZtfObjectWriter
extends AbstractCassandraWriter
    {
    /**
     * Public constructor. 
     * 
     */
    public ZtfObjectWriter(final String hostname, final String dcname)
        {
        super(
            hostname,
            dcname
            );
        }

    private PreparedStatement select ;        
    private PreparedStatement insert ;        
    private PreparedStatement update ;        

    @Override
    protected void prepare()
        {
        /*
         * 
        if (null == this.select)
            {
            this.select = this.session().prepare(
                "SELECT objectid FROM ztftest.simple_objects WHERE objectid = ?"
                );
            }

        if (null == this.insert)
            {
            this.insert = this.session().prepare(
                "INSERT INTO ztftest.simple_objects () VALUES ()"
                );
            }
         * 
         */

        if (null == this.update)
            {
            this.update = this.session().prepare(
                "UPDATE "
                    + "ztftest.simple_objects "
                + "SET "
                    + "ncand       = :ncand, "
                    + "ramean      = :ramean, "
                    + "rastd       = :rastd, "
                    + "decmean     = :decmean, "
                    + "decstd      = :decstd, "
                    + "maggmin     = :maggmin, "
                    + "maggmax     = :maggmax, "
                    + "maggmedian  = :maggmedian, "
                    + "maggmean    = :maggmean, "
                    + "magrmin     = :magrmin, "
                    + "magrmax     = :magrmax, "
                    + "magrmedian  = :magrmedian, "
                    + "magrmean    = :magrmean, "
                    + "latestgmag  = :latestgmag, "
                    + "latestrmag  = :latestrmag, "
                    + "jdmin       = :jdmin, "
                    + "jdmax       = :jdmax, "
                    + "glatmean    = :glatmean, "
                    + "glonmean    = :glonmean "
                + "WHERE "
                    + "objectid = :objectid"
                );
            }
        }

    @Override
    protected void process(final ZtfAlert alert)
        {
        DescriptiveStatistics ra   = new DescriptiveStatistics();        
        DescriptiveStatistics jd   = new DescriptiveStatistics();        
        DescriptiveStatistics dec  = new DescriptiveStatistics();        
        DescriptiveStatistics magg = new DescriptiveStatistics();        
        DescriptiveStatistics magr = new DescriptiveStatistics();        

        //double  glat;
        //double  glon;
        Float lastg = null;
        Float lastr = null;

        int count = 1 ;
        
        //
        // Add the previous candidates.
        for (ZtfCandidate prev : alert.getPrvCandidates())
            {
            count++ ;
            
            log.trace("jd  [{}][{}]", prev.getJd(),  jd);
            log.trace("ra  [{}][{}]", prev.getRa(),  ra);
            log.trace("dec [{}][{}]", prev.getDec(), dec);

            jd.addValue(prev.getJd());
            ra.addValue(prev.getRa());
            dec.addValue(prev.getDec());

            if (prev.getFid() == 1)
                {
                magg.addValue(prev.getMagpsf());
                lastg = prev.getMagpsf();
                }
            else {
                magr.addValue(prev.getMagpsf());
                lastr = prev.getMagpsf();
                }
            }

        //
        // Add this candidate.
        ZtfCandidate cand = alert.getCandidate();

        jd.addValue(cand.getJd());
        ra.addValue(cand.getRa());
        dec.addValue(cand.getDec());

        if (cand.getFid() == 1)
            {
            magg.addValue(cand.getMagpsf());
            lastg = cand.getMagpsf();
            }
        else {
            magr.addValue(cand.getMagpsf());
            lastr = cand.getMagpsf();
            }

        Double maggmin  = null ;
        Double maggmax  = null ;
        Double maggmed  = null ;
        Double maggmean = null ;

        Double magrmin  = null ;
        Double magrmax  = null ;
        Double magrmed  = null ;
        Double magrmean = null ;

        if (magg.getN() > 0)
            {
            maggmin  = magg.getMin();
            maggmax  = magg.getMax();
            maggmean = magg.getMean();
            maggmed  = magg.getPercentile(50.0);
            }
        
        if (magr.getN() > 0)
            {
            magrmin  = magr.getMin();
            magrmax  = magr.getMax();
            magrmean = magr.getMean();
            magrmed  = magr.getPercentile(50.0);
            }

        Double jdmin  = jd.getMin();
        Double jdmax  = jd.getMax();

        Double ramean = ra.getMean(); 
        Double rastd  = 3600 * ra.getStandardDeviation(); 

        Double decmean = dec.getMean(); 
        Double decstd  = 3600 * dec.getStandardDeviation(); 

        this.session().execute(
            this.update.bind(
                count,
                ramean,
                rastd,
                decmean,
                decstd,
                maggmin,
                maggmax,
                maggmed,
                maggmean,
                magrmin,
                magrmax,
                magrmed,
                magrmean,
                lastg,
                lastr,
                jdmin,
                jdmax,
                null,
                null
                )
            );
        }
    }
