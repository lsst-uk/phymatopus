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
package uk.ac.roe.wfau.phymatopus.alert;

public interface BaseCandidate
    {
    /**
     * Gets the value of the 'candid' field.
     * @return Candidate ID from operations DB
     */
    public java.lang.Long getCandid();

    /**
     * Gets the value of the 'objectId' field.
     * @return object identifier or name
     */
    public CharSequence getObjectId();
    
    /**
     * Gets the value of the 'jd' field.
     * @return Observation Julian date at start of exposure [days]
     */
    public java.lang.Double getJd();

    /**
     * Gets the value of the 'fid' field.
     * @return Filter ID (1=g; 2=R; 3=i)
     */
    public java.lang.Integer getFid();

    /**
     * Gets the value of the 'pid' field.
     * @return Processing ID for science image to facilitate archive retrieval
     */
    public java.lang.Long getPid();

    /**
     * Gets the value of the 'diffmaglim' field.
     * @return Expected 5-sigma mag limit in difference image based on global noise estimate [mag]
     */
    public java.lang.Float getDiffmaglim();

    /**
     * Gets the value of the 'pdiffimfilename' field.
     * @return filename of positive (sci minus ref) difference image
     */
    public java.lang.CharSequence getPdiffimfilename();

    /**
     * Gets the value of the 'programpi' field.
     * @return Principal investigator attached to program ID
     */
    public java.lang.CharSequence getProgrampi();

    /**
     * Gets the value of the 'programid' field.
     * @return Program ID: encodes either public, collab, or caltech mode
     */
    public java.lang.Integer getProgramid();

    /**
     * Gets the value of the 'isdiffpos' field.
     * @return t or 1 => candidate is from positive (sci minus ref) subtraction; f or 0 => candidate is from negative (ref minus sci) subtraction
     */
    public java.lang.CharSequence getIsdiffpos();

    /**
     * Gets the value of the 'tblid' field.
     * @return Internal pipeline table extraction ID
     */
    public java.lang.Long getTblid();

    /**
     * Gets the value of the 'nid' field.
     * @return Night ID
     */
    public java.lang.Integer getNid();

    /**
     * Gets the value of the 'rcid' field.
     * @return Readout channel ID [00 .. 63]
     */
    public java.lang.Integer getRcid();

    /**
     * Gets the value of the 'field' field.
     * @return ZTF field ID
     */
    public java.lang.Integer getField();

    /**
     * Gets the value of the 'xpos' field.
     * @return x-image position of candidate [pixels]
     */
    public java.lang.Float getXpos();

    /**
     * Gets the value of the 'ypos' field.
     * @return y-image position of candidate [pixels]
     */
    public java.lang.Float getYpos();

    /**
     * Gets the value of the 'ra' field.
     * @return Right Ascension of candidate; J2000 [deg]
     */
    public java.lang.Double getRa();

    /**
     * Gets the value of the 'dec' field.
     * @return Declination of candidate; J2000 [deg]
     */
    public java.lang.Double getDec();

    /**
     * Gets the value of the 'magpsf' field.
     * @return Magnitude from PSF-fit photometry [mag]
     */
    public java.lang.Float getMagpsf();

    /**
     * Gets the value of the 'sigmapsf' field.
     * @return 1-sigma uncertainty in magpsf [mag]
     */
    public java.lang.Float getSigmapsf();

    /**
     * Gets the value of the 'chipsf' field.
     * @return Reduced chi-square for PSF-fit
     */
    public java.lang.Float getChipsf();

    /**
     * Gets the value of the 'magap' field.
     * @return Aperture mag using 14 pixel diameter aperture [mag]
     */
    public java.lang.Float getMagap();

    /**
     * Gets the value of the 'sigmagap' field.
     * @return 1-sigma uncertainty in magap [mag]
     */
    public java.lang.Float getSigmagap();

    /**
     * Gets the value of the 'distnr' field.
     * @return distance to nearest source in reference image PSF-catalog [pixels]
     */
    public java.lang.Float getDistnr();

    /**
     * Gets the value of the 'magnr' field.
     * @return magnitude of nearest source in reference image PSF-catalog [mag]
     */
    public java.lang.Float getMagnr();

    /**
     * Gets the value of the 'sigmagnr' field.
     * @return 1-sigma uncertainty in magnr [mag]
     */
    public java.lang.Float getSigmagnr();

    /**
     * Gets the value of the 'chinr' field.
     * @return DAOPhot chi parameter of nearest source in reference image PSF-catalog
     */
    public java.lang.Float getChinr();

    /**
     * Gets the value of the 'sharpnr' field.
     * @return DAOPhot sharp parameter of nearest source in reference image PSF-catalog
     */
    public java.lang.Float getSharpnr();

    /**
     * Gets the value of the 'sky' field.
     * @return Local sky background estimate [DN]
     */
    public java.lang.Float getSky();

    /**
     * Gets the value of the 'magdiff' field.
     * @return Difference: magap - magpsf [mag]
     */
    public java.lang.Float getMagdiff();

    /**
     * Gets the value of the 'fwhm' field.
     * @return Full Width Half Max assuming a Gaussian core, from SExtractor [pixels]
     */
    public java.lang.Float getFwhm();

    /**
     * Gets the value of the 'classtar' field.
     * @return Star/Galaxy classification score from SExtractor
     */
    public java.lang.Float getClasstar();

    /**
     * Gets the value of the 'mindtoedge' field.
     * @return Distance to nearest edge in image [pixels]
     */
    public java.lang.Float getMindtoedge();

    /**
     * Gets the value of the 'magfromlim' field.
     * @return Difference: diffmaglim - magap [mag]
     */
    public java.lang.Float getMagfromlim();

    /**
     * Gets the value of the 'seeratio' field.
     * @return Ratio: difffwhm / fwhm
     */
    public java.lang.Float getSeeratio();

    /**
     * Gets the value of the 'aimage' field.
     * @return Windowed profile RMS afloat major axis from SExtractor [pixels]
     */
    public java.lang.Float getAimage();

    /**
     * Gets the value of the 'bimage' field.
     * @return Windowed profile RMS afloat minor axis from SExtractor [pixels]
     */
    public java.lang.Float getBimage();

    /**
     * Gets the value of the 'aimagerat' field.
     * @return Ratio: aimage / fwhm
     */
    public java.lang.Float getAimagerat();

    /**
     * Gets the value of the 'bimagerat' field.
     * @return Ratio: bimage / fwhm
     */
    public java.lang.Float getBimagerat();

    /**
     * Gets the value of the 'elong' field.
     * @return Ratio: aimage / bimage
     */
    public java.lang.Float getElong();

    /**
     * Gets the value of the 'nneg' field.
     * @return number of negative pixels in a 5 x 5 pixel stamp
     */
    public java.lang.Integer getNneg();

    /**
     * Gets the value of the 'nbad' field.
     * @return number of prior-tagged bad pixels in a 5 x 5 pixel stamp
     */
    public java.lang.Integer getNbad();

    /**
     * Gets the value of the 'rb' field.
     * @return RealBogus quality score; range is 0 to 1 where closer to 1 is more reliable
     */
    public java.lang.Float getRb();

    /**
     * Gets the value of the 'ssdistnr' field.
     * @return distance to nearest known solar system object if exists within 30 arcsec [arcsec]
     */
    public java.lang.Float getSsdistnr();

    /**
     * Gets the value of the 'ssmagnr' field.
     * @return magnitude of nearest known solar system object if exists within 30 arcsec (usually V-band from MPC archive) [mag]
     */
    public java.lang.Float getSsmagnr();

    /**
     * Gets the value of the 'ssnamenr' field.
     * @return name of nearest known solar system object if exists within 30 arcsec (from MPC archive)
     */
    public java.lang.CharSequence getSsnamenr();

    /**
     * Gets the value of the 'sumrat' field.
     * @return Ratio: sum(pixels) / sum(|pixels|) in a 5 x 5 pixel stamp where stamp is first median-filtered to mitigate outliers
     */
    public java.lang.Float getSumrat();

    /**
     * Gets the value of the 'magapbig' field.
     * @return Aperture mag using 18 pixel diameter aperture [mag]
     */
    public java.lang.Float getMagapbig();

    /**
     * Gets the value of the 'sigmagapbig' field.
     * @return 1-sigma uncertainty in magapbig [mag]
     */
    public java.lang.Float getSigmagapbig();

    /**
     * Gets the value of the 'ranr' field.
     * @return Right Ascension of nearest source in reference image PSF-catalog; J2000 [deg]
     */
    public java.lang.Double getRanr();

    /**
     * Gets the value of the 'decnr' field.
     * @return Declination of nearest source in reference image PSF-catalog; J2000 [deg]
     */
    public java.lang.Double getDecnr();

    /**
     * Gets the value of the 'scorr' field.
     * @return Peak-pixel signal-to-noise ratio in point source matched-filtered detection image
     */
    public java.lang.Double getScorr();

    /**
     * Gets the value of the 'rbversion' field.
     * @return version of RealBogus model/classifier used to assign rb quality score
     */
    public java.lang.CharSequence getRbversion();

    }
