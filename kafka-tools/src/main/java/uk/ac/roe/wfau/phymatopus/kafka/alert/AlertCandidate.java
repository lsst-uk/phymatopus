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

public interface AlertCandidate
extends BaseCandidate
    {
    /**
     * The Kafka topic this alert was read from.
     *  
     */
    public String getTopic();

    /**
     * Gets the value of the 'sgmag1' field.
     * @return g-band PSF-fit magnitude of closest source from PS1 catalog; if exists within 30 arcsec [mag]
     */
    public Float getSgmag1();

    /**
     * Gets the value of the 'srmag1' field.
     * @return r-band PSF-fit magnitude of closest source from PS1 catalog; if exists within 30 arcsec [mag]
     */
    public Float getSrmag1();

    /**
     * Gets the value of the 'simag1' field.
     * @return i-band PSF-fit magnitude of closest source from PS1 catalog; if exists within 30 arcsec [mag]
     */
    public Float getSimag1();

    /**
     * Gets the value of the 'szmag1' field.
     * @return z-band PSF-fit magnitude of closest source from PS1 catalog; if exists within 30 arcsec [mag]
     */
    public Float getSzmag1();

    /**
     * Gets the value of the 'sgscore1' field.
     * @return Star/Galaxy score of closest source from PS1 catalog; if exists within 30 arcsec: 0 <= sgscore <= 1 where closer to 1 implies higher likelihood of being a star
     */
    public Float getSgscore1();

    /**
     * Gets the value of the 'distpsnr1' field.
     * @return Distance to closest source from PS1 catalog; if exists within 30 arcsec [arcsec]
     */
    public Float getDistpsnr1();

    /**
     * Gets the value of the 'ndethist' field.
     * @return Number of spatially-coincident detections falling within 1.5 arcsec going back to beginning of survey; only detections that fell on the same field and readout-channel ID where the input candidate was observed are counted
     */
    public Integer getNdethist();

    /**
     * Gets the value of the 'ncovhist' field.
     * @return Number of times input candidate position fell on any field and readout-channel going back to beginning of survey
     */
    public Integer getNcovhist();

    /**
     * Gets the value of the 'jdstarthist' field.
     * @return Earliest Julian date of epoch corresponding to ndethist [days]
     */
    public Double getJdstarthist();

    /**
     * Gets the value of the 'jdendhist' field.
     * @return Latest Julian date of epoch corresponding to ndethist [days]
     */
    public Double getJdendhist();

    /**
     * Gets the value of the 'tooflag' field.
     * @return 1 => candidate is from a Target-of-Opportunity (ToO) exposure; 0 => candidate is from a non-ToO exposure
     */
    public Integer getTooflag();

    /**
     * Gets the value of the 'objectidps1' field.
     * @return Object ID of closest source from PS1 catalog; if exists within 30 arcsec
     */
    public Long getObjectidps1();

    /**
     * Gets the value of the 'objectidps2' field.
     * @return Object ID of second closest source from PS1 catalog; if exists within 30 arcsec
     */
    public Long getObjectidps2();

    /**
     * Gets the value of the 'sgmag2' field.
     * @return g-band PSF-fit magnitude of second closest source from PS1 catalog; if exists within 30 arcsec [mag]
     */
    public Float getSgmag2();

    /**
     * Gets the value of the 'srmag2' field.
     * @return r-band PSF-fit magnitude of second closest source from PS1 catalog; if exists within 30 arcsec [mag]
     */
    public Float getSrmag2();

    /**
     * Gets the value of the 'simag2' field.
     * @return i-band PSF-fit magnitude of second closest source from PS1 catalog; if exists within 30 arcsec [mag]
     */
    public Float getSimag2();

    /**
     * Gets the value of the 'szmag2' field.
     * @return z-band PSF-fit magnitude of second closest source from PS1 catalog; if exists within 30 arcsec [mag]
     */
    public Float getSzmag2();

    /**
     * Gets the value of the 'sgscore2' field.
     * @return Star/Galaxy score of second closest source from PS1 catalog; if exists within 30 arcsec: 0 <= sgscore <= 1 where closer to 1 implies higher likelihood of being a star
     */
    public Float getSgscore2();

    /**
     * Gets the value of the 'distpsnr2' field.
     * @return Distance to second closest source from PS1 catalog; if exists within 30 arcsec [arcsec]
     */
    public Float getDistpsnr2();

    /**
     * Gets the value of the 'objectidps3' field.
     * @return Object ID of third closest source from PS1 catalog; if exists within 30 arcsec
     */
    public Long getObjectidps3();

    /**
     * Gets the value of the 'sgmag3' field.
     * @return g-band PSF-fit magnitude of third closest source from PS1 catalog; if exists within 30 arcsec [mag]
     */
    public Float getSgmag3();

    /**
     * Gets the value of the 'srmag3' field.
     * @return r-band PSF-fit magnitude of third closest source from PS1 catalog; if exists within 30 arcsec [mag]
     */
    public Float getSrmag3();

    /**
     * Gets the value of the 'simag3' field.
     * @return i-band PSF-fit magnitude of third closest source from PS1 catalog; if exists within 30 arcsec [mag]
     */
    public Float getSimag3();

    /**
     * Gets the value of the 'szmag3' field.
     * @return z-band PSF-fit magnitude of third closest source from PS1 catalog; if exists within 30 arcsec [mag]
     */
    public Float getSzmag3();

    /**
     * Gets the value of the 'sgscore3' field.
     * @return Star/Galaxy score of third closest source from PS1 catalog; if exists within 30 arcsec: 0 <= sgscore <= 1 where closer to 1 implies higher likelihood of being a star
     */
    public Float getSgscore3();

    /**
     * Gets the value of the 'distpsnr3' field.
     * @return Distance to third closest source from PS1 catalog; if exists within 30 arcsec [arcsec]
     */
    public Float getDistpsnr3();

    /**
     * Gets the value of the 'nmtchps' field.
     * @return Number of source matches from PS1 catalog falling within 30 arcsec
     */
    public Integer getNmtchps();

    /**
     * Gets the value of the 'rfid' field.
     * @return Processing ID for reference image to facilitate archive retrieval
     */
    public Long getRfid();

    /**
     * Gets the value of the 'jdstartref' field.
     * @return Observation Julian date of earliest exposure used to generate reference image [days]
     */
    public Double getJdstartref();

    /**
     * Gets the value of the 'jdendref' field.
     * @return Observation Julian date of latest exposure used to generate reference image [days]
     */
    public Double getJdendref();

    /**
     * Gets the value of the 'nframesref' field.
     * @return Number of frames (epochal images) used to generate reference image
     */
    public Integer getNframesref();


    /**
     * Gets the value of the 'dsnrms' field.
     * @return Ratio: D/stddev(D) on event position where D = difference image
     */
    public Float getDsnrms();

    /**
     * Gets the value of the 'ssnrms' field.
     * @return Ratio: S/stddev(S) on event position where S = image of convolution: D (x) PSF(D)
     */
    public Float getSsnrms();

    /**
     * Gets the value of the 'dsdiff' field.
     * @return Difference of statistics: dsnrms - ssnrms
     */
    public Float getDsdiff();

    /**
     * Gets the value of the 'magzpsci' field.
     * @return Magnitude zero point for photometry estimates [mag]
     */
    public Float getMagzpsci();

    /**
     * Gets the value of the 'magzpsciunc' field.
     * @return Magnitude zero point uncertainty (in magzpsci) [mag]
     */
    public Float getMagzpsciunc();

    /**
     * Gets the value of the 'magzpscirms' field.
     * @return RMS (deviation from average) in all differences between instrumental photometry and matched photometric calibrators from science image processing [mag]
     */
    public Float getMagzpscirms();

    /**
     * Gets the value of the 'nmatches' field.
     * @return Number of PS1 photometric calibrators used to calibrate science image from science image processing
     */
    public Integer getNmatches();

    /**
     * Gets the value of the 'clrcoeff' field.
     * @return Color coefficient from linear fit from photometric calibration of science image
     */
    public Float getClrcoeff();

    /**
     * Gets the value of the 'clrcounc' field.
     * @return Color coefficient uncertainty from linear fit (corresponding to clrcoeff)
     */
    public Float getClrcounc();

    /**
     * Gets the value of the 'zpclrcov' field.
     * @return Covariance in magzpsci and clrcoeff from science image processing [mag^2]
     */
    public Float getZpclrcov();

    /**
     * Gets the value of the 'zpmed' field.
     * @return Magnitude zero point from median of all differences between instrumental photometry and matched photometric calibrators from science image processing [mag]
     */
    public Float getZpmed();

    /**
     * Gets the value of the 'clrmed' field.
     * @return Median color of all PS1 photometric calibrators used from science image processing [mag]: for filter (fid) = 1, 2, 3, PS1 color used = g-r, g-r, r-i respectively
     */
    public Float getClrmed();

    /**
     * Gets the value of the 'clrrms' field.
     * @return RMS color (deviation from average) of all PS1 photometric calibrators used from science image processing [mag]
     */
    public Float getClrrms();

    /**
     * Gets the value of the 'neargaia' field.
     * @return Distance to closest source from Gaia DR1 catalog irrespective of magnitude; if exists within 90 arcsec [arcsec]
     */
    public Float getNeargaia();

    /**
     * Gets the value of the 'neargaiabright' field.
     * @return Distance to closest source from Gaia DR1 catalog brighter than magnitude 14; if exists within 90 arcsec [arcsec]
     */
    public Float getNeargaiabright();

    /**
     * Gets the value of the 'maggaia' field.
     * @return Gaia (G-band) magnitude of closest source from Gaia DR1 catalog irrespective of magnitude; if exists within 90 arcsec [mag]
     */
    public Float getMaggaia();

    /**
     * Gets the value of the 'maggaiabright' field.
     * @return Gaia (G-band) magnitude of closest source from Gaia DR1 catalog brighter than magnitude 14; if exists within 90 arcsec [mag]
     */
    public Float getMaggaiabright();

    }
